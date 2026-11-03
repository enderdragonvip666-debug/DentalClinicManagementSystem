package com.dcms.integration;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The only external adapter (SDS package "integration"): sends the password-reset email.
 * Configure with env vars DCMS_SMTP_HOST, DCMS_SMTP_PORT, DCMS_SMTP_USER, DCMS_SMTP_PASSWORD, DCMS_SMTP_FROM.
 * Without DCMS_SMTP_HOST the reset link is written to the server log (development mode).
 */
public class EmailNotificationClient {
    private static final Logger LOG = Logger.getLogger(EmailNotificationClient.class.getName());
    private static String env(String k, String d) { String v = System.getenv(k); return v == null || v.isEmpty() ? d : v; }

    /** @return true when the mail was handed to the provider; failure is logged and returns false. */
    public boolean sendPasswordReset(String to, String fullName, String link) {
        String host = env("DCMS_SMTP_HOST", "");
        String body = "Xin chào " + fullName + ",\n\nVui lòng dùng liên kết bên dưới để đặt mật khẩu mới. Liên kết chỉ dùng được một lần và hết hạn sau 30 phút.\n\n" + link
                + "\n\nNếu bạn không yêu cầu việc này, vui lòng bỏ qua email này.\n\nPhòng khám nha khoa";
        if (host.isEmpty()) { LOG.warning("[DEV] SMTP not configured. Reset link for " + to + ": " + link); return true; }
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", host); p.put("mail.smtp.port", env("DCMS_SMTP_PORT", "587"));
            p.put("mail.smtp.auth", "true"); p.put("mail.smtp.starttls.enable", "true");
            String user = env("DCMS_SMTP_USER", ""), pass = env("DCMS_SMTP_PASSWORD", "");
            Session s = Session.getInstance(p, new Authenticator() {
                @Override protected PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(user, pass); }
            });
            MimeMessage m = new MimeMessage(s);
            m.setFrom(new InternetAddress(env("DCMS_SMTP_FROM", user)));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject("Đặt lại mật khẩu của bạn"); m.setText(body, "UTF-8");
            Transport.send(m);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Password-reset email failed", e);
            return false;
        }
    }
}
