package de.sowrong.together.data;

import java.util.HashMap;

public interface CalendarDataListener {
    public void onCalendarDataChanged(HashMap<String, CalendarEntry> calendar);
}