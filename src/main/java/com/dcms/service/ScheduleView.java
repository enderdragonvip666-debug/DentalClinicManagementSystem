package com.dcms.service;

import com.dcms.model.ConsultationRequest;
import com.dcms.util.Shift;
import java.time.LocalDate;
import java.util.List;

/** Read model for the receptionist's schedule: day tabs, one panel per shift, and overdue bookings. */
public class ScheduleView {
    public static class Panel {
        private final Shift shift, other; private final boolean open, otherOpen; private final List<ConsultationRequest> rows;
        public Panel(Shift shift, LocalDate day, List<ConsultationRequest> rows) {
            this.shift = shift; this.other = shift.other(); this.rows = rows;
            this.open = !shift.isOver(day); this.otherOpen = !other.isOver(day);
        }
        public String getCode() { return shift.getCode(); }
        public String getLabel() { return shift.getLabel(); }
        public String getIcon() { return shift.getIcon(); }
        public String getRange() { return shift.getRange(); }
        public String getOtherCode() { return other.getCode(); }
        public String getOtherLabel() { return other.getLabel().toLowerCase(); }
        public boolean isOpen() { return open; }
        public boolean isOtherOpen() { return otherOpen; }
        public List<ConsultationRequest> getRows() { return rows; }
    }

    private final List<DayOption> days; private final DayOption selected; private final List<Panel> panels;
    private final List<ConsultationRequest> overdue, results;   // results != null means a code/phone search is shown
    public ScheduleView(List<DayOption> days, DayOption selected, List<Panel> panels, List<ConsultationRequest> overdue, List<ConsultationRequest> results) {
        this.days = days; this.selected = selected; this.panels = panels; this.overdue = overdue; this.results = results;
    }
    public boolean isSearching() { return results != null; }
    public List<ConsultationRequest> getResults() { return results; }
    public List<DayOption> getDays() { return days; }
    public DayOption getSelected() { return selected; }
    public List<Panel> getPanels() { return panels; }
    public List<ConsultationRequest> getOverdue() { return overdue; }
}
