package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Visit implements java.io.Serializable {
    private long visitId;
    private long patientId;
    private long requestId;
    private long dentistAccountId;
    private String source;
    private String status;
    private String patientName;
    private String patientPhone;
    private String diagnosis;
    private String services;
    private String medication;
    private String clinicalNotes;
    private Timestamp createdAt;
    public long getVisitId() { return visitId; }
    public void setVisitId(long v) { this.visitId = v; }
    public long getPatientId() { return patientId; }
    public void setPatientId(long v) { this.patientId = v; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long v) { this.requestId = v; }
    public long getDentistAccountId() { return dentistAccountId; }
    public void setDentistAccountId(long v) { this.dentistAccountId = v; }
    public String getSource() { return source; }
    public void setSource(String v) { this.source = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String v) { this.patientName = v; }
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String v) { this.patientPhone = v; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String v) { this.diagnosis = v; }
    public String getServices() { return services; }
    public void setServices(String v) { this.services = v; }
    public String getMedication() { return medication; }
    public void setMedication(String v) { this.medication = v; }
    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String v) { this.clinicalNotes = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
