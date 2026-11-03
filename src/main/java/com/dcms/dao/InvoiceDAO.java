package com.dcms.dao;

import com.dcms.model.Invoice;
import com.dcms.model.InvoiceItem;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class InvoiceDAO extends BaseDAO {
    public InvoiceDAO(Connection c) { super(c); }
    private static Invoice map(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setInvoiceId(rs.getLong("invoice_id")); i.setVisitId(rs.getLong("visit_id")); i.setTotal(rs.getBigDecimal("total"));
        i.setStatus(rs.getString("status")); i.setCreatedAt(rs.getTimestamp("created_at"));
        return i;
    }
    public Invoice findByVisit(long visitId) throws SQLException {
        Invoice i = one("SELECT * FROM invoices WHERE visit_id = ?", InvoiceDAO::map, visitId);
        if (i != null) i.setItems(items(i.getInvoiceId()));
        return i;
    }
    public long insert(long visitId, BigDecimal total) throws SQLException {
        return insert("INSERT INTO invoices (visit_id, total) VALUES (?,?)", visitId, total);
    }
    public void insertItem(long invoiceId, long serviceId, BigDecimal fee) throws SQLException {
        exec("INSERT INTO invoice_items (invoice_id, service_id, fee_at_billing) VALUES (?,?,?)", invoiceId, serviceId, fee);
    }
    public List<InvoiceItem> items(long invoiceId) throws SQLException {
        return query("SELECT ii.*, s.name AS service_name FROM invoice_items ii JOIN dental_services s ON s.service_id = ii.service_id WHERE ii.invoice_id = ? ORDER BY s.name",
                rs -> { InvoiceItem it = new InvoiceItem();
                    it.setInvoiceItemId(rs.getLong("invoice_item_id")); it.setInvoiceId(rs.getLong("invoice_id"));
                    it.setServiceId(rs.getLong("service_id")); it.setServiceName(rs.getString("service_name"));
                    it.setFeeAtBilling(rs.getBigDecimal("fee_at_billing")); return it; }, invoiceId);
    }
    public void markPaid(long invoiceId) throws SQLException {
        exec("UPDATE invoices SET status='PAID', updated_at=SYSDATETIME() WHERE invoice_id=?", invoiceId);
    }
}
