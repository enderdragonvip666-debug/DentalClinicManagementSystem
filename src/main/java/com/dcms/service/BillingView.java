package com.dcms.service;

import com.dcms.model.*;
import java.math.BigDecimal;
import java.util.List;

/** Read model for the Invoice and Payment screen. */
public class BillingView {
    private final Visit visit; private final Invoice invoice; private final List<InvoiceItem> lines; private final BigDecimal total; private final ExamForm examForm;
    public BillingView(Visit visit, Invoice invoice, List<InvoiceItem> lines, BigDecimal total, ExamForm examForm) {
        this.visit = visit; this.invoice = invoice; this.lines = lines; this.total = total; this.examForm = examForm;
    }
    public Visit getVisit() { return visit; }
    public Invoice getInvoice() { return invoice; }
    public List<InvoiceItem> getLines() { return lines; }
    public BigDecimal getTotal() { return total; }
    public ExamForm getExamForm() { return examForm; }
    public boolean isInvoiced() { return invoice != null; }
    public boolean isPaid() { return invoice != null && "PAID".equals(invoice.getStatus()); }
    public String getStatus() { return invoice == null ? "NOT PRINTED" : invoice.getStatus(); }
}
