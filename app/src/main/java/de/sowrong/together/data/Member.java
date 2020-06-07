package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Member {
    private static final String MEMBER_TAG = "data/Member";
    private String id;
    private String name;
    private String groupId;
    private double balance;

    public Member(String id, String name, String groupId) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("groups").orderByKey().equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals("members")) {
                            for (DataSnapshot memberSnapshot : childSnapshot.getChildren()) {
                                if (memberSnapshot.getKey().equals(id)) {
                                    for (DataSnapshot memberDataSnapshot : memberSnapshot.getChildren()) {
                                        if (memberDataSnapshot.getKey().equals("balance")) {
                                            balance = Double.parseDouble((String) memberDataSnapshot.getValue());
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
                Log.e(MEMBER_TAG, "failed to read user balance", error.toException());
            }
        });
    }

    public Member(String id, String name, String groupId, double balance) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
