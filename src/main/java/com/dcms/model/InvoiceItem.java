package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class InvoiceItem implements java.io.Serializable {
    private long invoiceItemId;
    private long invoiceId;
    private long serviceId;
    private String serviceName;
    private BigDecimal feeAtBilling;
    public long getInvoiceItemId() { return invoiceItemId; }
    public void setInvoiceItemId(long v) { this.invoiceItemId = v; }
    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long v) { this.invoiceId = v; }
    public long getServiceId() { return serviceId; }
    public void setServiceId(long v) { this.serviceId = v; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String v) { this.serviceName = v; }
    public BigDecimal getFeeAtBilling() { return feeAtBilling; }
    public void setFeeAtBilling(BigDecimal v) { this.feeAtBilling = v; }
}
