package com.dcms.dao;

import com.dcms.model.Account;
import java.sql.*;
import java.util.List;

public class AccountDAO extends BaseDAO {
    public AccountDAO(Connection c) { super(c); }
    private static final String SEL = "SELECT a.*, r.role_name FROM accounts a JOIN roles r ON r.role_id = a.role_id ";
    private static Account map(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getLong("account_id")); a.setRoleId(rs.getLong("role_id")); a.setRoleName(rs.getString("role_name"));
        a.setPhone(rs.getString("phone")); a.setPasswordHash(rs.getString("password_hash")); a.setEmail(rs.getString("email"));
        a.setResetTokenHash(rs.getString("reset_token_hash")); a.setResetTokenExpiresAt(rs.getTimestamp("reset_token_expires_at"));
        a.setFullName(rs.getString("full_name")); a.setStatus(rs.getString("status")); a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
    public Account findByPhone(String phone) throws SQLException { return one(SEL + "WHERE a.phone = ?", AccountDAO::map, phone); }
    public Account findById(long id) throws SQLException { return one(SEL + "WHERE a.account_id = ?", AccountDAO::map, id); }
    public Account findByResetHash(String h) throws SQLException { return one(SEL + "WHERE a.reset_token_hash = ?", AccountDAO::map, h); }
    public List<Account> search(String q) throws SQLException {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        return query(SEL + "WHERE a.status <> 'DELETED' AND (a.full_name LIKE ? OR a.phone LIKE ?) ORDER BY a.account_id", AccountDAO::map, like, like);
    }
    public Long roleId(String roleName) throws SQLException {
        return one("SELECT role_id FROM roles WHERE role_name = ? AND status = 'ACTIVE'", rs -> rs.getLong(1), roleName);
    }
    public List<com.dcms.model.Account> roles() throws SQLException {
        return query("SELECT role_id, role_name FROM roles WHERE status='ACTIVE' ORDER BY role_id", rs -> {
            Account a = new Account(); a.setRoleId(rs.getLong(1)); a.setRoleName(rs.getString(2)); return a; });
    }
    public Account firstActiveWithRole(String roleName) throws SQLException {
        return one(SEL + "WHERE r.role_name = ? AND a.status = 'ACTIVE' ORDER BY a.account_id", AccountDAO::map, roleName);
    }
    /** Count ACTIVE administrators, not suspended or inactive accounts. */
    public long countActiveAdministrators() throws SQLException {
        return one("SELECT COUNT(*) FROM accounts a JOIN roles r ON r.role_id = a.role_id "
                + "WHERE r.role_name = 'Administrator' AND a.status = 'ACTIVE'", rs -> rs.getLong(1));
    }

    /**
     * Serialize changes to administrator membership using a normal SQL Server row lock.
     * Must be called after DB.tx() starts a transaction and before checking admin counts.
     * UPDLOCK + HOLDLOCK keep the role row locked until the transaction commits or rolls back,
     * so two concurrent requests cannot both demote the last two active administrators.
     * Unlike sp_getapplock, this requires only the existing SELECT permission on roles.
     */
    public void lockAdministratorChanges() throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT role_id FROM roles WITH (UPDLOCK, HOLDLOCK) WHERE role_name = ?")) {
            ps.setString(1, "Administrator");
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Không tìm thấy vai trò Administrator trong cơ sở dữ liệu.");
            }
        }
    }

    public long count() throws SQLException { return one("SELECT COUNT(*) FROM accounts", rs -> rs.getLong(1)); }
    public long insert(long roleId, String phone, String hash, String email, String fullName, String status) throws SQLException {
        return insert("INSERT INTO accounts (role_id, phone, password_hash, email, full_name, status) VALUES (?,?,?,?,?,?)",
                roleId, phone, hash, email, fullName, status);
    }
    public void updateByAdmin(long id, String fullName, String email, long roleId, String status) throws SQLException {
        exec("UPDATE accounts SET full_name=?, email=?, role_id=?, status=?, updated_at=SYSDATETIME() WHERE account_id=?", fullName, email, roleId, status, id);
    }
    /** Sets a new password and clears the one-time reset token (DBR-31). */
    public void updatePassword(long id, String hash) throws SQLException {
        exec("UPDATE accounts SET password_hash=?, reset_token_hash=NULL, reset_token_expires_at=NULL, updated_at=SYSDATETIME() WHERE account_id=?", hash, id);
    }
    public void setResetToken(long id, String hash, Timestamp expires) throws SQLException {
        exec("UPDATE accounts SET reset_token_hash=?, reset_token_expires_at=?, updated_at=SYSDATETIME() WHERE account_id=?", hash, expires, id);
    }
}
