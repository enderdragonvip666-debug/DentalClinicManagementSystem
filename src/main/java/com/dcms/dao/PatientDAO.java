package com.dcms.dao;

import com.dcms.model.Patient;
import java.sql.*;
import java.util.List;

public class PatientDAO extends BaseDAO {
    public PatientDAO(Connection c) { super(c); }
    private static Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getLong("patient_id")); p.setAccountId(lng(rs, "account_id"));
        p.setFullName(rs.getString("full_name")); p.setPhone(rs.getString("phone"));
        return p;
    }
    public Patient findByPhone(String phone) throws SQLException { return one("SELECT * FROM patients WHERE phone = ?", PatientDAO::map, phone); }
    public Patient findByAccountId(long id) throws SQLException { return one("SELECT * FROM patients WHERE account_id = ?", PatientDAO::map, id); }
    public Patient findById(long id) throws SQLException { return one("SELECT * FROM patients WHERE patient_id = ?", PatientDAO::map, id); }
    public List<Patient> search(String q) throws SQLException {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        return query("SELECT * FROM patients WHERE full_name LIKE ? OR phone LIKE ? ORDER BY full_name", PatientDAO::map, like, like);
    }
    public long insert(Long accountId, String fullName, String phone) throws SQLException {
        return insert("INSERT INTO patients (account_id, full_name, phone) VALUES (?,?,?)", accountId, fullName, phone);
    }
    /** Background / medical history typed by the Dentist (allergies, chronic illness, past dental treatment). */
    public String medicalHistory(long patientId) throws SQLException { return one("SELECT medical_history FROM patients WHERE patient_id = ?", rs -> rs.getString(1), patientId); }
    public void setMedicalHistory(long patientId, String text) throws SQLException {
        exec("UPDATE patients SET medical_history=?, updated_at=SYSDATETIME() WHERE patient_id=?", text, patientId);
    }
    public void linkAccount(long patientId, long accountId) throws SQLException {
        exec("UPDATE patients SET account_id=?, updated_at=SYSDATETIME() WHERE patient_id=?", accountId, patientId);
    }
}
