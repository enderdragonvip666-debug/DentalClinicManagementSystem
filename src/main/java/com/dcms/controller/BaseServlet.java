package com.dcms.controller;

import com.dcms.model.Account;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Shared helpers: render a JSP, flash messages (post-redirect-get), redirect, parse ids. */
public abstract class BaseServlet extends HttpServlet {
    @Override public void init() { }
    protected Account user(HttpServletRequest r) { return SessionUtil.user(r); }

    protected void render(HttpServletRequest req, HttpServletResponse resp, String jsp, String title) throws ServletException, IOException {
        req.setAttribute("title", title);
        HttpSession s = req.getSession(false);
        if (s != null) for (String k : new String[]{"ok", "err"}) {
            Object v = s.getAttribute("flash_" + k);
            if (v != null) { if (req.getAttribute(k) == null) req.setAttribute(k, v); s.removeAttribute("flash_" + k); }
        }
        req.getRequestDispatcher("/WEB-INF/views/" + jsp + ".jsp").forward(req, resp);
    }
    protected void ok(HttpServletRequest r, String m) { r.getSession().setAttribute("flash_ok", m); }
    protected void err(HttpServletRequest r, String m) { r.getSession().setAttribute("flash_err", m); }
    protected void go(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException { resp.sendRedirect(req.getContextPath() + path); }
    protected static long id(String s) { try { return Long.parseLong(s.trim()); } catch (Exception e) { return 0; } }
    protected static String p(HttpServletRequest r, String k) { String v = r.getParameter(k); return v == null ? "" : v; }
    protected static String baseUrl(HttpServletRequest r) {
        String env = System.getenv("DCMS_BASE_URL");
        if (env != null && !env.isEmpty()) return env;
        int port = r.getServerPort(); boolean std = port == 80 || port == 443;
        return r.getScheme() + "://" + r.getServerName() + (std ? "" : ":" + port) + r.getContextPath();
    }
}
