package com.dcms.controller;

import com.dcms.service.CatalogService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** UC11-UC14: Dental Service Management (Administrator). */
@WebServlet("/admin/services")
public class DentalServiceServlet extends BaseServlet {
    private final CatalogService catalog = new CatalogService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("services", catalog.listAll());
        if ("edit".equals(p(req, "do"))) req.setAttribute("edit", catalog.find(id(p(req, "id"))));
        render(req, resp, "services", "Quản lý dịch vụ nha khoa");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try { catalog.save(id(p(req, "serviceId")), p(req, "name"), p(req, "fee"), p(req, "status")); ok(req, Msg.MSG37); }
        catch (BizException e) { err(req, e.getMessage()); }
        go(req, resp, "/admin/services");
    }
}
