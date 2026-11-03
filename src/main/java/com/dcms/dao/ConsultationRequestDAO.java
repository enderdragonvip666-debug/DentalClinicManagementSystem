package com.dcms.dao;

import com.dcms.model.ConsultationRequest;
import java.sql.*;
import java.util.List;

public class ConsultationRequestDAO extends BaseDAO {
    public ConsultationRequestDAO(Connection c) { super(c); }
    private static final String SEL = "SELECT q.*, s.name AS service_name, COALESCE(v.status, q.status) AS display_status, v.visit_id AS linked_visit_id "
            + "FROM consultation_requests q JOIN dental_services s ON s.service_id = q.service_id "
            + "LEFT JOIN visits v ON v.request_id = q.request_id ";
    private static ConsultationRequest map(ResultSet rs) throws SQLException {
        ConsultationRequest r = new ConsultationRequest();
        r.setRequestId(rs.getLong("request_id")); r.setRequestCode(rs.getString("request_code"));
        r.setFullName(rs.getString("full_name")); r.setPhone(rs.getString("phone"));
        r.setServiceId(rs.getLong("service_id")); r.setServiceName(rs.getString("service_name"));
        r.setApptDate(rs.getDate("appt_date")); r.setShift(rs.getString("appt_shift"));
        r.setNote(rs.getString("note")); r.setPatientId(lng(rs, "patient_id"));
        r.setStatus(rs.getString("status")); r.setDisplayStatus(rs.getString("display_status"));
        long visitId = rs.getLong("linked_visit_id");
        r.setVisitId(rs.wasNull() ? null : visitId);
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
    private static final String ORD = " ORDER BY q.appt_date, CASE q.appt_shift WHEN 'MORNING' THEN 0 ELSE 1 END, q.created_at";
    public long insert(String code, String name, String phone, long serviceId, String note, java.time.LocalDate date, String shift) throws SQLException {
        return insert("INSERT INTO consultation_requests (request_code, full_name, phone, service_id, note, appt_date, appt_shift) VALUES (?,?,?,?,?,?,?)",
                code, name, phone, serviceId, note, java.sql.Date.valueOf(date), shift);
    }
    public ConsultationRequest findById(long id) throws SQLException { return one(SEL + "WHERE q.request_id = ?", ConsultationRequestDAO::map, id); }
    /** Locks the row so two check-ins of the same request cannot both succeed (DBR-07). */
    public ConsultationRequest findByIdForUpdate(long id) throws SQLException {
        return one("SELECT q.*, s.name AS service_name, COALESCE(v.status, q.status) AS display_status, v.visit_id AS linked_visit_id FROM consultation_requests q WITH (UPDLOCK, ROWLOCK) JOIN dental_services s ON s.service_id = q.service_id LEFT JOIN visits v ON v.request_id = q.request_id WHERE q.request_id = ?", ConsultationRequestDAO::map, id);
    }
    public ConsultationRequest findOpenByPhone(String phone) throws SQLException {
        return one(SEL + "WHERE q.phone = ? AND q.status = 'REQUESTED'", ConsultationRequestDAO::map, phone);
    }
    /** Only SQL fragments from fixed status names are ever appended (no user-supplied SQL). */
    private static String statusFilter(String status) {
        switch (status) {
            case "REQUESTED": return " AND q.status = 'REQUESTED' AND v.visit_id IS NULL";
            case "WAITING": return " AND v.status = 'WAITING'";
            case "IN_PROGRESS": return " AND v.status = 'IN_PROGRESS'";
            case "AWAITING_PAYMENT": return " AND v.status = 'AWAITING_PAYMENT'";
            default: return " AND (q.status = 'REQUESTED' OR v.status IN ('WAITING','IN_PROGRESS','AWAITING_PAYMENT'))";
        }
    }
    /** Daily receptionist schedule filtered by the real workflow state. */
    public List<ConsultationRequest> listOpenOn(java.time.LocalDate day, String status) throws SQLException {
        return query(SEL + "WHERE q.status IN ('REQUESTED','ARRIVED') AND q.appt_date = ?"
                + statusFilter(status) + ORD, ConsultationRequestDAO::map, java.sql.Date.valueOf(day));
    }
    /** Search all dates by booking code/phone, also respecting the selected workflow status. */
    public List<ConsultationRequest> searchOpen(String qstr, String status) throws SQLException {
        String like = "%" + (qstr == null ? "" : qstr.trim()) + "%";
        return query(SEL + "WHERE q.status IN ('REQUESTED','ARRIVED') AND (q.request_code LIKE ? OR q.phone LIKE ?)"
                + statusFilter(status) + ORD, ConsultationRequestDAO::map, like, like);
    }
    /** Still-open bookings of days that are already over (no-shows nobody has closed). */
    public List<ConsultationRequest> listOverdue(java.time.LocalDate today) throws SQLException {
        return query(SEL + "WHERE q.status = 'REQUESTED' AND q.appt_date < ?" + ORD, ConsultationRequestDAO::map, java.sql.Date.valueOf(today));
    }
    /** {iso date, shift, count} for appointments matching receptionist's current filter. */
    public List<Object[]> counts(java.time.LocalDate from, java.time.LocalDate to, String status) throws SQLException {
        return query("SELECT q.appt_date, q.appt_shift, COUNT(*) "
                + "FROM consultation_requests q LEFT JOIN visits v ON v.request_id = q.request_id "
                + "WHERE q.status IN ('REQUESTED','ARRIVED') AND q.appt_date BETWEEN ? AND ? "
                + statusFilter(status) + " GROUP BY q.appt_date, q.appt_shift",
                rs -> new Object[]{rs.getDate(1).toLocalDate().toString(), rs.getString(2), rs.getInt(3)},
                java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));
    }
    /** Date and shift are updated together, so the appointment cannot end up partially moved. */
    public void reschedule(long id, java.time.LocalDate day, String shift) throws SQLException {
        exec("UPDATE consultation_requests SET appt_date=?, appt_shift=?, updated_at=SYSDATETIME() WHERE request_id=?", java.sql.Date.valueOf(day), shift, id);
    }
    public List<ConsultationRequest> listByPhone(String phone) throws SQLException {
        return query(SEL + "WHERE q.phone = ? ORDER BY q.appt_date DESC, q.created_at DESC", ConsultationRequestDAO::map, phone);
    }
    public void setStatus(long id, String status, Long patientId) throws SQLException {
        exec("UPDATE consultation_requests SET status=?, patient_id=COALESCE(?, patient_id), updated_at=SYSDATETIME() WHERE request_id=?", status, patientId, id);
    }
}
