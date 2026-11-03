package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class DentalService implements java.io.Serializable {
    private long serviceId;
    private String name;
    private BigDecimal fee;
    private String status;
    public long getServiceId() { return serviceId; }
    public void setServiceId(long v) { this.serviceId = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal v) { this.fee = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}
