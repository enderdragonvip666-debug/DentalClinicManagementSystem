package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Patient implements java.io.Serializable {
    private long patientId;
    private Long accountId;
    private String fullName;
    private String phone;
    public long getPatientId() { return patientId; }
    public void setPatientId(long v) { this.patientId = v; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long v) { this.accountId = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
}
