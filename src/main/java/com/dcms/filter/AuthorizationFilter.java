package com.dcms.filter;

import com.dcms.model.Account;
import com.dcms.dao.AccountDAO;
import com.dcms.util.DB;
import com.dcms.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/** Role-based access (GBR-03, SRS 4.4 permission matrix): each fixed role reaches only its own screens. */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    private static final String P = "Patient", DA = "Dental Assistant", DN = "Dentist", AD = "Administrator";
    private static final Map<String, Set<String>> RULES = new LinkedHashMap<>();
    static {
        RULES.put("/my-requests", Set.of(P));
        for (String s : new String[]{"/requests", "/reschedule", "/checkin", "/checkout", "/invoice", "/payment"}) RULES.put(s, Set.of(DA));
        RULES.put("/history", Set.of(P, DA));
        for (String s : new String[]{"/patients", "/patient", "/patient-history", "/examform", "/doctor-schedule"}) RULES.put(s, Set.of(DN));
        RULES.put("/admin/", Set.of(AD));
        RULES.put("/consult", Set.of(P, DA));          // Guests are allowed too (no account); Dentist and Administrator are not
    }

    public static String homeOf(Account a) {
        switch (a.getRoleName()) {
            case P: return "/my-requests"; case DA: return "/requests"; case DN: return "/patients"; default: return "/admin/users";
        }
    }

    @Override public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq; HttpServletResponse resp = (HttpServletResponse) rs;
        Account u = SessionUtil.user(req);
        String path = req.getServletPath();
        // A previously logged-in administrator may have been demoted by another admin.
        // Always validate the current DB role before allowing any /admin/* request.
        if (u != null && path.startsWith("/admin/")) {
            Account live = DB.read(c -> new AccountDAO(c).findById(u.getAccountId()));
            if (live == null || !AD.equals(live.getRoleName()) || !"ACTIVE".equals(live.getStatus())) {
                req.getSession().invalidate();
                req.getSession().setAttribute("flash_err", "Quyền quản trị đã thay đổi. Vui lòng đăng nhập lại.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }
        if (u != null) {
            for (Map.Entry<String, Set<String>> e : RULES.entrySet()) {
                boolean hit = e.getKey().endsWith("/") ? path.startsWith(e.getKey()) : path.equals(e.getKey());
                if (hit && !e.getValue().contains(u.getRoleName())) {
                    req.getSession().setAttribute("flash_err", "Bạn không có quyền truy cập trang này.");
                    resp.sendRedirect(req.getContextPath() + homeOf(u));
                    return;
                }
            }
        }
        chain.doFilter(rq, rs);
    }
}
