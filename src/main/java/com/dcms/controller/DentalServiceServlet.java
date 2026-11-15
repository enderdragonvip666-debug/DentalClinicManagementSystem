package com.dcms.controller;

import com.dcms.model.DentalService;
import com.dcms.service.CatalogService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Dental service list, creation page, and edit modal (Administrator). */
@WebServlet({"/admin/services", "/admin/services/new"})
public class DentalServiceServlet extends BaseServlet {
    private final CatalogService catalog = new CatalogService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("/admin/services/new".equals(req.getServletPath())) {
            render(req, resp, "services_new", "Thêm dịch vụ nha khoa");
            return;
        }
        if ("edit".equals(p(req, "do"))) {
            DentalService edit = catalog.find(id(p(req, "id")));
            if (edit == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("edit", edit);
            if ("1".equals(p(req, "modal"))) {
                resp.setHeader("Cache-Control", "no-store");
                req.getRequestDispatcher("/WEB-INF/views/service_edit_modal.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("openEditId", edit.getServiceId());
        }
        req.setAttribute("services", catalog.listAll());
        render(req, resp, "services", "Quản lý dịch vụ nha khoa");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean create = id(p(req, "serviceId")) == 0;
        try {
            catalog.save(id(p(req, "serviceId")), p(req, "name"), p(req, "fee"), p(req, "status"));
            ok(req, Msg.MSG37);
            go(req, resp, "/admin/services");
        } catch (BizException e) {
            err(req, e.getMessage());
            go(req, resp, create ? "/admin/services/new" : "/admin/services");
        }
    }
}
