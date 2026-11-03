package com.dcms.controller;

import com.dcms.filter.AuthorizationFilter;
import com.dcms.model.Account;
import com.dcms.service.AuthService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** UC01-UC06. */
@WebServlet({"/login", "/signup", "/forgot", "/reset", "/logout", "/password"})
public class AuthServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/logout": logout(req, resp); return;
            case "/login": if (user(req) != null) { go(req, resp, AuthorizationFilter.homeOf(user(req))); return; } render(req, resp, "login", "Đăng nhập"); return;
            case "/signup": render(req, resp, "signup", "Đăng ký tài khoản"); return;
            case "/forgot": render(req, resp, "forgot", "Quên mật khẩu"); return;
            case "/reset":
                try { auth.verifyReset(req.getParameter("token")); req.setAttribute("token", p(req, "token")); }
                catch (BizException e) { req.setAttribute("err", e.getMessage()); }
                render(req, resp, "forgot", "Đặt lại mật khẩu"); return;
            default: render(req, resp, "password", "Đổi mật khẩu");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/logout": logout(req, resp); return;
                case "/login": {
                    Account a = auth.signIn(p(req, "phone"), p(req, "password"));
                    req.getSession(true); req.changeSessionId();
                    req.getSession().setAttribute(SessionUtil.USER, a);
                    go(req, resp, AuthorizationFilter.homeOf(a)); return;
                }
                case "/signup":
                    auth.signUp(p(req, "fullName"), p(req, "phone"), p(req, "email"), p(req, "password"), p(req, "confirm"));
                    ok(req, Msg.MSG10); go(req, resp, "/login"); return;
                case "/forgot":
                    auth.requestReset(p(req, "phone"), baseUrl(req));
                    req.setAttribute("ok", Msg.MSG12); render(req, resp, "forgot", "Quên mật khẩu"); return;
                case "/reset":
                    auth.resetPassword(p(req, "token"), p(req, "password"), p(req, "confirm"));
                    ok(req, Msg.MSG14); go(req, resp, "/login"); return;
                case "/password":
                    auth.changePassword(user(req).getAccountId(), p(req, "current"), p(req, "password"), p(req, "confirm"));
                    if (passwordModal(req)) { passwordResponse(resp, HttpServletResponse.SC_OK, Msg.MSG14); return; }
                    ok(req, Msg.MSG14);
                    go(req, resp, AuthorizationFilter.homeOf(user(req)));
                    return;
                default: resp.sendError(HttpServletResponse.SC_NOT_FOUND); return;
            }
        } catch (BizException e) {
            if (path.equals("/password") && passwordModal(req)) {
                passwordResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            req.setAttribute("err", e.getMessage());
            req.setAttribute("token", p(req, "token"));
            String jsp = path.substring(1);
            render(req, resp, path.equals("/reset") ? "forgot" : jsp, jsp);
        }
    }

    private boolean passwordModal(HttpServletRequest req) {
        return "1".equals(p(req, "modal")) && "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    private void passwordResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(message);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        go(req, resp, "/home");
    }
}
