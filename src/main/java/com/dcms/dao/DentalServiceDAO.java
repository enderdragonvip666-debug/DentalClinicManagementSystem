package com.dcms.dao;

import com.dcms.model.DentalService;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class DentalServiceDAO extends BaseDAO {
    public DentalServiceDAO(Connection c) { super(c); }
    private static DentalService map(ResultSet rs) throws SQLException {
        DentalService s = new DentalService();
        s.setServiceId(rs.getLong("service_id")); s.setName(rs.getString("name"));
        s.setFee(rs.getBigDecimal("fee")); s.setStatus(rs.getString("status"));
        return s;
    }
    public List<DentalService> listActive() throws SQLException { return query("SELECT * FROM dental_services WHERE status='ACTIVE' ORDER BY name", DentalServiceDAO::map); }
    public List<DentalService> listAll() throws SQLException { return query("SELECT * FROM dental_services ORDER BY name", DentalServiceDAO::map); }
    public DentalService findById(long id) throws SQLException { return one("SELECT * FROM dental_services WHERE service_id=?", DentalServiceDAO::map, id); }
    public boolean nameExists(String name, long excludeId) throws SQLException {
        return one("SELECT 1 FROM dental_services WHERE name=? AND service_id<>?", rs -> 1, name, excludeId) != null;
    }
    public long insert(String name, BigDecimal fee, String status) throws SQLException {
        return insert("INSERT INTO dental_services (name, fee, status) VALUES (?,?,?)", name, fee, status);
    }
    public void update(long id, String name, BigDecimal fee, String status) throws SQLException {
        exec("UPDATE dental_services SET name=?, fee=?, status=?, updated_at=SYSDATETIME() WHERE service_id=?", name, fee, status, id);
    }
    public long count() throws SQLException { return one("SELECT COUNT(*) FROM dental_services", rs -> rs.getLong(1)); }
}
