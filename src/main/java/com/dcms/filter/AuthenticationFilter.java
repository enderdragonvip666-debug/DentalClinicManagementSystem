package com.dcms.filter;

import com.dcms.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

/** Every screen needs sign-in except the public pages and the pages open to Guests (SDS 1). */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    private static final Set<String> PUBLIC = Set.of("/", "/index.jsp", "/home", "/login", "/signup", "/forgot", "/reset", "/consult");

    @Override public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq; HttpServletResponse resp = (HttpServletResponse) rs;
        req.setCharacterEncoding("UTF-8");             // Vietnamese text in form posts
        String path = req.getServletPath();
        if (path.startsWith("/assets/") || PUBLIC.contains(path)) { chain.doFilter(rq, rs); return; }
        if (SessionUtil.user(req) == null) {
            if ("/password".equals(path) && "XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }
        resp.setHeader("Cache-Control", "no-store");     // no cached private pages after sign-out
        chain.doFilter(rq, rs);
    }
}
