package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.model.*;
import com.dcms.util.*;
import java.util.*;

/** UC08-UC10: Administrator manages accounts and fixed roles. */
public class AccountService {
    private static final Set<String> STATUSES = new LinkedHashSet<>(Arrays.asList("ACTIVE", "INACTIVE", "SUSPENDED"));
    public static Set<String> statuses() { return STATUSES; }

    public List<Account> search(String q) { return DB.read(c -> new AccountDAO(c).search(q)); }
    public List<Account> roles() { return DB.read(c -> new AccountDAO(c).roles()); }
    public Account find(long id) { return DB.read(c -> new AccountDAO(c).findById(id)); }
    public long countActiveAdministrators() { return DB.read(c -> new AccountDAO(c).countActiveAdministrators()); }

    /** Avoid using a stale Administrator session after a role/status change by another administrator. */
    private static void ensureCurrentAdministrator(AccountDAO ad, Account actor) throws java.sql.SQLException {
        if (actor == null) throw new BizException("Vui lòng đăng nhập với tài khoản quản trị viên.");
        Account current = ad.findById(actor.getAccountId());
        if (current == null || !"Administrator".equals(current.getRoleName()) || !"ACTIVE".equals(current.getStatus()))
            throw new BizException("Tài khoản không còn quyền quản trị. Vui lòng đăng nhập lại.");
    }

    private static String checkCommon(String fullName, String email, String role, String status) {
        Validator.required(fullName, "Họ và tên"); Validator.max(fullName, 150, "Họ và tên"); Validator.max(email, 150, "Email");
        if (!Validator.blank(email) && !email.contains("@")) throw new BizException("Vui lòng nhập địa chỉ email hợp lệ.");
        if (!STATUSES.contains(status)) throw new BizException("Vui lòng chọn trạng thái.");
        if (Validator.blank(role)) throw new BizException("Vui lòng chọn vai trò.");
        return Validator.blank(email) ? null : email.trim();
    }

    public void create(Account admin, String fullName, String phone, String email, String password, String roleName, String status) {
        fullName = Validator.trim(fullName); phone = Validator.trim(phone);
        String em = checkCommon(fullName, Validator.trim(email), roleName, status);
        Validator.phoneOk(phone); Validator.passwordOk(password, null);
        final String n = fullName, p = phone;
        DB.tx(c -> {
            AccountDAO ad = new AccountDAO(c);
            // Serialize all admin account writes, including adding a second administrator.
            ad.lockAdministratorChanges();
            ensureCurrentAdministrator(ad, admin);
            if (ad.findByPhone(p) != null) throw new BizException(Msg.MSG09);
            Long rid = ad.roleId(roleName);
            if (rid == null) throw new BizException("Vui lòng chọn vai trò.");
            long id = ad.insert(rid, p, PasswordUtil.hash(password), em, n, status);
            if ("Patient".equals(roleName)) AuthService.linkOrCreatePatient(c, id, n, p);
            return null;
        });
    }

    /** Phone is the sign-in identifier and equals the Patient phone (DBR-30), so it is not editable here. */
    public void update(Account admin, long id, String fullName, String email, String roleName, String status, String newPassword) {
        fullName = Validator.trim(fullName);
        String em = checkCommon(fullName, Validator.trim(email), roleName, status);
        if (admin == null) throw new BizException("Vui lòng đăng nhập với tài khoản quản trị viên.");
        if (admin.getAccountId() == id && !"ACTIVE".equals(status)) throw new BizException(Msg.MSG40);
        if (!Validator.blank(newPassword)) Validator.passwordOk(newPassword, null);
        final String n = fullName;
        DB.tx(c -> {
            AccountDAO ad = new AccountDAO(c);
            // One admin may leave the role if another ACTIVE admin remains.
            // Lock + count + update are within the SAME database transaction.
            ad.lockAdministratorChanges();
            ensureCurrentAdministrator(ad, admin);
            Account a = ad.findById(id);
            Long rid = ad.roleId(roleName);
            if (a == null || rid == null) throw new BizException("Không tìm thấy tài khoản.");
            boolean removesActiveAdmin = "Administrator".equals(a.getRoleName()) && "ACTIVE".equals(a.getStatus())
                    && (!"Administrator".equals(roleName) || !"ACTIVE".equals(status));
            if (removesActiveAdmin && ad.countActiveAdministrators() <= 1)
                throw new BizException("Phải luôn có ít nhất 1 quản trị viên đang hoạt động. Hãy tạo hoặc kích hoạt quản trị viên thứ hai trước khi đổi vai trò hoặc khóa tài khoản này.");
            ad.updateByAdmin(id, n, em, rid, status);
            if (!Validator.blank(newPassword)) ad.updatePassword(id, PasswordUtil.hash(newPassword));
            return null;
        });
    }
}
