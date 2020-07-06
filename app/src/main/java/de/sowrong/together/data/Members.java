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

    public HashMap<String, Member> getMemberMap() {
        return membersMap;
    }

    public int getNumberMembers() {
        return membersMap.size();
    }

    ValueEventListener getMemberEventListener(String userId, String role) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);

                    Member member = membersMap.get(user.getId());

                    if (member == null) {
                        member = new Member(userId, user.getName(), user.getGroupId(), role);
                        membersMap.put(userId, member);

                        updateBalances(Transactions.getInstance().getTransactionMap());
                    }

                    // TODO Check here if groupID has changed
                    member.setName(user.getName());

                    notifyMemberDataChangedListeners(membersMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
            }
        };
    }

    void addMember(String userId, String role) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users/" + userId);
        ref.addListenerForSingleValueEvent(getMemberEventListener(userId, role));
        ref.addValueEventListener(getMemberEventListener(userId, role));
    }

    void populate(Group group) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        String groupId = group.getGroupId();

        DatabaseReference ref = database.getReference("groups/" + groupId + "/member");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot roleSnapshot : dataSnapshot.getChildren()) {
                                String userId = roleSnapshot.getKey();
                                Role role = roleSnapshot.getValue(Role.class);

                                addMember(userId, role.getRole());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
                    }
                }
        );

        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot roleSnapshot : dataSnapshot.getChildren()) {
                                String userId = roleSnapshot.getKey();
                                Role role = roleSnapshot.getValue(Role.class);

                                if (membersMap.containsKey(userId)) {
                                    //todo enable!
                                    //membersMap.get(userId).setRole(role.getRole());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
                    }
                }
        );

        Transactions.getInstance().addTransactionDataChangedListeners(transactionMap -> updateBalances(transactionMap));
    }

    void updateBalances(HashMap<String, Transaction> transactionMap) {
        membersMap.entrySet().forEach(entry -> {
            Member currentMember = entry.getValue();
            String currentMemberUserId = currentMember.getId();
            int numberMembers = Members.getInstance().getNumberMembers();

            double currentMemberTransactionValues = transactionMap.values().stream().
                    filter(transaction -> currentMemberUserId.equals(transaction.getUserId())).
                    mapToDouble(Transaction::getValue).sum();

            double currentMemberTotalTransactionValues = transactionMap.values().stream().mapToDouble(Transaction::getValue).sum();

            currentMember.setBalance(currentMemberTransactionValues - (currentMemberTotalTransactionValues / numberMembers));
        });

        notifyMemberDataChangedListeners(membersMap);
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

    public String getNameById(String userId) {
        for (Member member : membersMap.values()) {
            if (member.getId().equals(userId)) {
                return member.getName();
            }
        }
        return null;
    }
}
