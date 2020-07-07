package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Members {
    String MEMBERS_TAG = "data/Members";
    private static Members instance;
    private static HashMap<String, Member> membersMap;
    private static ArrayList<MemberDataListener> memberDataListener;

    private static DatabaseReference membersDatabaseReference;
    private static ValueEventListener membersValueEventListener;

    private static HashMap<String, DatabaseReference> memberDatabaseReferenceMap;
    private static HashMap<String, ValueEventListener> memberValueEventListenerMap;

    public static void clear() {
        membersMap.entrySet().forEach(
                member -> {
                    if (memberDatabaseReferenceMap != null) {
                        DatabaseReference databaseReference = memberDatabaseReferenceMap.get(member.getKey());
                        ValueEventListener valueEventListener = memberValueEventListenerMap.get(member.getKey());
                        if (databaseReference != null && valueEventListener != null)
                            databaseReference.removeEventListener(valueEventListener);
                    }
                }
        );
        membersDatabaseReference.removeEventListener(membersValueEventListener);
        memberDatabaseReferenceMap = new HashMap<>();
        memberValueEventListenerMap = new HashMap<>();
        membersMap = new HashMap<>();
        memberDataListener = new ArrayList<>();
        membersDatabaseReference = null;
        membersValueEventListener = null;
        instance = null;
    }

    public Members() {
        membersMap = new HashMap<>();
        memberDataListener = new ArrayList<>();
        memberDatabaseReferenceMap = new HashMap<>();
        memberValueEventListenerMap = new HashMap<>();
    }

    public HashMap<String, Member> getMemberMap() {
        return membersMap;
    }

    public int getNumberMembers() {
        return membersMap.size();
    }

    ValueEventListener getMemberEventListener(String userId, String role) {
        if (memberValueEventListenerMap.get(userId) == null) {
            memberValueEventListenerMap.put(userId, new ValueEventListener() {
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
            });
        }
        return memberValueEventListenerMap.get(userId);
    }

    void addMember(String userId, String role) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = database.getReference("users/" + userId);
        ValueEventListener valueEventListener = getMemberEventListener(userId, role);

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        databaseReference.addValueEventListener(valueEventListener);

        memberDatabaseReferenceMap.put(userId, databaseReference);
        memberValueEventListenerMap.put(userId, valueEventListener);
    }

    void populate(String groupId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        membersDatabaseReference = database.getReference("groups/" + groupId + "/member");
        membersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot roleSnapshot : dataSnapshot.getChildren()) {
                        String userId = roleSnapshot.getKey();
                        Role role = roleSnapshot.getValue(Role.class);

                        if (!membersMap.containsKey(userId)) {
                            addMember(userId, role.getRole());
                        }
                    }

                    notifyMemberDataChangedListeners(membersMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MEMBERS_TAG, "failed to read user and group id", error.toException());
            }
        };

        membersDatabaseReference.addListenerForSingleValueEvent(membersValueEventListener);
        membersDatabaseReference.addValueEventListener(membersValueEventListener);

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
        memberDataListener.add(listener);
    }
    public void removeMemberDataChangedListeners(MemberDataListener listener) {
        // Remove the listener from the list of the registered listeners
        memberDataListener.remove(listener);
    }

    protected void notifyMemberDataChangedListeners(HashMap<String, Member> memberDataList) {
        // Notify each of the listeners in the list of registered listeners
        for (MemberDataListener listener : this.memberDataListener) {
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
