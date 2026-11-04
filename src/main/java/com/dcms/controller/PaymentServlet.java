package com.dcms.controller;

import com.dcms.service.BillingService;
import com.dcms.util.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** UC28: record the payment in full. */
@WebServlet("/payment")
public class PaymentServlet extends BaseServlet {
    private final BillingService billing = new BillingService();

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long vid = id(p(req, "visitId"));
        try { billing.recordPayment(user(req), vid); ok(req, Msg.MSG33); }
        catch (BizException e) { err(req, e.getMessage()); }
        go(req, resp, "/invoice?visitId=" + vid);
    }
}
