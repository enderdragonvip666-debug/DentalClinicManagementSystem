package com.dcms.controller;

import com.dcms.model.ExamForm;
import com.dcms.model.Visit;
import com.dcms.service.*;
import com.dcms.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * UC23-UC25: enter / delete the exam form and finish the examination.
 */
@WebServlet("/examform")
public class ExamFormServlet extends BaseServlet {

    private final ExamFormService forms = new ExamFormService();
    private final VisitService visits = new VisitService();
    private final CatalogService catalog = new CatalogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long vid = id(p(req, "visitId"));
        try {
            showForm(req, resp, vid, null);
        } catch (BizException e) {
            err(req, e.getMessage());
            go(req, resp, "/patients");
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, long vid, ExamForm posted) throws ServletException, IOException {
        ExamForm cur = forms.current(user(req), vid);        // saved ACTIVE form, if any
        Visit v = visits.open(user(req), vid);               // checks the Visit is on this Dentist's list
        req.setAttribute("visit", v);
        req.setAttribute("form", posted != null ? posted : cur);
        req.setAttribute("saved", cur != null);
        req.setAttribute("services", catalog.listActive());
        render(req, resp, "exam_form", "Phiếu khám");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long vid = id(p(req, "visitId"));
        if (!"create".equals(p(req, "action"))) {
            err(req, "Thao tác không hợp lệ. Hãy tạo phiếu khám và xác nhận.");
            go(req, resp, "/examform?visitId=" + vid);
            return;
        }
        List<Long> ids = new ArrayList<>();
        String[] selected = req.getParameterValues("serviceIds");
        if (selected != null) {
            for (String s : selected) {
                if (id(s) > 0) {
                    ids.add(id(s));
                }
            }
        }
        try {
            String medication = p(req, "medication");

            if (medication == null || medication.trim().isEmpty()) {
                medication = "Không sử dụng thuốc";
            }

            forms.createAndFinish(
                    user(req),
                    vid,
                    p(req, "diagnosis"),
                    p(req, "clinicalNotes"),
                    medication,
                    ids
            );
            ok(req, "Đã xác nhận phiếu khám. Bệnh nhân đã chuyển sang danh sách chờ thanh toán.");
            go(req, resp, "/patients");
        } catch (BizException e) {
            ExamForm f = new ExamForm();
            f.setVisitId(vid);
            f.setDiagnosis(p(req, "diagnosis"));
            f.setClinicalNotes(p(req, "clinicalNotes"));
            f.setMedication(p(req, "medication"));
            f.setServiceIds(ids);
            req.setAttribute("err", e.getMessage());
            try {
                showForm(req, resp, vid, f);
            } catch (BizException other) {
                err(req, other.getMessage());
                go(req, resp, "/patients");
            }
        }
    }
}
