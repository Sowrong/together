package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if ((childSnapshot.getKey().equals(groupId))) {
                            for (DataSnapshot groupSnapshot : childSnapshot.getChildren()) {
                                if (groupSnapshot.getKey().equals("calendar")) {
                                    for (DataSnapshot calendarEntrySnapshot : groupSnapshot.getChildren()) {
                                        String entryId = calendarEntrySnapshot.getKey();
                                        String userId = "";
                                        String title = "";
                                        String description = "";
                                        String datetime = "";

                                        for (DataSnapshot calendarEntryDetailSnapshot : calendarEntrySnapshot.getChildren()) {
                                            switch (calendarEntryDetailSnapshot.getKey()) {
                                                case "userId":
                                                    userId = (String) calendarEntryDetailSnapshot.getValue();
                                                    break;
                                                case "title":
                                                    title = (String) calendarEntryDetailSnapshot.getValue();
                                                    break;
                                                case "description":
                                                    description = (String) calendarEntryDetailSnapshot.getValue();
                                                    break;
                                                case "datetime":
                                                    datetime = (String) calendarEntryDetailSnapshot.getValue();
                                                    break;
                                            }
                                        }

                                        if (!userId.isEmpty() && !title.isEmpty() && !description.isEmpty() && !datetime.isEmpty()) {
                                            calendarMap.put(entryId, new CalendarEntry(entryId, userId, title, description, datetime));
                                            notifyCalendarDataChangedListeners(calendarMap);
                                        }
                                    }
                                }
                            }
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

        Query databaseQuery = FirebaseDatabase.getInstance().getReference().child("groups").orderByKey().equalTo(groupId);

        databaseQuery.addListenerForSingleValueEvent(getCalendarEventListener(groupId));
        databaseQuery.addValueEventListener(getCalendarEventListener(groupId));
    }

    public void addCalendarDataChangedListeners(CalendarDataListener listener) {
        calendarDataListeners.add(listener);
    }
    public void removeCalendarDataChangedListeners(CalendarDataListener listener) {
        calendarDataListeners.remove(listener);
    }
    protected void notifyCalendarDataChangedListeners(HashMap<String, CalendarEntry> calendarMap) {
        for (CalendarDataListener listener: this.calendarDataListeners) {
            listener.onCalendarDataChanged(calendarMap);
        }
    }
}
