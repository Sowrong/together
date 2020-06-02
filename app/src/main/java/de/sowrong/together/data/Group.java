package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private static final String GROUP_TAG = "data/Group";
    private static String userId;
    private static String groupId;
    private static HashMap<String, Member> members;
    private static HashMap<String, CleaningWeek> cleaning;
    private static HashMap<String, Duty> cleaningDuties;
    private static HashMap<String, CalendarEntry> calendar;
    private static HashMap<String, ShoppingListEntry> shoppingList;
    private static HashMap<String, Transaction> transactions;

    private static Group instance;
    private static ArrayList<MemberDataListener> memberDataListeners;
    private static ArrayList<CleaningDataListener> cleaningDataListeners;
    private static ArrayList<CalendarDataListener> calendarDataListeners;
    private static ArrayList<ShoppingListDataListener> shoppingListDataListeners;
    private static ArrayList<TransactionDataListener> transactionDataListeners;


    public Group() {
        memberDataListeners = new ArrayList<>();
        cleaningDataListeners = new ArrayList<>();
        calendarDataListeners  = new ArrayList<>();
        shoppingListDataListeners = new ArrayList<>();
        transactionDataListeners = new ArrayList<>();
        groupId = "";

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userId = user.getUid();

        db.getReference().child("users").orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        String id = childSnapshot.getKey();

                        for (DataSnapshot userDetailSnapshot: childSnapshot.getChildren()) {
                            if (user.getUid().equals(id) && userDetailSnapshot.getKey().equals("groupId")) {
                                groupId = (String) userDetailSnapshot.getValue();
                                Log.d(GROUP_TAG, "userId: " + userId + ", groupId: " + groupId);

                                Member member = new Member(groupId);
                                member.addMemberDataChangedListeners(memberDataMap -> {
                                    members = memberDataMap;
                                    Log.d(GROUP_TAG, "Member data updated");
                                });



                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(GROUP_TAG, "failed to read user and group id", error.toException());
            }
        });



        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference usersReference = db.getReference().child("users");

    }


}
