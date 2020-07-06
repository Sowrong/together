package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Group {
    private static final String GROUP_TAG = "data/Group";
    public static final int ID_LENGTH = 128;

    private static String ownUserId;
    private static String groupId;

    private static Group instance;
    private static ArrayList<MemberDataListener> memberDataListeners;
    private static ArrayList<CleaningDataListener> cleaningDataListeners;
    private static ArrayList<CalendarDataListener> calendarDataListeners;
    private static ArrayList<ShoppingListDataListener> shoppingListDataListeners;
    private static ArrayList<TransactionDataListener> transactionDataListeners;

    public Group() {
        memberDataListeners = new ArrayList<>();
        cleaningDataListeners = new ArrayList<>();
        calendarDataListeners = new ArrayList<>();
        shoppingListDataListeners = new ArrayList<>();
        transactionDataListeners = new ArrayList<>();
        groupId = "";

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e(GROUP_TAG, "FIREBASE ERROR: user not logged in");
            return;
        }

        ownUserId = user.getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("users").orderByKey().equalTo(ownUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals(ownUserId)) {
                            for (DataSnapshot userDetailSnapshot : userSnapshot.getChildren()) {
                                if (userDetailSnapshot.getKey().equals("groupId")) {
                                    groupId = (String) userDetailSnapshot.getValue();
                                    Log.d(GROUP_TAG, "user " + ownUserId + " found, adding members to group: " + groupId);

                                    Transactions transactions = Transactions.getInstance();
                                    transactions.populate(instance);
                                    transactions.addTransactionDataChangedListeners(transactionsMap -> notifyTransactionDataChangedListeners(transactionsMap));

                                    Members members = Members.getInstance();
                                    members.populate(instance);
                                    members.addMemberDataChangedListeners(membersMap -> notifyMemberDataChangedListeners(membersMap));

                                    Cleaning cleaning = Cleaning.getInstance();
                                    cleaning.populate(instance);
                                    cleaning.addCleaningDataChangedListeners(new CleaningDataListener() {
                                        @Override
                                        public void onCleaningDataChanged(HashMap<String, CleaningWeek> cleaningMap) {
                                            notifyCleaningDataChangedListeners(cleaningMap);
                                        }

                                        @Override
                                        public void onDutyDataChanged(HashMap<String, Duty> dutiesMap) {
                                            notifyDutyDataChangedListeners(dutiesMap);
                                        }
                                    });

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.populate(instance);
                                    calendar.addCalendarDataChangedListeners(calendarMap -> notifyCalendarDataChangedListeners(calendarMap));

                                    ShoppingList shoppingList = ShoppingList.getInstance();
                                    shoppingList.populate(instance);
                                    shoppingList.addShoppingListDataChangedListeners(shoppingListMap -> notifyShoppingListDataChangedListeners(shoppingListMap));
                                }
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
    }

    public static Group getInstance() {
        if (instance == null) {
            instance = new Group();
        }
        return instance;
    }

    public static String getOwnUserId() {
        return ownUserId;
    }

    public static String getGroupId() {
        return groupId;
    }


    public void addMemberDataChangedListeners(MemberDataListener listener) {
        memberDataListeners.add(listener);
    }
    public void removeMemberDataChangedListeners(MemberDataListener listener) {
        memberDataListeners.remove(listener);
    }
    protected void notifyMemberDataChangedListeners(HashMap<String, Member> membersMap) {
        for (MemberDataListener listener: this.memberDataListeners) {
            listener.onMemberDataChanged(membersMap);
        }
    }


    public void addTransactionDataChangedListeners(TransactionDataListener listener) {
        transactionDataListeners.add(listener);
    }
    public void removeTransactionDataChangedListeners(TransactionDataListener listener) {
        transactionDataListeners.remove(listener);
    }
    protected void notifyTransactionDataChangedListeners(HashMap<String, Transaction> transactionMap) {
        for (TransactionDataListener listener: this.transactionDataListeners) {
            listener.onTransactionDataChanged(transactionMap);
        }
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

    protected void notifyDutyDataChangedListeners(HashMap<String, Duty> dutyMap) {
        for (CleaningDataListener listener : this.cleaningDataListeners) {
            listener.onDutyDataChanged(dutyMap);
        }
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


    public void addShoppingListDataChangedListeners(ShoppingListDataListener listener) {
        shoppingListDataListeners.add(listener);
    }

    public void removeShoppingListDataChangedListeners(ShoppingListDataListener listener) {
        shoppingListDataListeners.remove(listener);
    }

    protected void notifyShoppingListDataChangedListeners(HashMap<String, ShoppingListEntry> shoppingListMap) {
        for (ShoppingListDataListener listener : this.shoppingListDataListeners) {
            listener.onShoppingListDataChanged(shoppingListMap);
        }
    }

    public static String randomId(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
