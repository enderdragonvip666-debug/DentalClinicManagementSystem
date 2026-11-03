package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.integration.EmailNotificationClient;
import com.dcms.model.*;
import com.dcms.util.*;
import java.sql.Timestamp;

/** UC01-UC06: sign in/up, forgot/reset password, change password. */
public class AuthService {
    private final EmailNotificationClient mail = new EmailNotificationClient();

    /** DBR-27 / DBR-28. Wrong phone and wrong password give the same message. */
    public Account signIn(String phone, String password) {
        return DB.read(c -> {
            Account a = new AccountDAO(c).findByPhone(Validator.trim(phone));
            if (a == null || !PasswordUtil.matches(password, a.getPasswordHash())) throw new BizException(Msg.MSG07);
            if (!"ACTIVE".equals(a.getStatus())) throw new BizException(Msg.MSG08);
            return a;
        });
    }

    /** Creates a Patient account plus its linked Patient record (same name and phone, DBR-30). */
    public void signUp(String fullName, String phone, String email, String password, String confirm) {
        fullName = Validator.trim(fullName); phone = Validator.trim(phone); email = Validator.trim(email);
        if (Validator.blank(fullName) || fullName.length() < 2 || fullName.length() > 100) throw new BizException("Họ và tên phải từ 2 đến 100 ký tự.");
        Validator.phoneOk(phone); Validator.passwordOk(password, confirm);
        if (!Validator.blank(email) && (email.length() > 150 || !email.contains("@"))) throw new BizException("Vui lòng nhập địa chỉ email hợp lệ.");
        final String n = fullName, p = phone, e = Validator.blank(email) ? null : email;
        DB.tx(c -> {
            AccountDAO ad = new AccountDAO(c);
            if (ad.findByPhone(p) != null) throw new BizException(Msg.MSG09);
            long accId = ad.insert(ad.roleId("Patient"), p, PasswordUtil.hash(password), e, n, "ACTIVE");
            linkOrCreatePatient(c, accId, n, p);
            return null;
        });
    }

    static void linkOrCreatePatient(java.sql.Connection c, long accountId, String name, String phone) throws Exception {
        PatientDAO pd = new PatientDAO(c);
        Patient existing = pd.findByPhone(phone);       // created earlier at check-in (DBR-01)
        if (existing == null) pd.insert(accountId, name, phone);
        else if (existing.getAccountId() == null) pd.linkAccount(existing.getPatientId(), accountId);
    }

    /** UC04. MSG12 is always returned for unknown phones; MSG11 only when the account exists without email. */
    public void requestReset(String phone, String baseUrl) {
        DB.tx(c -> {
            AccountDAO ad = new AccountDAO(c);
            Account a = Validator.phone(phone) ? ad.findByPhone(phone.trim()) : null;
            if (a == null || !"ACTIVE".equals(a.getStatus())) return null;
            if (Validator.blank(a.getEmail())) throw new BizException(Msg.MSG11);
            String token = PasswordUtil.newToken();
            ad.setResetToken(a.getAccountId(), PasswordUtil.sha256(token), new Timestamp(System.currentTimeMillis() + 30L * 60 * 1000));
            if (!mail.sendPasswordReset(a.getEmail(), a.getFullName(), baseUrl + "/reset?token=" + token))
                throw new BizException("Không thể gửi email. Mật khẩu của bạn chưa bị thay đổi. Vui lòng thử lại sau.");
            return null;
        });
    }

    /** UC05 / DBR-31: valid, unused, not older than 30 minutes. */
    public Account verifyReset(String token) {
        return DB.read(c -> {
            Account a = Validator.blank(token) ? null : new AccountDAO(c).findByResetHash(PasswordUtil.sha256(token));
            if (a == null || a.getResetTokenExpiresAt() == null || a.getResetTokenExpiresAt().getTime() < System.currentTimeMillis())
                throw new BizException(Msg.MSG13);
            return a;
        });
    }

    public void resetPassword(String token, String password, String confirm) {
        Account a = verifyReset(token);
        Validator.passwordOk(password, confirm);
        DB.tx(c -> { new AccountDAO(c).updatePassword(a.getAccountId(), PasswordUtil.hash(password)); return null; });
    }

    public void changePassword(long accountId, String current, String password, String confirm) {
        DB.tx(c -> {
            AccountDAO ad = new AccountDAO(c);
            Account a = ad.findById(accountId);
            if (a == null || !PasswordUtil.matches(current, a.getPasswordHash())) throw new BizException("Mật khẩu hiện tại không đúng.");
            Validator.passwordOk(password, confirm);
            if (password.equals(current)) throw new BizException(Msg.MSG15);
            ad.updatePassword(accountId, PasswordUtil.hash(password));
            return null;
        });
    }
}
