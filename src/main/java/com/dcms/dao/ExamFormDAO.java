package com.dcms.dao;

import com.dcms.model.DentalService;
import com.dcms.model.ExamForm;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class ExamFormDAO extends BaseDAO {
    public ExamFormDAO(Connection c) { super(c); }
    private static ExamForm map(ResultSet rs) throws SQLException {
        ExamForm f = new ExamForm();
        f.setExamFormId(rs.getLong("exam_form_id")); f.setVisitId(rs.getLong("visit_id"));
        f.setDiagnosis(rs.getString("diagnosis")); f.setClinicalNotes(rs.getString("clinical_notes"));
        f.setMedication(rs.getString("medication")); f.setStatus(rs.getString("status"));
        return f;
    }
    public ExamForm findActiveByVisit(long visitId) throws SQLException {
        ExamForm f = one("SELECT * FROM exam_forms WHERE visit_id = ? AND status = 'ACTIVE'", ExamFormDAO::map, visitId);
        if (f != null) f.setServiceIds(query("SELECT service_id FROM exam_form_services WHERE exam_form_id = ?", rs -> rs.getLong(1), f.getExamFormId()));
        return f;
    }
    public long insert(long visitId, String diagnosis, String notes, String medication) throws SQLException {
        return insert("INSERT INTO exam_forms (visit_id, diagnosis, clinical_notes, medication) VALUES (?,?,?,?)", visitId, diagnosis, notes, medication);
    }
    public void update(long id, String diagnosis, String notes, String medication) throws SQLException {
        exec("UPDATE exam_forms SET diagnosis=?, clinical_notes=?, medication=?, updated_at=SYSDATETIME() WHERE exam_form_id=?", diagnosis, notes, medication, id);
    }
    public void markDeleted(long id) throws SQLException {
        exec("UPDATE exam_forms SET status='DELETED', updated_at=SYSDATETIME() WHERE exam_form_id=?", id);
    }
    public void replaceServices(long examFormId, List<Long> serviceIds) throws SQLException {
        exec("DELETE FROM exam_form_services WHERE exam_form_id = ?", examFormId);
        for (Long sid : serviceIds) exec("INSERT INTO exam_form_services (exam_form_id, service_id) VALUES (?,?)", examFormId, sid);
    }
    /** Services of the exam form with their CURRENT fee - used for the invoice preview and the first print. */
    public List<DentalService> servicesWithCurrentFee(long examFormId) throws SQLException {
        return query("SELECT s.service_id, s.name, s.fee, s.status FROM exam_form_services e JOIN dental_services s ON s.service_id = e.service_id WHERE e.exam_form_id = ? ORDER BY s.name",
                rs -> { DentalService s = new DentalService(); s.setServiceId(rs.getLong(1)); s.setName(rs.getString(2)); s.setFee(rs.getBigDecimal(3)); s.setStatus(rs.getString(4)); return s; }, examFormId);
    }
}
