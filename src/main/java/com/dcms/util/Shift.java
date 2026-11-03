package com.dcms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/** The two working shifts of a day. Change the hours here and every screen follows. */
public enum Shift {
    MORNING("Ca sáng", "☀️", LocalTime.of(7, 30), LocalTime.of(11, 30)),
    AFTERNOON("Ca chiều", "🌤️", LocalTime.of(13, 30), LocalTime.of(17, 30));

    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final String label, icon;
    private final LocalTime start, end;

    Shift(String label, String icon, LocalTime start, LocalTime end) {
        this.label = label; this.icon = icon; this.start = start; this.end = end;
    }

    public String getCode() { return name(); }
    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getRange() { return fmt(start) + " – " + fmt(end); }
    public Shift other() { return this == MORNING ? AFTERNOON : MORNING; }

    /** True when this shift on the given date has already ended (a booking can no longer be placed or moved into it). */
    public boolean isOver(LocalDate d) { return !LocalDateTime.now(ZONE).isBefore(d.atTime(end)); }

    public static Shift parse(String s) {
        for (Shift x : values()) if (x.name().equals(s)) return x;
        return null;
    }

    private static String fmt(LocalTime t) { return t.getHour() + ":" + String.format("%02d", t.getMinute()); }
}
