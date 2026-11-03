package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.model.*;
import com.dcms.util.*;
import java.math.BigDecimal;
import java.util.*;

/** BF-03: checkout list, invoice on first print, full payment. */
public class BillingService {

    public List<Visit> checkoutList(String q) { return DB.read(c -> new VisitDAO(c).checkoutList(q)); }

    private static Visit billable(VisitDAO vd, long visitId) throws Exception {
        Visit v = vd.findById(visitId);
        if (v == null || !("AWAITING_PAYMENT".equals(v.getStatus()) || "COMPLETED".equals(v.getStatus())))
            throw new BizException("Lượt khám này không nằm trong danh sách thanh toán.");
        return v;
    }

    /** Before the first print this is only a preview with current fees: nothing is written (SRS 1.6.2). */
    public BillingView view(long visitId) {
        return DB.read(c -> {
            Visit v = billable(new VisitDAO(c), visitId);
            ExamFormDAO fd = new ExamFormDAO(c);
            ExamForm f = fd.findActiveByVisit(visitId);
            Invoice inv = new InvoiceDAO(c).findByVisit(visitId);
            List<InvoiceItem> lines; BigDecimal total = BigDecimal.ZERO;
            if (inv != null) { lines = inv.getItems(); total = inv.getTotal(); }
            else {
                lines = new ArrayList<>();
                if (f != null) for (DentalService s : fd.servicesWithCurrentFee(f.getExamFormId())) {
                    InvoiceItem it = new InvoiceItem(); it.setServiceId(s.getServiceId()); it.setServiceName(s.getName()); it.setFeeAtBilling(s.getFee());
                    lines.add(it); total = total.add(s.getFee());
                }
            }
            return new BillingView(v, inv, lines, total, f);
        });
    }

    /** DBR-17/18/19/20: create the invoice on first print with the fees of that moment; later prints reuse it. */
    public void print(long visitId) {
        DB.tx(c -> {
            VisitDAO vd = new VisitDAO(c);
            billable(vd, visitId); vd.findByIdForUpdate(visitId);
            InvoiceDAO id = new InvoiceDAO(c);
            if (id.findByVisit(visitId) != null) return null;
            ExamFormDAO fd = new ExamFormDAO(c);
            ExamForm f = fd.findActiveByVisit(visitId);                       // DBR-15: a Deleted form is never billed
            if (f == null) throw new BizException(Msg.MSG28);
            List<DentalService> svc = fd.servicesWithCurrentFee(f.getExamFormId());
            BigDecimal total = BigDecimal.ZERO;
            for (DentalService s : svc) total = total.add(s.getFee());
            long invId = id.insert(visitId, total);
            for (DentalService s : svc) id.insertItem(invId, s.getServiceId(), s.getFee());
            return null;
        });
    }

    /** DBR-21/22/23/24: one full payment, invoice Paid, Visit Completed. */
    public void recordPayment(Account assistant, long visitId) {
        DB.tx(c -> {
            VisitDAO vd = new VisitDAO(c);
            Visit v = vd.findByIdForUpdate(visitId);
            InvoiceDAO id = new InvoiceDAO(c);
            Invoice inv = v == null ? null : id.findByVisit(visitId);
            if (inv == null) throw new BizException("Vui lòng in hóa đơn trước, sau đó mới ghi nhận thanh toán.");
            if ("PAID".equals(inv.getStatus())) throw new BizException(Msg.MSG34);
            if (!"AWAITING_PAYMENT".equals(v.getStatus())) throw new BizException("Lượt khám này không ở trạng thái chờ thanh toán.");
            if (inv.getTotal().signum() <= 0) throw new BizException("Tổng hóa đơn bằng 0 nên không có khoản thanh toán để ghi nhận.");
            new PaymentDAO(c).insert(inv.getInvoiceId(), assistant.getAccountId(), inv.getTotal());
            id.markPaid(inv.getInvoiceId());
            vd.setStatus(visitId, "COMPLETED");
            return null;
        });
    }
}
