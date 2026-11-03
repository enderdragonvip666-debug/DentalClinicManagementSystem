package com.dcms.util;

import com.dcms.dao.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.math.BigDecimal;
import java.sql.Statement;
import java.util.logging.Logger;

/** First start only: creates demo staff accounts and sample services when the tables are empty. */
@WebListener
public class Bootstrap implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(Bootstrap.class.getName());
    /** Idempotent schema upgrade for booking by date + shift and the patient's medical history (same as db/migration_booking.sql). */
    private static void migrate() {
        DB.tx(c -> {
            try (Statement s = c.createStatement()) {
                s.execute("IF COL_LENGTH('consultation_requests','appt_date') IS NULL ALTER TABLE consultation_requests ADD appt_date DATE NULL");
                s.execute("IF COL_LENGTH('consultation_requests','appt_shift') IS NULL ALTER TABLE consultation_requests ADD appt_shift VARCHAR(10) NULL");
                s.execute("IF COL_LENGTH('patients','medical_history') IS NULL ALTER TABLE patients ADD medical_history NVARCHAR(1000) NULL");
                s.executeUpdate("UPDATE consultation_requests SET appt_date = CAST(created_at AS DATE) WHERE appt_date IS NULL");
                s.executeUpdate("UPDATE consultation_requests SET appt_shift = 'MORNING' WHERE appt_shift IS NULL");
            }
            return null;
        });
    }

    @Override public void contextInitialized(ServletContextEvent e) {
        try { migrate(); } catch (Exception ex) { LOG.warning("Schema upgrade skipped: " + ex.getMessage()); }
        try {
            DB.tx(c -> {
                AccountDAO ad = new AccountDAO(c);
                if (ad.count() == 0) {
                    ad.insert(ad.roleId("Administrator"), "0900000001", PasswordUtil.hash("Admin@123"), null, "System Administrator", "ACTIVE");
                    ad.insert(ad.roleId("Dentist"), "0900000002", PasswordUtil.hash("Dentist@123"), null, "Dr. Nguyen Van A", "ACTIVE");
                    ad.insert(ad.roleId("Dental Assistant"), "0900000003", PasswordUtil.hash("Assistant@123"), null, "Tran Thi B", "ACTIVE");
                    LOG.warning("Demo accounts created: 0900000001/Admin@123, 0900000002/Dentist@123, 0900000003/Assistant@123 - change these passwords.");
                }
                DentalServiceDAO sd = new DentalServiceDAO(c);
                if (sd.count() == 0) {
                    sd.insert("Dental check-up", new BigDecimal("100000"), "ACTIVE");
                    sd.insert("Teeth cleaning", new BigDecimal("150000"), "ACTIVE");
                    sd.insert("Tooth filling", new BigDecimal("300000"), "ACTIVE");
                    sd.insert("Tooth extraction", new BigDecimal("400000"), "ACTIVE");
                    sd.insert("Teeth whitening", new BigDecimal("1200000"), "ACTIVE");
                }
                return null;
            });
        } catch (Exception ex) { LOG.warning("Bootstrap skipped (is the dcmsv2 schema created?): " + ex.getMessage()); }
    }
}
