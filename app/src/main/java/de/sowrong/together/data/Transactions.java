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

public class Transactions {
    private static String TRANSACTIONS_TAG = "data/Transactions";
    private static Transactions instance;
    private static HashMap<String, Transaction> transactionMap;
    private static ArrayList<TransactionDataListener> transactionDataListeners;
    private static DatabaseReference databaseReference;
    private static ValueEventListener valueEventListener;

    public static void clear() {
        if (databaseReference != null)
            databaseReference.removeEventListener(valueEventListener);
        valueEventListener = null;
        transactionMap = new HashMap<>();
        transactionDataListeners = new ArrayList<>();
        instance = null;
    }

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
                    transactionMap.clear();
                    for (DataSnapshot transactionsSnapshot : dataSnapshot.getChildren()) {
                        String transactionEntryId = transactionsSnapshot.getKey();
                        String userId = "";
                        String date = "";
                        String time = "";
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
                                case "valueString":
                                    value = Double.parseDouble((String) transactionEntryDetailSnapshot.getValue());
                                    break;
                                case "date":
                                    date = (String) transactionEntryDetailSnapshot.getValue();
                                    break;
                                case "time":
                                    time = (String) transactionEntryDetailSnapshot.getValue();
                                    break;
                            }
                        }

                        if (!userId.isEmpty() && !item.isEmpty() && !date.isEmpty() && !time.isEmpty() && value != null) {
                            transactionMap.put(transactionEntryId, new Transaction(transactionEntryId, userId, item, String.format("%s %s", date, time), value));
                            notifyTransactionDataChangedListeners(transactionMap);
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

    void populate(String groupId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("groups/" + Group.getInstance().getGroupId() + "/transactions");
        valueEventListener = getTransactionEventListener(groupId);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        databaseReference.addValueEventListener(valueEventListener);
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
        for (TransactionDataListener listener : this.transactionDataListeners) {
            listener.onTransactionDataChanged(transactionMap);
        }
    }

    public HashMap<String, Transaction> getTransactionMap() {
        return transactionMap;
    }

    public Transaction getTransaction(String transactionId) {
        return transactionMap.get(transactionId);
    }

    public void addTransaction(Transaction transaction) {
        transactionMap.put(transaction.getTransactionEntryId(), transaction);
    }

    public void deleteTransaction(String id) {
        transactionMap.remove(id);
        notifyTransactionDataChangedListeners(transactionMap);
    }

    public void syncTransactions() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("groups/" + Group.getInstance().getGroupId() + "/transactions");
        ref.setValue(transactionMap);
    }
}
