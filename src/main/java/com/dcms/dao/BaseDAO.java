package com.dcms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Small JDBC helper shared by all DAOs. A DAO works on the Connection it is given so services control transactions. */
public abstract class BaseDAO {
    public interface Mapper<T> { T map(ResultSet rs) throws SQLException; }
    protected final Connection c;
    protected BaseDAO(Connection c) { this.c = c; }

    private PreparedStatement prep(String sql, boolean keys, Object... p) throws SQLException {
        PreparedStatement ps = keys ? c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : c.prepareStatement(sql);
        for (int i = 0; i < p.length; i++) ps.setObject(i + 1, p[i]);
        return ps;
    }
    protected <T> List<T> query(String sql, Mapper<T> m, Object... p) throws SQLException {
        try (PreparedStatement ps = prep(sql, false, p); ResultSet rs = ps.executeQuery()) {
            List<T> out = new ArrayList<>();
            while (rs.next()) out.add(m.map(rs));
            return out;
        }
    }
    protected <T> T one(String sql, Mapper<T> m, Object... p) throws SQLException {
        List<T> l = query(sql, m, p);
        return l.isEmpty() ? null : l.get(0);
    }
    protected int exec(String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = prep(sql, false, p)) { return ps.executeUpdate(); }
    }
    protected long insert(String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = prep(sql, true, p)) {
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getLong(1); }
        }
    }
    protected static Long lng(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col); return rs.wasNull() ? null : v;
    }
}
