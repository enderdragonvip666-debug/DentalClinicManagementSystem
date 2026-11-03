package com.dcms.model;

import java.sql.Timestamp;

/** One appointment in a dentist's daily patient table (booked or already checked in). */
public class DoctorCase {
    private String patientName, patientPhone, serviceName, status, requestCode;
    private Timestamp createdAt; // visit creation time; null until check-in
    private long visitId;
    private boolean canOpen;
    public String getPatientName() { return patientName; }
    public void setPatientName(String value) { patientName = value; }
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String value) { patientPhone = value; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String value) { serviceName = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getRequestCode() { return requestCode; }
    public void setRequestCode(String value) { requestCode = value; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp value) { createdAt = value; }
    public long getVisitId() { return visitId; }
    public void setVisitId(long value) { visitId = value; }
    public boolean isCanOpen() { return canOpen; }
    public void setCanOpen(boolean value) { canOpen = value; }
}
