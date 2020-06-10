package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Members {
    String MEMBERS_TAG = "data/Members";
    private static Members instance;
    private static HashMap<String, Member> membersMap;
    private static ArrayList<MemberDataListener> listeners;

    public Members() {
        membersMap = new HashMap<>();
        listeners = new ArrayList<>();
    }

    ValueEventListener getMemberEventListener(String userId, Double userBalance) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String name = "";
                        String groupId = "";

                        if (userId.equals(childSnapshot.getKey())) {
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

                            Log.d(MEMBERS_TAG, "userId: " + userId + ", name: " + name + ", groupId: " + groupId);
                        }

                        Member member = new Member(userId, name, groupId, userBalance);
                        membersMap.put(userId, member);

                        notifyMemberDataChangedListeners(membersMap);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
            }
        };
    }

    ValueEventListener getBalanceEventListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if ((childSnapshot.getKey().equals(groupId))) {
                            for (DataSnapshot groupSnapshot : childSnapshot.getChildren()) {
                                if (groupSnapshot.getKey().equals("balances")) {
                                    for (DataSnapshot memberSnapshot : groupSnapshot.getChildren()) {
                                        String id = memberSnapshot.getKey();
                                        Double balance = null;
                                        for (DataSnapshot memberDataSnapshot : memberSnapshot.getChildren()) {
                                            if (memberDataSnapshot.getKey().equals("balance")) {
                                                balance = Double.parseDouble((String) memberDataSnapshot.getValue());
                                            }
                                        }

                                        if (balance != null)
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
        };
    }

    void addMember(String userId, Double userBalance) {
        Query databaseQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(userId);
        ValueEventListener memberEventListener = getMemberEventListener(userId, userBalance);

        databaseQuery.addListenerForSingleValueEvent(memberEventListener);
        databaseQuery.addValueEventListener(memberEventListener);
    }

    void populate(Group group) {
        String groupId = group.getGroupId();

        Query databaseQuery = FirebaseDatabase.getInstance().getReference().child("groups").orderByKey().equalTo(groupId);
        ValueEventListener valueEventListener = getBalanceEventListener(groupId);

        databaseQuery.addListenerForSingleValueEvent(valueEventListener);
        databaseQuery.addValueEventListener(valueEventListener);
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

    public static String getNameById(HashMap<String, Member> membersMap, String userId) {
        for (Member member: membersMap.values()) {
            if (member.getId().equals(userId)) {
                return member.getName();
            }
        }
        return null;
    }
}
