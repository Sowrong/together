package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Cleaning {
    private static String CLEANING_TAG = "data/Cleaning";
    private static Cleaning instance;

    private static HashMap<String, Duty> dutiesMap;
    private static HashMap<String, CleaningWeek> cleaningMap;

    private static ArrayList<CleaningDataListener> cleaningDataListeners;

    private static ValueEventListener dutiesValueEventListener;
    private static ValueEventListener cleaningWeeksValueEventListener;
    private static DatabaseReference dutiesDatabaseReference;
    private static DatabaseReference cleaningWeeksDatabaseReference;

    public static void clear() {
        if (dutiesDatabaseReference != null)
            dutiesDatabaseReference.removeEventListener(dutiesValueEventListener);
        if (cleaningWeeksDatabaseReference != null)
            cleaningWeeksDatabaseReference.removeEventListener(cleaningWeeksValueEventListener);
        dutiesValueEventListener = null;
        cleaningWeeksValueEventListener = null;
        dutiesDatabaseReference = null;
        cleaningWeeksDatabaseReference = null;

        cleaningMap = new HashMap<>();
        dutiesMap = new HashMap<>();
        cleaningDataListeners = new ArrayList<>();

        instance = null;
    }

    public Cleaning() {
        dutiesMap = new HashMap<>();
        cleaningMap = new HashMap<>();
        cleaningDataListeners = new ArrayList<>();
    }

    public static Cleaning getInstance() {
        if (instance == null) {
            instance = new Cleaning();
        }
        return instance;
    }

    public void setCleaningMap(HashMap<String, Duty> dutiesMap) {
        this.dutiesMap = dutiesMap;
    }

    public HashMap<String, CleaningWeek> getCleaningMap() {
        return cleaningMap;
    }

    public HashMap<String, Duty> getDutiesMap() {
        return dutiesMap;
    }

    ValueEventListener getDutyListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    dutiesMap.clear();

                    for (DataSnapshot dutiesSnapshot : dataSnapshot.getChildren()) {
                        String dutyId = dutiesSnapshot.getKey();
                        Duty duty = dutiesSnapshot.getValue(Duty.class);
                        dutiesMap.put(dutyId, duty);
                    }
                    notifyDutyDataChangedListeners(dutiesMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(CLEANING_TAG, "failed to read duties data", error.toException());
            }
        };
    }

    ValueEventListener getCleaningEventListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    cleaningMap.clear();

                    for (DataSnapshot cleaningWeekSnapshot : dataSnapshot.getChildren()) {
                        String cleaningWeekId = cleaningWeekSnapshot.getKey();
                        CleaningWeek cleaningWeek = new CleaningWeek(cleaningWeekId);

                        for (DataSnapshot cleaningWeekOverviewSnapshot : cleaningWeekSnapshot.getChildren()) {
                            if (cleaningWeekOverviewSnapshot.getKey().equals("userTasks")) {
                                for (DataSnapshot cleaningWeekTasksSnapshot : cleaningWeekOverviewSnapshot.getChildren()) {
                                    String taskId = cleaningWeekTasksSnapshot.getKey();
                                    CleaningWeekUserTask cleaningWeekUserTask = cleaningWeekTasksSnapshot.getValue(CleaningWeekUserTask.class);
                                    cleaningWeek.addUserTask(taskId, cleaningWeekUserTask);
                                }
                                cleaningMap.put(cleaningWeekId, cleaningWeek);
                            }
                        }
                    }
                    notifyCleaningDataChangedListeners(cleaningMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(CLEANING_TAG, "failed to read cleaning week data", error.toException());
            }
        };
    }

    void populate(String groupId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dutiesDatabaseReference = database.getReference("groups/" + Group.getInstance().getGroupId() + "/cleaning/duties");
        dutiesValueEventListener = getDutyListener(groupId);
        dutiesDatabaseReference.addListenerForSingleValueEvent(dutiesValueEventListener);
        dutiesDatabaseReference.addValueEventListener(dutiesValueEventListener);

        cleaningWeeksDatabaseReference = database.getReference("groups/" + Group.getInstance().getGroupId() + "/cleaning/weeks");
        cleaningWeeksValueEventListener = getCleaningEventListener(groupId);
        cleaningWeeksDatabaseReference.addListenerForSingleValueEvent(cleaningWeeksValueEventListener);
        cleaningWeeksDatabaseReference.addValueEventListener(cleaningWeeksValueEventListener);
    }

    public void addCleaningDataChangedListeners(CleaningDataListener listener) {
        cleaningDataListeners.add(listener);
    }
    public void removeCleaningDataChangedListeners(CalendarDataListener listener) {
        cleaningDataListeners.remove(listener);
    }

    protected void notifyCleaningDataChangedListeners(HashMap<String, CleaningWeek> cleaningMap) {
        for (CleaningDataListener listener : this.cleaningDataListeners) {
            listener.onCleaningDataChanged(cleaningMap);
        }
    }

    protected void notifyDutyDataChangedListeners(HashMap<String, Duty> dutiesMap) {
        for (CleaningDataListener listener : this.cleaningDataListeners) {
            listener.onDutyDataChanged(dutiesMap);
        }
    }

    public void deleteCurrentWeek() {
        String currentWeek = CleaningWeek.getWeekStringFromLocalDate(LocalDateTime.now());
        cleaningMap.remove(currentWeek);
    }

    public void syncCleaning() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        cleaningMap.entrySet().forEach(entry -> {
            DatabaseReference refCleaningWeek = database.getReference("groups/" + Group.getInstance().getGroupId() + "/cleaning/weeks/" + entry.getKey());
            refCleaningWeek.setValue(entry.getValue());
        });

        DatabaseReference refDuties = database.getReference("groups/" + Group.getInstance().getGroupId() + "/cleaning/duties");
        refDuties.setValue(dutiesMap);
    }
}
