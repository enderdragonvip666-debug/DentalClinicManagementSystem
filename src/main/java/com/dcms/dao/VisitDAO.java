package com.dcms.dao;

import com.dcms.model.Visit;
import java.sql.*;
import java.util.List;

public class VisitDAO extends BaseDAO {
    public VisitDAO(Connection c) { super(c); }
    private static final String SVC = "(SELECT STRING_AGG(ds.name, ', ') FROM exam_form_services efs JOIN dental_services ds ON ds.service_id = efs.service_id WHERE efs.exam_form_id = ef.exam_form_id)";
    private static final String SEL = "SELECT v.*, p.full_name AS patient_name, p.phone AS patient_phone, ef.diagnosis, ef.clinical_notes, ef.medication, " + SVC + " AS services "
            + "FROM visits v JOIN patients p ON p.patient_id = v.patient_id LEFT JOIN exam_forms ef ON ef.visit_id = v.visit_id AND ef.status = 'ACTIVE' ";
    private static Visit map(ResultSet rs) throws SQLException {
        Visit v = new Visit();
        v.setVisitId(rs.getLong("visit_id")); v.setPatientId(rs.getLong("patient_id")); v.setRequestId(rs.getLong("request_id"));
        v.setDentistAccountId(rs.getLong("dentist_account_id")); v.setSource(rs.getString("source")); v.setStatus(rs.getString("status"));
        v.setPatientName(rs.getString("patient_name")); v.setPatientPhone(rs.getString("patient_phone"));
        v.setDiagnosis(rs.getString("diagnosis")); v.setClinicalNotes(rs.getString("clinical_notes")); v.setMedication(rs.getString("medication")); v.setServices(rs.getString("services"));
        v.setCreatedAt(rs.getTimestamp("created_at"));
        return v;
    }
    private static String like(String q) { return "%" + (q == null ? "" : q.trim()) + "%"; }

    public long insert(long patientId, long requestId, long dentistId, String source) throws SQLException {
        return insert("INSERT INTO visits (patient_id, request_id, dentist_account_id, source) VALUES (?,?,?,?)", patientId, requestId, dentistId, source);
    }
    public Visit findById(long id) throws SQLException { return one(SEL + "WHERE v.visit_id = ?", VisitDAO::map, id); }
    public Visit findByIdForUpdate(long id) throws SQLException {
        one("SELECT visit_id FROM visits WITH (UPDLOCK, ROWLOCK) WHERE visit_id = ?", rs -> 1, id);
        return findById(id);
    }
    public void setStatus(long id, String status) throws SQLException {
        exec("UPDATE visits SET status=?, updated_at=SYSDATETIME() WHERE visit_id=?", status, id);
    }
    public List<Visit> patientList(long dentistId, String q) throws SQLException {
        return query(SEL + "WHERE v.dentist_account_id = ? AND v.status IN ('WAITING','IN_PROGRESS') AND (p.full_name LIKE ? OR p.phone LIKE ?) ORDER BY v.created_at",
                VisitDAO::map, dentistId, like(q), like(q));
    }
    /** Appointments on one selected day. Pending bookings have no Visit yet and cannot be opened by dentists. */
    public List<com.dcms.model.DoctorCase> doctorCasesOnDay(long dentistId, java.time.LocalDate date, String q) throws SQLException {
        String sql = "SELECT r.request_code, r.full_name, r.phone, ds.name AS service_name, "
                + "v.visit_id, v.dentist_account_id, v.status AS visit_status, v.created_at AS checked_in_at "
                + "FROM consultation_requests r "
                + "JOIN dental_services ds ON ds.service_id = r.service_id "
                + "LEFT JOIN visits v ON v.request_id = r.request_id "
                + "WHERE r.appt_date = ? "
                + "AND ((r.status = 'REQUESTED' AND v.visit_id IS NULL) "
                + "OR (v.dentist_account_id = ? AND v.status IN ('WAITING','IN_PROGRESS'))) "
                + "AND (r.full_name LIKE ? OR r.phone LIKE ?) "
                + "ORDER BY CASE WHEN v.status = 'IN_PROGRESS' THEN 0 WHEN v.status = 'WAITING' THEN 1 "
                + "WHEN v.visit_id IS NULL THEN 2 ELSE 3 END, r.created_at";
        return query(sql, rs -> {
            com.dcms.model.DoctorCase d = new com.dcms.model.DoctorCase();
            d.setRequestCode(rs.getString("request_code"));
            d.setPatientName(rs.getString("full_name"));
            d.setPatientPhone(rs.getString("phone"));
            d.setServiceName(rs.getString("service_name"));
            d.setVisitId(rs.getLong("visit_id"));
            d.setCreatedAt(rs.getTimestamp("checked_in_at"));
            String status = rs.getString("visit_status");
            d.setStatus(status == null ? "REQUESTED" : status);
            long assignedId = rs.getLong("dentist_account_id");
            d.setCanOpen(d.getVisitId() > 0 && assignedId == dentistId
                    && ("WAITING".equals(status) || "IN_PROGRESS".equals(status)));
            return d;
        }, java.sql.Date.valueOf(date), dentistId, like(q), like(q));
    }

    /** Counts visible cases per day and shift for one dentist (excludes finished/paid cases). */
    public List<Object[]> doctorCaseCounts(long dentistId, java.time.LocalDate from, java.time.LocalDate to) throws SQLException {
        String sql = "SELECT r.appt_date, r.appt_shift, COUNT(*) "
                + "FROM consultation_requests r LEFT JOIN visits v ON v.request_id = r.request_id "
                + "WHERE r.appt_date BETWEEN ? AND ? "
                + "AND ((r.status = 'REQUESTED' AND v.visit_id IS NULL) "
                + "OR (v.dentist_account_id = ? AND v.status IN ('WAITING','IN_PROGRESS'))) "
                + "GROUP BY r.appt_date, r.appt_shift";
        return query(sql, rs -> new Object[]{rs.getDate(1).toLocalDate().toString(), rs.getString(2), rs.getInt(3)},
                java.sql.Date.valueOf(from), java.sql.Date.valueOf(to), dentistId);
    }

    public List<Visit> checkoutList(String q) throws SQLException {
        return query(SEL + "WHERE v.status = 'AWAITING_PAYMENT' AND (p.full_name LIKE ? OR p.phone LIKE ?) ORDER BY v.updated_at",
                VisitDAO::map, like(q), like(q));
    }
    /** Visits the patient has really been examined in (finished, with a saved exam form), excluding the current one, newest first. */
    public List<Visit> examHistoryOf(long patientId, long excludeVisitId) throws SQLException {
        return query(SEL + "WHERE v.patient_id = ? AND v.visit_id <> ? AND v.status IN ('AWAITING_PAYMENT','COMPLETED') AND ef.exam_form_id IS NOT NULL ORDER BY v.created_at DESC",
                VisitDAO::map, patientId, excludeVisitId);
    }
    /** The most recent finished visits of all patients (receptionist's default view of Treatment History). */
    public List<Visit> recentTreatmentHistory(int limit) throws SQLException {
        return query(SEL.replaceFirst("SELECT ", "SELECT TOP " + limit + " ") + "WHERE v.status IN ('AWAITING_PAYMENT','COMPLETED') AND ef.exam_form_id IS NOT NULL ORDER BY v.created_at DESC", VisitDAO::map);
    }
    /** Treatment history: finished visits with an active exam form. Deleted forms are never shown. */
    public List<Visit> treatmentHistory(long patientId) throws SQLException {
        return query(SEL + "WHERE v.patient_id = ? AND v.status IN ('AWAITING_PAYMENT','COMPLETED') AND ef.exam_form_id IS NOT NULL ORDER BY v.created_at DESC",
                VisitDAO::map, patientId);
    }
}
