package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Account implements java.io.Serializable {
    private long accountId;
    private long roleId;
    private String roleName;
    private String phone;
    private String passwordHash;
    private String email;
    private String resetTokenHash;
    private Timestamp resetTokenExpiresAt;
    private String fullName;
    private String status;
    private Timestamp createdAt;
    public long getAccountId() { return accountId; }
    public void setAccountId(long v) { this.accountId = v; }
    public long getRoleId() { return roleId; }
    public void setRoleId(long v) { this.roleId = v; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String v) { this.roleName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getResetTokenHash() { return resetTokenHash; }
    public void setResetTokenHash(String v) { this.resetTokenHash = v; }
    public Timestamp getResetTokenExpiresAt() { return resetTokenExpiresAt; }
    public void setResetTokenExpiresAt(Timestamp v) { this.resetTokenExpiresAt = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
