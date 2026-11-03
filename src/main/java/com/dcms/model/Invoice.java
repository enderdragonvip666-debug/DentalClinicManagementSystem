package com.dcms.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Invoice implements java.io.Serializable {
    private long invoiceId;
    private long visitId;
    private BigDecimal total;
    private String status;
    private Timestamp createdAt;
    private List<InvoiceItem> items;
    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long v) { this.invoiceId = v; }
    public long getVisitId() { return visitId; }
    public void setVisitId(long v) { this.visitId = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { this.total = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> v) { this.items = v; }
}
