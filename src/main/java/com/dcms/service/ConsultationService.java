package com.dcms.service;

import com.dcms.dao.*;
import com.dcms.model.*;
import com.dcms.util.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/** BF-01 first half: book an exam slot (date + shift), view the schedule, move a booking to the other shift, cancel. */
public class ConsultationService {

    /** A Patient uses the account name and phone (the Dental Assistant books for the patient). Returns the booking code. */
    public String request(Account actor, String fullName, String phone, long serviceId, String note, String dateStr, String shiftStr) {
        if (actor != null && "Patient".equals(actor.getRoleName())) { fullName = actor.getFullName(); phone = actor.getPhone(); }
        fullName = Validator.trim(fullName); phone = Validator.trim(phone); note = Validator.trim(note);
        Validator.required(fullName, "Họ và tên"); Validator.max(fullName, 150, "Họ và tên");
        Validator.phoneOk(phone); Validator.max(note, 500, "Ghi chú");
        final LocalDate date = BookingWindow.parse(dateStr);
        if (date == null || !BookingWindow.contains(date)) throw new BizException(Msg.MSG41);
        final Shift shift = Shift.parse(shiftStr);
        if (shift == null) throw new BizException(Msg.MSG42);
        if (shift.isOver(date)) throw new BizException(Msg.MSG43);
        final String n = fullName, p = phone, nt = Validator.blank(note) ? null : note;
        return DB.tx(c -> {
            DentalService s = new DentalServiceDAO(c).findById(serviceId);
            if (s == null || !"ACTIVE".equals(s.getStatus())) throw new BizException("Vui lòng chọn dịch vụ nha khoa.");
            ConsultationRequestDAO rd = new ConsultationRequestDAO(c);
            if (rd.findOpenByPhone(p) != null) throw new BizException(Msg.MSG16);      // one open booking per phone
            for (int i = 0; ; i++) {
                String code = PasswordUtil.requestCode();
                try { rd.insert(code, n, p, serviceId, nt, date, shift.name()); return code; }
                catch (SQLException e) { if (i > 4 || !String.valueOf(e.getMessage()).contains("request_code")) throw e; }
            }
        });
    }

    /** Whitelist of receptionist's status choices. */
    public static String normalizeStatus(String value) {
        if ("REQUESTED".equals(value) || "WAITING".equals(value) || "IN_PROGRESS".equals(value)
                || "AWAITING_PAYMENT".equals(value)) return value;
        return "ALL";
    }

    private static List<DayOption> buildDays(ConsultationRequestDAO rd, String status) throws SQLException {
        LocalDate from = BookingWindow.today(), to = BookingWindow.last();
        Map<String, int[]> cnt = new HashMap<>();
        for (Object[] o : rd.counts(from, to, status)) {
            int[] a = cnt.computeIfAbsent((String) o[0], k -> new int[2]);
            a["MORNING".equals(o[1]) ? 0 : 1] += (Integer) o[2];
        }
        List<DayOption> out = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            int[] a = cnt.getOrDefault(d.toString(), new int[2]);
            out.add(new DayOption(d, !Shift.MORNING.isOver(d), !Shift.AFTERNOON.isOver(d), a[0], a[1]));
        }
        return out;
    }

    /** Bookable days (today .. today + 7 days) for the date pickers. */
    public List<DayOption> days() { return DB.read(c -> buildDays(new ConsultationRequestDAO(c), "ALL")); }

    /** The receptionist's schedule for one day; a non-blank q searches open bookings of all days by code or phone. An out-of-range date falls back to today. */
    public ScheduleView schedule(String dateStr, String q) { return schedule(dateStr, q, "ALL"); }

    /** One day of the receptionist schedule, filtered by true check-in/exam/payment status. */
    public ScheduleView schedule(String dateStr, String q, String requestedStatus) {
        final String status = normalizeStatus(requestedStatus);
        LocalDate d = BookingWindow.parse(dateStr);
        final LocalDate day = (d == null || !BookingWindow.contains(d)) ? BookingWindow.today() : d;
        return DB.read(c -> {
            ConsultationRequestDAO rd = new ConsultationRequestDAO(c);
            List<DayOption> days = buildDays(rd, status);
            DayOption sel = days.get(0);
            for (DayOption o : days) if (o.getValue().equals(day.toString())) sel = o;
            List<ConsultationRequest> rows = rd.listOpenOn(day, status);
            List<ScheduleView.Panel> panels = new ArrayList<>();
            for (Shift s : Shift.values()) {
                List<ConsultationRequest> part = new ArrayList<>();
                for (ConsultationRequest r : rows) if (s.name().equals(r.getShift())) part.add(r);
                panels.add(new ScheduleView.Panel(s, day, part));
            }
            List<ConsultationRequest> found = Validator.blank(q) ? null : rd.searchOpen(q, status);
            return new ScheduleView(days, sel, panels,
                    "ALL".equals(status) || "REQUESTED".equals(status) ? rd.listOverdue(BookingWindow.today()) : java.util.Collections.emptyList(), found);
        });
    }

    /** A Patient sees the bookings whose phone equals the account phone, including older ones. */
    public List<ConsultationRequest> listMine(Account patient) { return DB.read(c -> new ConsultationRequestDAO(c).listByPhone(patient.getPhone())); }

    /** Receptionist can move a not-yet-checked-in booking to any valid day and shift. */
    public String reschedule(long requestId, String targetDate, String targetShift) {
        final LocalDate day = BookingWindow.parse(targetDate);
        final Shift shift = Shift.parse(targetShift);
        if (day == null || !BookingWindow.contains(day)) throw new BizException(Msg.MSG41);
        if (shift == null) throw new BizException(Msg.MSG42);
        if (shift.isOver(day)) throw new BizException(Msg.MSG43);
        return DB.tx(c -> {
            ConsultationRequestDAO rd = new ConsultationRequestDAO(c);
            ConsultationRequest r = rd.findByIdForUpdate(requestId);
            if (r == null || !"REQUESTED".equals(r.getStatus())) throw new BizException("Chỉ có thể đổi lịch trước khi bệnh nhân check-in.");
            if (day.equals(r.getApptDate().toLocalDate()) && shift.name().equals(r.getShift()))
                throw new BizException("Vui lòng chọn ngày hoặc ca khác lịch hẹn hiện tại.");
            rd.reschedule(requestId, day, shift.name());
            return BookingWindow.describe(day, shift);
        });
    }

    /** A Patient may cancel only own bookings; a Dental Assistant any open booking. */
    public void cancel(Account actor, long requestId) {
        DB.tx(c -> {
            ConsultationRequestDAO rd = new ConsultationRequestDAO(c);
            ConsultationRequest r = rd.findByIdForUpdate(requestId);
            if (r == null) throw new BizException(Msg.MSG20);
            if ("Patient".equals(actor.getRoleName()) && !actor.getPhone().equals(r.getPhone())) throw new BizException(Msg.MSG20);
            if (!"REQUESTED".equals(r.getStatus())) throw new BizException(Msg.MSG20);
            rd.setStatus(requestId, "CANCELLED", null);
            return null;
        });
    }
}
