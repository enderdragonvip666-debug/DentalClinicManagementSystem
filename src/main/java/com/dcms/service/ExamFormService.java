package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.model.*;
import com.dcms.util.*;
import java.util.*;

/** UC23-UC25: enter, delete and finish the exam form of an In Progress Visit. */
public class ExamFormService {

    private Visit editable(VisitDAO vd, long visitId, Account dentist) throws Exception {
        Visit v = vd.findByIdForUpdate(visitId);
        VisitService.ensureOnList(v, dentist);
        if (!"IN_PROGRESS".equals(v.getStatus())) throw new BizException("Vui lòng mở bệnh nhân từ danh sách bệnh nhân trước.");   // DBR-10/17
        return v;
    }

    public ExamForm current(Account dentist, long visitId) {
        return DB.read(c -> {
            Visit v = new VisitDAO(c).findById(visitId);
            VisitService.ensureOnList(v, dentist);
            return new ExamFormDAO(c).findActiveByVisit(visitId);
        });
    }

    /** Saves the single ACTIVE exam form of the Visit (DBR-13); services come from the active catalog and appear once (DBR-12). */
    public void save(Account dentist, long visitId, String diagnosis, String notes, String medication, List<Long> serviceIds) {
        diagnosis = Validator.trim(diagnosis); notes = Validator.trim(notes); medication = Validator.trim(medication);
        if (Validator.blank(diagnosis)) throw new BizException(Msg.MSG23);
        Validator.max(diagnosis, 1000, "Chẩn đoán"); Validator.max(notes, 2000, "Ghi chú lâm sàng"); Validator.max(medication, 1000, "Thuốc");
        if (serviceIds == null || serviceIds.isEmpty()) throw new BizException(Msg.MSG24);
        final String d = diagnosis, n = Validator.blank(notes) ? null : notes, m = Validator.blank(medication) ? null : medication;
        final List<Long> ids = new ArrayList<>(new LinkedHashSet<>(serviceIds));
        DB.tx(c -> {
            editable(new VisitDAO(c), visitId, dentist);
            DentalServiceDAO sd = new DentalServiceDAO(c);
            for (Long id : ids) { DentalService s = sd.findById(id); if (s == null || !"ACTIVE".equals(s.getStatus())) throw new BizException(Msg.MSG24); }
            ExamFormDAO fd = new ExamFormDAO(c);
            ExamForm f = fd.findActiveByVisit(visitId);
            long fid;
            if (f == null) fid = fd.insert(visitId, d, n, m); else { fid = f.getExamFormId(); fd.update(fid, d, n, m); }
            fd.replaceServices(fid, ids);
            return null;
        });
    }

    /** One confirmation writes the form and closes the examination atomically. A preview never changes the DB. */
    public void createAndFinish(Account dentist, long visitId, String diagnosis, String notes, String medication, List<Long> serviceIds) {
        diagnosis = Validator.trim(diagnosis); notes = Validator.trim(notes); medication = Validator.trim(medication);
        if (Validator.blank(diagnosis)) throw new BizException(Msg.MSG23);
        Validator.max(diagnosis, 1000, "Chẩn đoán");
        Validator.max(notes, 2000, "Ghi chú lâm sàng"); Validator.max(medication, 1000, "Thuốc");
        if (serviceIds == null || serviceIds.isEmpty()) throw new BizException(Msg.MSG24);
        final String d = diagnosis, n = Validator.blank(notes) ? null : notes, m = Validator.blank(medication) ? null : medication;
        final List<Long> ids = new ArrayList<>(new LinkedHashSet<>(serviceIds));
        DB.tx(c -> {
            VisitDAO vd = new VisitDAO(c);
            editable(vd, visitId, dentist);
            DentalServiceDAO sd = new DentalServiceDAO(c);
            for (Long id : ids) {
                DentalService s = sd.findById(id);
                if (s == null || !"ACTIVE".equals(s.getStatus())) throw new BizException(Msg.MSG24);
            }
            ExamFormDAO fd = new ExamFormDAO(c);
            ExamForm existing = fd.findActiveByVisit(visitId);
            long fid;
            if (existing == null) fid = fd.insert(visitId, d, n, m);
            else { fid = existing.getExamFormId(); fd.update(fid, d, n, m); }
            fd.replaceServices(fid, ids);
            vd.setStatus(visitId, "AWAITING_PAYMENT");
            return null;
        });
    }

    /** DBR-15: marked DELETED, kept, never billed; a new form can then be entered. */
    public void delete(Account dentist, long visitId) {
        DB.tx(c -> {
            editable(new VisitDAO(c), visitId, dentist);
            ExamFormDAO fd = new ExamFormDAO(c);
            ExamForm f = fd.findActiveByVisit(visitId);
            if (f == null) throw new BizException(Msg.MSG28);
            fd.markDeleted(f.getExamFormId());
            return null;
        });
    }

    /** DBR-14 / DBR-16: needs a saved form; afterwards the form is locked and the Visit goes to checkout. */
    public void finish(Account dentist, long visitId) {
        DB.tx(c -> {
            VisitDAO vd = new VisitDAO(c);
            editable(vd, visitId, dentist);
            if (new ExamFormDAO(c).findActiveByVisit(visitId) == null) throw new BizException(Msg.MSG28);
            vd.setStatus(visitId, "AWAITING_PAYMENT");
            return null;
        });
    }
}
