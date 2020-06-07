package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Transactions {
    private static String TRANSACTIONS_TAG = "data/Transactions";
    private static Transactions instance;
    private static HashMap<String, Transaction> transactionMap;
    private static ArrayList<TransactionDataListener> transactionDataListeners;

    public Transactions() {
        transactionMap = new HashMap<>();
        transactionDataListeners = new ArrayList<>();
    }

    public static Transactions getInstance() {
        if (instance == null) {
            instance = new Transactions();
        }
        return instance;
    }

    ValueEventListener getTransactionEventListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if ((childSnapshot.getKey().equals(groupId))) {
                            for (DataSnapshot groupSnapshot : childSnapshot.getChildren()) {
                                if (groupSnapshot.getKey().equals("transactions")) {
                                    for (DataSnapshot transactionsSnapshot : groupSnapshot.getChildren()) {
                                        String transactionEntryId = transactionsSnapshot.getKey();
                                        String userId = "";
                                        String datetime = "";
                                        String item = "";
                                        Double value = null;

                                        for (DataSnapshot transactionEntryDetailSnapshot : transactionsSnapshot.getChildren()) {
                                            switch (transactionEntryDetailSnapshot.getKey()) {
                                                case "userId":
                                                    userId = (String) transactionEntryDetailSnapshot.getValue();
                                                    break;
                                                case "item":
                                                    item = (String) transactionEntryDetailSnapshot.getValue();
                                                    break;
                                                case "datetime":
                                                    datetime = (String) transactionEntryDetailSnapshot.getValue();
                                                    break;
                                                case "value":
                                                    value = Double.parseDouble((String) transactionEntryDetailSnapshot.getValue());
                                                    break;
                                            }
                                        }

                                        if (!userId.isEmpty() && !item.isEmpty() && !datetime.isEmpty() && value != null) {
                                            transactionMap.put(transactionEntryId, new Transaction(transactionEntryId, userId, item, datetime, value));
                                            notifyTransactionDataChangedListeners(transactionMap);
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
                Log.e(TRANSACTIONS_TAG, "failed to read transactions data", error.toException());
            }
        };
    }

    void populate(Group group) {
        String groupId = group.getGroupId();
        Query databaseQuery = FirebaseDatabase.getInstance().getReference().child("groups").orderByKey().equalTo(groupId);

        databaseQuery.addListenerForSingleValueEvent(getTransactionEventListener(groupId));
        databaseQuery.addValueEventListener(getTransactionEventListener(groupId));
    }

    public static HashMap<String, Transaction> getShoppingListMap() {
        return transactionMap;
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
}
