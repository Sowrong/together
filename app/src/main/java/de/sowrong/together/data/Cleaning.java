package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Cleaning {
    private static String CLEANING_TAG = "data/Cleaning";
    private static Cleaning instance;

    private static HashMap<String, ArrayList<Duty>> dutiesMap;
    private static HashMap<String, CleaningWeek> cleaningMap;

    private static ArrayList<CleaningDataListener> cleaningDataListeners;

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


    public static HashMap<String, CleaningWeek> getCleaningMap() {
        return cleaningMap;
    }

    public static HashMap<String, ArrayList<Duty>> getDutiesMap() {
        return dutiesMap;
    }

    void populate(Group group) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        db.getReference().child("groups").orderByKey().equalTo(group.getGroupId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if ((childSnapshot.getKey().equals(group.getGroupId()))) {
                            for (DataSnapshot groupSnapshot : childSnapshot.getChildren()) {
                                if (groupSnapshot.getKey().equals("cleaning")) {
                                    for (DataSnapshot cleaningWeekSnapshot : groupSnapshot.getChildren()) {
                                        if (cleaningWeekSnapshot.getKey().equals("duties")) {
                                            for (DataSnapshot dutiesSnapshot : cleaningWeekSnapshot.getChildren()) {
                                                String dutyId = dutiesSnapshot.getKey();
                                                ArrayList<Duty> duties = new ArrayList<>();

                                                for (DataSnapshot dutySnapshot : dutiesSnapshot.getChildren()) {
                                                    String title = "";
                                                    String icon = "";

                                                    for (DataSnapshot dutyDetailsSnapshot : dutySnapshot.getChildren()) {
                                                        switch (dutyDetailsSnapshot.getKey()) {
                                                            case "title":
                                                                title = (String) dutyDetailsSnapshot.getValue();
                                                                break;
                                                            case "icon":
                                                                icon = (String) dutyDetailsSnapshot.getValue();
                                                                break;
                                                        }
                                                    }

                                                    if (!title.isEmpty() && !icon.isEmpty()) {
                                                        duties.add(new Duty(title, icon));
                                                    }
                                                }

                                                dutiesMap.put(dutyId, duties);
                                                notifyDutyDataChangedListeners(dutiesMap);
                                            }
                                        }

                                        else {
                                            String entryId = cleaningWeekSnapshot.getKey();
                                            CleaningWeek cleaningWeek = new CleaningWeek(entryId);

                                            for (DataSnapshot cleaningWeekMemberSnapshot : cleaningWeekSnapshot.getChildren()) {
                                                String userId = "";
                                                String dutyId = "";
                                                boolean finished = false;

                                                for (DataSnapshot cleaningWeekMemberDetailSnapshot : cleaningWeekMemberSnapshot.getChildren()) {
                                                    switch (cleaningWeekMemberDetailSnapshot.getKey()) {
                                                        case "userId":
                                                            userId = (String) cleaningWeekMemberDetailSnapshot.getValue();
                                                            break;
                                                        case "dutyId":
                                                            dutyId = (String) cleaningWeekMemberDetailSnapshot.getValue();
                                                            break;
                                                        case "finished":
                                                            finished = (Boolean) cleaningWeekMemberDetailSnapshot.getValue();
                                                            break;
                                                    }
                                                }

                                                if (!entryId.isEmpty() && !userId.isEmpty() && !dutyId.isEmpty()) {
                                                    cleaningWeek.addUserTask(entryId, userId, dutyId, finished);
                                                }
                                            }
                                            cleaningMap.put(entryId, cleaningWeek);
                                            notifyCleaningDataChangedListeners(cleaningMap);
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
                Log.e(CLEANING_TAG, "failed to read duties data", error.toException());
            }
        });
    }

    public void addCleaningDataChangedListeners(CleaningDataListener listener) {
        cleaningDataListeners.add(listener);
    }
    public void removeCleaningDataChangedListeners(CalendarDataListener listener) {
        cleaningDataListeners.remove(listener);
    }
    protected void notifyCleaningDataChangedListeners(HashMap<String, CleaningWeek> cleaningMap) {
        for (CleaningDataListener listener: this.cleaningDataListeners) {
            listener.onCleaningDataChanged(cleaningMap);
        }
    }
    protected void notifyDutyDataChangedListeners(HashMap<String, ArrayList<Duty>> dutyMap) {
        for (CleaningDataListener listener: this.cleaningDataListeners) {
            listener.onDutyDataChanged(dutyMap);
        }
    }
}
