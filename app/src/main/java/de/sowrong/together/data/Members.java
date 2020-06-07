package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Members {
    String MEMBERS_TAG = "data/Members";
    private static Members instance;
    private static HashMap<String, Member> membersMap;
    private static ArrayList<MemberDataListener> listeners;

    public Members() {
        membersMap = new HashMap<>();
        listeners = new ArrayList<>();
    }

    void addMember(String id, Double balance) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("users").orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String name = "";
                        String groupId = "";

                        if (id.equals(childSnapshot.getKey())) {
                            for (DataSnapshot userDetailSnapshot : childSnapshot.getChildren()) {
                                switch (userDetailSnapshot.getKey()) {
                                    case "name":
                                        name = (String) userDetailSnapshot.getValue();
                                        break;
                                    case "groupId":
                                        groupId = (String) userDetailSnapshot.getValue();
                                        break;
                                }
                            }

                            Log.d(MEMBERS_TAG, "userId: " + id + ", name: " + name + ", groupId: " + groupId);
                        }

                        if (balance == null) {
                            Member member = new Member(id, name, groupId);
                            membersMap.put(id, member);
                        }
                        else {
                            Member member = new Member(id, name, groupId, balance);
                            membersMap.put(id, member);
                        }

                        notifyMemberDataChangedListeners(membersMap);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
            }
        });
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
                                if (groupSnapshot.getKey().equals("members")) {
                                    for (DataSnapshot memberSnapshot : groupSnapshot.getChildren()) {
                                        String id = memberSnapshot.getKey();
                                        Double balance = null;
                                        for (DataSnapshot memberDataSnapshot : memberSnapshot.getChildren()) {
                                            if (memberDataSnapshot.getKey().equals("balance")) {
                                                balance = Double.parseDouble((String) memberDataSnapshot.getValue());
                                            }
                                        }
                                        addMember(id, balance);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
            }
        });
    }

    public static Members getInstance() {
        if (instance == null) {
            instance = new Members();
        }
        return instance;
    }

    public static HashMap<String, Member> getMembersMap() {
        return membersMap;
    }

    public void addMemberDataChangedListeners(MemberDataListener listener) {
        // Add the listener to the list of registered listeners
        listeners.add(listener);
    }
    public void removeMemberDataChangedListeners(MemberDataListener listener) {
        // Remove the listener from the list of the registered listeners
        listeners.remove(listener);
    }

    protected void notifyMemberDataChangedListeners(HashMap<String, Member> memberDataList) {
        // Notify each of the listeners in the list of registered listeners
        for (MemberDataListener listener: this.listeners) {
            listener.onMemberDataChanged(memberDataList);
        }
    }
}
