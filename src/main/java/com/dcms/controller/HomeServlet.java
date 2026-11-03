package com.dcms.controller;

import com.dcms.service.CatalogService;
import com.dcms.service.ConsultationService;
import com.dcms.util.Shift;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Public Home: banner, quick appointment booking (date + shift) and services. */
@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    private final CatalogService catalog = new CatalogService();
    private final ConsultationService consult = new ConsultationService();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("services", catalog.listActive());
        req.setAttribute("days", consult.days());
        req.setAttribute("shifts", Shift.values());
        render(req, resp, "home", "Trang chủ");
    }
}
