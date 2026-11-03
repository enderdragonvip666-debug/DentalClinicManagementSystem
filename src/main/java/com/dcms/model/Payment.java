package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Payment implements java.io.Serializable {
    private long paymentId;
    private long invoiceId;
    private long recordedByAccountId;
    private BigDecimal amount;
    private Timestamp createdAt;
    public long getPaymentId() { return paymentId; }
    public void setPaymentId(long v) { this.paymentId = v; }
    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long v) { this.invoiceId = v; }
    public long getRecordedByAccountId() { return recordedByAccountId; }
    public void setRecordedByAccountId(long v) { this.recordedByAccountId = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { this.amount = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
