package com.dcms.controller;

import com.dcms.service.AccountService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** UC08-UC10: User Management (Administrator). */
@WebServlet("/admin/users")
public class AccountServlet extends BaseServlet {
    private final AccountService accounts = new AccountService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        list(req, resp, "edit".equals(p(req, "do")) ? accounts.find(id(p(req, "id"))) : null);
    }

    private void list(HttpServletRequest req, HttpServletResponse resp, Object edit) throws ServletException, IOException {
        req.setAttribute("q", p(req, "q")); req.setAttribute("users", accounts.search(p(req, "q")));
        req.setAttribute("roles", accounts.roles()); req.setAttribute("statuses", AccountService.statuses());
        req.setAttribute("activeAdminCount", accounts.countActiveAdministrators());
        if (edit != null) req.setAttribute("edit", edit);
        render(req, resp, "users", "Quản lý người dùng");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = id(p(req, "accountId"));
        try {
            if (id == 0) accounts.create(user(req), p(req, "fullName"), p(req, "phone"), p(req, "email"), p(req, "password"), p(req, "role"), p(req, "status"));
            else accounts.update(user(req), id, p(req, "fullName"), p(req, "email"), p(req, "role"), p(req, "status"), p(req, "password"));
            if (id != 0 && user(req) != null && user(req).getAccountId() == id && !"Administrator".equals(p(req, "role"))) {
                // Prevent the old session from retaining Administrator privileges after self-demotion.
                req.getSession().invalidate();
                ok(req, "Đã đổi vai trò của bạn. Vui lòng đăng nhập lại với vai trò mới.");
                go(req, resp, "/login");
                return;
            }
            ok(req, Msg.MSG39);
        } catch (BizException e) { err(req, e.getMessage()); }
        go(req, resp, "/admin/users");
    }
}
