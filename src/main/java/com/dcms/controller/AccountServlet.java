package com.dcms.controller;

import com.dcms.model.Account;
import com.dcms.service.AccountService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** User listing, separate creation page, and edit modal (Administrator). */
@WebServlet({"/admin/users", "/admin/users/new"})
public class AccountServlet extends BaseServlet {
    private final AccountService accounts = new AccountService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/users/new".equals(path)) {
            editorData(req);
            render(req, resp, "users_new", "Thêm người dùng");
            return;
        }
        if ("edit".equals(p(req, "do"))) {
            Account edit = accounts.find(id(p(req, "id")));
            if (edit == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            editorData(req);
            req.setAttribute("edit", edit);
            if ("1".equals(p(req, "modal"))) {
                resp.setHeader("Cache-Control", "no-store");
                req.getRequestDispatcher("/WEB-INF/views/user_edit_modal.jsp").forward(req, resp);
                return;
            }
            // Preserve older bookmarks by loading the user list with the edit dialog open.
            list(req, resp);
            req.setAttribute("openEditId", edit.getAccountId());
            render(req, resp, "users", "Quản lý người dùng");
            return;
        }
        list(req, resp);
        render(req, resp, "users", "Quản lý người dùng");
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("q", p(req, "q"));
        req.setAttribute("users", accounts.search(p(req, "q")));
    }

    private void editorData(HttpServletRequest req) {
        req.setAttribute("roles", accounts.roles());
        req.setAttribute("statuses", AccountService.statuses());
        req.setAttribute("activeAdminCount", accounts.countActiveAdministrators());
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long accountId = id(p(req, "accountId"));
        boolean create = accountId == 0;
        try {
            if (create) {
                accounts.create(user(req), p(req, "fullName"), p(req, "phone"),
                        p(req, "email"), p(req, "password"), p(req, "role"), p(req, "status"));
            } else {
                accounts.update(user(req), accountId, p(req, "fullName"),
                        p(req, "email"), p(req, "role"), p(req, "status"), p(req, "password"));
            }
            if (!create && user(req) != null && user(req).getAccountId() == accountId
                    && !"Administrator".equals(p(req, "role"))) {
                req.getSession().invalidate();
                ok(req, "Đã đổi vai trò của bạn. Vui lòng đăng nhập lại với vai trò mới.");
                go(req, resp, "/login");
                return;
            }
            ok(req, Msg.MSG39);
            go(req, resp, "/admin/users");
        } catch (BizException e) {
            err(req, e.getMessage());
            go(req, resp, create ? "/admin/users/new" : "/admin/users");
        }
    }
}
