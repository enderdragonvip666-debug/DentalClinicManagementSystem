package com.dcms.dao;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentDAO extends BaseDAO {
    public PaymentDAO(Connection c) { super(c); }
    public long insert(long invoiceId, long recordedBy, BigDecimal amount) throws SQLException {
        return insert("INSERT INTO payments (invoice_id, recorded_by_account_id, amount) VALUES (?,?,?)", invoiceId, recordedBy, amount);
    }
    public boolean existsForInvoice(long invoiceId) throws SQLException {
        return one("SELECT 1 FROM payments WHERE invoice_id = ?", rs -> 1, invoiceId) != null;
    }
}
