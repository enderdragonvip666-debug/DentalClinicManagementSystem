package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.model.*;
import com.dcms.util.*;
import java.util.List;

/** Check-in (UC19) and the Dentist's patient list (UC21, UC22). */
public class VisitService {

    /** One request -> at most one Visit (DBR-07/08); finds or creates the Patient by phone (DBR-01/02); dentist auto-assigned (DBR-09). */
    public void checkIn(long requestId) {
        DB.tx(c -> {
            ConsultationRequestDAO rd = new ConsultationRequestDAO(c);
            ConsultationRequest r = rd.findByIdForUpdate(requestId);
            if (r == null || !"REQUESTED".equals(r.getStatus())) throw new BizException(Msg.MSG21);
            // Only appointments for today may be checked in, even if a client forges a POST.
            if (r.getApptDate() == null || !BookingWindow.today().equals(r.getApptDate().toLocalDate()))
                throw new BizException("Chỉ được check-in lịch khám đúng ngày hôm nay. Lịch ngày khác chỉ có thể đổi hoặc hủy.");
            PatientDAO pd = new PatientDAO(c); AccountDAO ad = new AccountDAO(c);
            Patient p = pd.findByPhone(r.getPhone());
            long patientId;
            if (p != null) patientId = p.getPatientId();
            else {
                Account a = ad.findByPhone(r.getPhone());
                Long accId = (a != null && "Patient".equals(a.getRoleName()) && pd.findByAccountId(a.getAccountId()) == null) ? a.getAccountId() : null;
                patientId = pd.insert(accId, r.getFullName(), r.getPhone());
            }
            Account dentist = ad.firstActiveWithRole("Dentist");
            if (dentist == null) throw new BizException("Hiện không có nha sĩ nào đang hoạt động. Vui lòng liên hệ quản trị viên.");
            rd.setStatus(requestId, "ARRIVED", patientId);
            new VisitDAO(c).insert(patientId, requestId, dentist.getAccountId(), "ONLINE");
            return null;
        });
    }

    public List<Visit> patientList(Account dentist, String q) { return DB.read(c -> new VisitDAO(c).patientList(dentist.getAccountId(), q)); }

    /** Read-only daily list combining scheduled bookings and visits already checked in. */
    public List<DoctorCase> doctorCasesOnDay(Account dentist, java.time.LocalDate date, String q) {
        return DB.read(c -> new VisitDAO(c).doctorCasesOnDay(dentist.getAccountId(), date, q));
    }

    /** Next eight days, with the total number of actually visible cases (morning+afternoon). */
    public List<DayOption> doctorDays(Account dentist) {
        return DB.read(c -> {
            java.time.LocalDate today = BookingWindow.today();
            java.util.Map<String, int[]> counts = new java.util.HashMap<>();
            for (Object[] row : new VisitDAO(c).doctorCaseCounts(dentist.getAccountId(), today, today.plusDays(7))) {
                int[] parts = counts.computeIfAbsent((String) row[0], k -> new int[2]);
                parts["MORNING".equals(row[1]) ? 0 : 1] += (Integer) row[2];
            }
            java.util.List<DayOption> days = new java.util.ArrayList<>();
            for (int i = 0; i <= 7; i++) {
                java.time.LocalDate day = today.plusDays(i);
                int[] parts = counts.getOrDefault(day.toString(), new int[2]);
                days.add(new DayOption(day, true, true, parts[0], parts[1]));
            }
            return days;
        });
    }

    /** DBR-11: only a Visit on this Dentist's list can be opened. Opening a Waiting Visit sets it to In Progress (DBR-10). */
    public Visit open(Account dentist, long visitId) {
        return DB.tx(c -> {
            VisitDAO vd = new VisitDAO(c);
            Visit v = vd.findByIdForUpdate(visitId);
            ensureOnList(v, dentist);
            if ("WAITING".equals(v.getStatus())) { vd.setStatus(visitId, "IN_PROGRESS"); v.setStatus("IN_PROGRESS"); }
            return v;
        });
    }

    /** Read-only look at a patient on this Dentist's list: does NOT change the Visit status (Waiting stays Waiting). */
    public Visit peek(Account dentist, long visitId) {
        return DB.read(c -> { Visit v = new VisitDAO(c).findById(visitId); ensureOnList(v, dentist); return v; });
    }
    /** Earlier examinations of the same patient, if any. */
    public List<Visit> examHistory(Visit v) { return DB.read(c -> new VisitDAO(c).examHistoryOf(v.getPatientId(), v.getVisitId())); }
    public String medicalHistory(Visit v) { return DB.read(c -> new PatientDAO(c).medicalHistory(v.getPatientId())); }
    public void saveMedicalHistory(Account dentist, long visitId, String text) {
        text = Validator.trim(text); Validator.max(text, 1000, "Tiền sử bệnh");
        final String t = Validator.blank(text) ? null : text;
        DB.tx(c -> {
            Visit v = new VisitDAO(c).findById(visitId);
            ensureOnList(v, dentist);
            new PatientDAO(c).setMedicalHistory(v.getPatientId(), t);
            return null;
        });
    }

    static void ensureOnList(Visit v, Account dentist) {
        if (v == null || v.getDentistAccountId() != dentist.getAccountId()
                || !("WAITING".equals(v.getStatus()) || "IN_PROGRESS".equals(v.getStatus())))
            throw new BizException("Bạn chỉ có thể mở hồ sơ của các bệnh nhân trong danh sách của mình.");
    }

    /** UC20: Patient sees only own history (R03); a Dental Assistant can find any patient. Deleted forms are never shown. */
    public List<Visit> treatmentHistory(Account actor, String q) {
        return DB.read(c -> {
            PatientDAO pd = new PatientDAO(c); VisitDAO vd = new VisitDAO(c);
            java.util.ArrayList<Visit> out = new java.util.ArrayList<>();
            if ("Patient".equals(actor.getRoleName())) {
                Patient p = pd.findByPhone(actor.getPhone());
                if (p != null) out.addAll(vd.treatmentHistory(p.getPatientId()));
            } else if (Validator.blank(q)) {
                out.addAll(vd.recentTreatmentHistory(100));        // default view: latest 100 examinations
            } else {
                for (Patient p : pd.search(q)) out.addAll(vd.treatmentHistory(p.getPatientId()));
            }
            return out;
        });
    }
}
