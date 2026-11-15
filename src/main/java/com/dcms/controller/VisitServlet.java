package com.dcms.controller;

import com.dcms.model.Visit;
import com.dcms.service.VisitService;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Check-in, treatment history, Dentist patient list, patient information and the history/background panel. */
@WebServlet({"/checkin", "/patients", "/patient", "/history", "/patient-history", "/doctor-schedule"})
public class VisitServlet extends BaseServlet {
    private final VisitService visits = new VisitService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/doctor-schedule":
                // Keep old bookmarks working, without a separate schedule screen.
                String dayParam = p(req, "date");
                go(req, resp, "/patients" + (dayParam.isEmpty() ? "" : "?date=" + URLEncoder.encode(dayParam, StandardCharsets.UTF_8)));
                return;
            case "/patients":
                java.time.LocalDate date = BookingWindow.parse(p(req, "date"));
                if (!BookingWindow.contains(date)) date = BookingWindow.today();
                req.setAttribute("selectedDate", date.toString());
                req.setAttribute("doctorDays", visits.doctorDays(user(req)));
                req.setAttribute("q", p(req, "q"));
                req.setAttribute("visits", visits.doctorCasesOnDay(user(req), date, p(req, "q")));
                render(req, resp, "patient_list", "Danh sách bệnh nhân");
                return;
            case "/history": req.setAttribute("q", p(req, "q")); req.setAttribute("visits", visits.treatmentHistory(user(req), p(req, "q"))); render(req, resp, "history", "Lịch sử điều trị"); return;
            case "/patient-history":
                try { panel(req, resp, visits.peek(user(req), id(p(req, "visitId")))); }
                catch (BizException e) { req.setAttribute("panelErr", e.getMessage()); panel(req, resp, null); }
                return;
            case "/patient":
                // Legacy bookmark: skip the old patient_info.jsp intermediate screen.
                // ExamFormServlet.showForm() validates access and starts a WAITING Visit.
                go(req, resp, "/examform?visitId=" + id(p(req, "visitId")));
                return;
            default: go(req, resp, "/requests");
        }
    }

    /** Renders only the history/background panel (used inside the pop-up and on the patient page). */
    private void panel(HttpServletRequest req, HttpServletResponse resp, Visit v) throws ServletException, IOException {
        if (v != null) { req.setAttribute("visit", v); req.setAttribute("history", visits.examHistory(v)); req.setAttribute("medHistory", visits.medicalHistory(v)); }
        req.getRequestDispatcher("/WEB-INF/views/_history_panel.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/patient-history")) {
            long vid = id(p(req, "visitId"));
            try { visits.saveMedicalHistory(user(req), vid, p(req, "medicalHistory")); req.setAttribute("panelOk", Msg.MSG46); }
            catch (BizException e) { req.setAttribute("panelErr", e.getMessage()); }
            try { panel(req, resp, visits.peek(user(req), vid)); }
            catch (BizException e) { req.setAttribute("panelErr", e.getMessage()); panel(req, resp, null); }
            return;
        }
        // Dentist read-only routes must never act as aliases for receptionist check-in.
        if (!"/checkin".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String date = p(req, "date");
        try { visits.checkIn(id(p(req, "requestId"))); ok(req, Msg.MSG22); }
        catch (BizException e) { err(req, e.getMessage()); }
        go(req, resp, "/requests?status=" + com.dcms.service.ConsultationService.normalizeStatus(p(req, "status"))
                + (date.isEmpty() ? "" : "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)));
    }
}
