package com.dcms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Which days can be booked: a rolling window from today (never the past) to the same weekday next week,
 * i.e. today + 7 days (Tuesday -> next Tuesday).
 */
public final class BookingWindow {
    private BookingWindow() {}
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate today() { return LocalDate.now(Shift.ZONE); }
    public static LocalDate last() { return today().plusDays(7); }
    public static boolean contains(LocalDate d) { return d != null && !d.isBefore(today()) && !d.isAfter(last()); }

    public static LocalDate parse(String s) {
        try { return s == null || s.isBlank() ? null : LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }

    public static String dayName(LocalDate d) {
        switch (d.getDayOfWeek()) {
            case MONDAY: return "Thứ 2"; case TUESDAY: return "Thứ 3"; case WEDNESDAY: return "Thứ 4";
            case THURSDAY: return "Thứ 5"; case FRIDAY: return "Thứ 6"; case SATURDAY: return "Thứ 7";
            default: return "Chủ nhật";
        }
    }

    /** e.g. "Thứ 3, 27/10/2026 – Ca sáng (7:30 – 11:30)". */
    public static String describe(LocalDate d, Shift s) {
        return dayName(d) + ", " + d.format(FULL) + " – " + s.getLabel() + " (" + s.getRange() + ")";
    }
}
