package com.dcms.util;

import java.sql.Connection;
import java.sql.DriverManager;

/** JDBC connection and transaction management (SDS: JDBC component). Config via env vars or system properties. */
public final class DB {
    public interface Work<T> { T run(Connection c) throws Exception; }
    private DB() {}

    private static String cfg(String k, String def) {
        String v = System.getenv(k);
        if (v == null) v = System.getProperty(k);
        return v == null ? def : v;
    }

    public static Connection get() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = cfg("DCMS_DB_URL", "jdbc:sqlserver://localhost:1433;databaseName=dcmsv2;encrypt=true;trustServerCertificate=true");
        return DriverManager.getConnection(url, cfg("DCMS_DB_USER", "sa"), cfg("DCMS_DB_PASSWORD", "123456"));
    }

    public static <T> T read(Work<T> w) {
        try (Connection c = get()) { return w.run(c); }
        catch (BizException e) { throw e; }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static <T> T tx(Work<T> w) {
        try (Connection c = get()) {
            c.setAutoCommit(false);
            try { T r = w.run(c); c.commit(); return r; }
            catch (Exception e) { c.rollback(); throw e; }
        } catch (BizException e) { throw e; }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
