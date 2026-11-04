package com.dcms.service;

import com.dcms.util.BookingWindow;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** One bookable day for the date pickers and the schedule tabs. */
public class DayOption {
    private final String value, dayName, shortDate;
    private final boolean today, morningOpen, afternoonOpen;
    private final int morningCount, afternoonCount;

    public DayOption(LocalDate d, boolean morningOpen, boolean afternoonOpen, int morningCount, int afternoonCount) {
        this.value = d.toString(); this.dayName = BookingWindow.dayName(d);
        this.shortDate = d.format(DateTimeFormatter.ofPattern("dd/MM"));
        this.today = d.equals(BookingWindow.today());
        this.morningOpen = morningOpen; this.afternoonOpen = afternoonOpen;
        this.morningCount = morningCount; this.afternoonCount = afternoonCount;
    }
    public String getValue() { return value; }
    public String getDayName() { return dayName; }
    public String getShortDate() { return shortDate; }
    public String getLabel() { return dayName + ", " + shortDate; }
    public boolean isToday() { return today; }
    public boolean isMorningOpen() { return morningOpen; }
    public boolean isAfternoonOpen() { return afternoonOpen; }
    public boolean isOpen() { return morningOpen || afternoonOpen; }
    public int getMorningCount() { return morningCount; }
    public int getAfternoonCount() { return afternoonCount; }
}
