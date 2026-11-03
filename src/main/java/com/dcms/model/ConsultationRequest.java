package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ConsultationRequest implements java.io.Serializable {
    private long requestId;
    private Long visitId; // null before patient check-in
    private String requestCode;
    private String fullName;
    private String phone;
    private long serviceId;
    private String serviceName;
    private String note;
    private Long patientId;
    private java.sql.Date apptDate;
    private String shift;
    private String status;
    private String displayStatus; // Visit status after check-in; booking status before check-in
    private Timestamp createdAt;
    public long getRequestId() { return requestId; }
    public Long getVisitId() { return visitId; }
    public void setVisitId(Long id) { this.visitId = id; }
    public void setRequestId(long v) { this.requestId = v; }
    public String getRequestCode() { return requestCode; }
    public void setRequestCode(String v) { this.requestCode = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public long getServiceId() { return serviceId; }
    public void setServiceId(long v) { this.serviceId = v; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String v) { this.serviceName = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long v) { this.patientId = v; }
    public java.sql.Date getApptDate() { return apptDate; }
    public void setApptDate(java.sql.Date v) { this.apptDate = v; }
    public String getShift() { return shift; }
    public void setShift(String v) { this.shift = v; }
    /** yyyy-MM-dd, used in hidden fields and links. */
    public String getApptDateIso() { return apptDate == null ? "" : apptDate.toLocalDate().toString(); }
    /** dd/MM/yyyy, for display (avoids time-zone surprises of fmt:formatDate on a DATE). */
    public String getApptDateText() { return apptDate == null ? "" : apptDate.toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getDisplayStatus() { return displayStatus == null ? status : displayStatus; }
    public void setDisplayStatus(String v) { this.displayStatus = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
