package com.dcms.controller;

import com.dcms.model.Account;
import com.dcms.service.*;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** Booking CRUD, separate receptionist schedule and rescheduling pages. */
@WebServlet({"/consult", "/my-requests", "/requests", "/reschedule"})
public class ConsultationRequestServlet extends BaseServlet {
    private final ConsultationService consult = new ConsultationService();
    private final CatalogService catalog = new CatalogService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/consult":
                form(req, resp);
                return;
            case "/my-requests":
                req.setAttribute("requests", consult.listMine(user(req)));
                render(req, resp, "my_requests", "Lịch khám của tôi");
                return;
            case "/reschedule":
                receptionistList(req, resp, true);
                return;
            case "/requests":
            default:
                receptionistList(req, resp, false);
        }
    }

    private void receptionistList(HttpServletRequest req, HttpServletResponse resp, boolean reschedule)
            throws ServletException, IOException {
        String status = reschedule ? "REQUESTED" : ConsultationService.normalizeStatus(p(req, "status"));
        req.setAttribute("statusFilter", status);
        req.setAttribute("q", p(req, "q"));
        req.setAttribute("todayIso", BookingWindow.today().toString());
        req.setAttribute("sv", consult.schedule(p(req, "date"), p(req, "q"), status));
        render(req, resp, reschedule ? "reschedule_list" : "request_list",
                reschedule ? "Đổi lịch khám" : "Lịch khám");
    }

    private void form(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("services", catalog.listActive());
        req.setAttribute("days", consult.days());
        req.setAttribute("shifts", Shift.values());
        if ("1".equals(p(req, "modal"))) {
            resp.setHeader("Cache-Control", "no-store");
            req.getRequestDispatcher("/WEB-INF/views/consult_modal.jsp").forward(req, resp);
        } else render(req, resp, "consult", "Đặt lịch khám");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account actor = user(req);
        String path = req.getServletPath();
        String date = p(req, "date");
        try {
            if ("/consult".equals(path)) {
                String code = consult.request(actor, p(req, "fullName"), p(req, "phone"),
                        id(p(req, "serviceId")), p(req, "note"), date, p(req, "shift"));
                req.setAttribute("ok", String.format(Msg.MSG18, code));
                req.setAttribute("code", code);
                req.setAttribute("booked", BookingWindow.describe(LocalDate.parse(date), Shift.parse(p(req, "shift"))));
                form(req, resp);
                return;
            }
            if ("/reschedule".equals(path)) {
                if (!"move".equals(p(req, "action"))) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                String moved = consult.reschedule(id(p(req, "requestId")),
                        p(req, "targetDate"), p(req, "targetShift"));
                ok(req, "Đã đổi lịch khám sang " + moved + ".");
                date = p(req, "targetDate");
            } else if ("/requests".equals(path) || "/my-requests".equals(path)) {
                // The Appointment List performs cancellation only; rescheduling has its own tab.
                if (!p(req, "action").isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                consult.cancel(actor, id(p(req, "requestId")));
                ok(req, Msg.MSG19);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } catch (BizException e) {
            if ("/consult".equals(path)) {
                req.setAttribute("err", e.getMessage());
                form(req, resp);
                return;
            }
            err(req, e.getMessage());
        }
        if ("/my-requests".equals(path)) {
            go(req, resp, "/my-requests");
        } else if ("/reschedule".equals(path)) {
            go(req, resp, "/reschedule" + (date.isEmpty() ? "" : "?date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)));
        } else {
            go(req, resp, "/requests?status=" + ConsultationService.normalizeStatus(p(req, "status"))
                    + (date.isEmpty() ? "" : "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)));
        }
    }
}
