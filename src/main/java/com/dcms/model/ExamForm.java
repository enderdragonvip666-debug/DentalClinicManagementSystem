package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ExamForm implements java.io.Serializable {
    private long examFormId;
    private long visitId;
    private String diagnosis;
    private String clinicalNotes;
    private String medication;
    private String status;
    private List<Long> serviceIds;
    public long getExamFormId() { return examFormId; }
    public void setExamFormId(long v) { this.examFormId = v; }
    public long getVisitId() { return visitId; }
    public void setVisitId(long v) { this.visitId = v; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String v) { this.diagnosis = v; }
    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String v) { this.clinicalNotes = v; }
    public String getMedication() { return medication; }
    public void setMedication(String v) { this.medication = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public List<Long> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Long> v) { this.serviceIds = v; }
}
