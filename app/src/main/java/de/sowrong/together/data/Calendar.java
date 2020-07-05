package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Calendar {
    private static String CALENDAR_TAG = "data/Calendar";
    private static Calendar instance;
    private static HashMap<String, CalendarEntry> calendarMap;
    private static ArrayList<CalendarDataListener> calendarDataListeners;

    public Calendar() {
        calendarMap = new HashMap<>();
        calendarDataListeners = new ArrayList<>();
    }

    public static Calendar getInstance() {
        if (instance == null) {
            instance = new Calendar();
        }
        return instance;
    }

    public static HashMap<String, CalendarEntry> getCalendarMap() {
        return calendarMap;
    }

    ValueEventListener getCalendarEventListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    calendarMap.clear();
                    for (DataSnapshot calendarEntrySnapshot : dataSnapshot.getChildren()) {
                        String entryId = calendarEntrySnapshot.getKey();
                        String userId = "";
                        String title = "";
                        String details = "";
                        String date = "";
                        String time = "";

                        for (DataSnapshot calendarEntryDetailSnapshot : calendarEntrySnapshot.getChildren()) {
                            switch (calendarEntryDetailSnapshot.getKey()) {
                                case "userId":
                                    userId = (String) calendarEntryDetailSnapshot.getValue();
                                    break;
                                case "title":
                                    title = (String) calendarEntryDetailSnapshot.getValue();
                                    break;
                                case "details":
                                    details = (String) calendarEntryDetailSnapshot.getValue();
                                    break;
                                case "date":
                                    date = (String) calendarEntryDetailSnapshot.getValue();
                                    break;
                                case "time":
                                    time = (String) calendarEntryDetailSnapshot.getValue();
                                    break;
                            }
                        }

                        if (!userId.isEmpty() && !title.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                            calendarMap.put(entryId, new CalendarEntry(entryId, userId, title, details, String.format("%s %s", date, time)));
                            notifyCalendarDataChangedListeners(calendarMap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(CALENDAR_TAG, "failed to read calendar data", error.toException());
            }
        };
    }

    void populate(Group group) {
        String groupId = group.getGroupId();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("groups/" + Group.getInstance().getGroupId() + "/calendar");
        ref.addListenerForSingleValueEvent(getCalendarEventListener(groupId));
        ref.addValueEventListener(getCalendarEventListener(groupId));
    }

    public void addCalendarDataChangedListeners(CalendarDataListener listener) {
        calendarDataListeners.add(listener);
    }

    public void removeCalendarDataChangedListeners(CalendarDataListener listener) {
        calendarDataListeners.remove(listener);
    }

    protected void notifyCalendarDataChangedListeners(HashMap<String, CalendarEntry> calendarMap) {
        for (CalendarDataListener listener : this.calendarDataListeners) {
            listener.onCalendarDataChanged(calendarMap);
        }
    }

    public CalendarEntry getCalendarEntry(String id) {
        return calendarMap.get(id);
    }

    public void addCalendarEntry(CalendarEntry calendarEntry) {
        calendarMap.put(calendarEntry.getEntryId(), calendarEntry);
    }

    public void deleteCalendarEntry(String id) {
        calendarMap.remove(id);
    }

    public void syncCalendar() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("groups/" + Group.getInstance().getGroupId() + "/calendar");
        ref.setValue(calendarMap);
    }
}
