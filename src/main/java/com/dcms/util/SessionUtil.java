package com.dcms.util;

import com.dcms.model.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {}
    public static final String USER = "user";
    public static Account user(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        return s == null ? null : (Account) s.getAttribute(USER);
    }
    public static boolean is(Account a, String role) { return a != null && role.equals(a.getRoleName()); }
}
