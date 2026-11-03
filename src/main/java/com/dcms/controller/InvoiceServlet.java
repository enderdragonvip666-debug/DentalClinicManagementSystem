package com.dcms.controller;

import com.dcms.service.BillingService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** UC26-UC27: checkout list, invoice preview, print (creates the invoice on first print). */
@WebServlet({"/checkout", "/invoice"})
public class InvoiceServlet extends BaseServlet {
    private final BillingService billing = new BillingService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/checkout")) {
            req.setAttribute("q", p(req, "q")); req.setAttribute("visits", billing.checkoutList(p(req, "q")));
            render(req, resp, "checkout_list", "Danh sách thanh toán"); return;
        }
        try { req.setAttribute("bill", billing.view(id(p(req, "visitId")))); req.setAttribute("autoPrint", "1".equals(p(req, "printed"))); }
        catch (BizException e) { err(req, e.getMessage()); go(req, resp, "/checkout"); return; }
        req.setAttribute("confirmMsg", "");
        render(req, resp, "invoice", "Hóa đơn và thanh toán");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long vid = id(p(req, "visitId"));
        try { billing.print(vid); ok(req, Msg.MSG31); go(req, resp, "/invoice?visitId=" + vid + "&printed=1"); return; }
        catch (BizException e) { err(req, e.getMessage()); }
        go(req, resp, "/invoice?visitId=" + vid);
    }
}
