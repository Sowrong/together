package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingList {
    private static String SHOPPING_LIST_TAG = "data/ShoppingList";
    private static ShoppingList instance;
    private static HashMap<String, ShoppingListEntry> shoppingListMap;
    private static ArrayList<ShoppingListDataListener> shoppingListDataListeners;

    public ShoppingList() {
        shoppingListMap = new HashMap<>();
        shoppingListDataListeners = new ArrayList<>();
    }

    public static ShoppingList getInstance() {
        if (instance == null) {
            instance = new ShoppingList();
        }
        return instance;
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
                                if (groupSnapshot.getKey().equals("shoppingList")) {
                                    for (DataSnapshot shoppingListEntrySnapshot : groupSnapshot.getChildren()) {
                                        String shoppingListEntryId = shoppingListEntrySnapshot.getKey();
                                        String userId = "";
                                        String datetime = "";
                                        String item = "";

                                        for (DataSnapshot shoppingListEntryDetailSnapshot : shoppingListEntrySnapshot.getChildren()) {
                                            switch (shoppingListEntryDetailSnapshot.getKey()) {
                                                case "userId":
                                                    userId = (String) shoppingListEntryDetailSnapshot.getValue();
                                                    break;
                                                case "item":
                                                    item = (String) shoppingListEntryDetailSnapshot.getValue();
                                                    break;
                                                case "datetime":
                                                    datetime = (String) shoppingListEntryDetailSnapshot.getValue();
                                                    break;
                                            }
                                        }

                                        if (!userId.isEmpty() && !item.isEmpty() && !datetime.isEmpty()) {
                                            shoppingListMap.put(shoppingListEntryId, new ShoppingListEntry(shoppingListEntryId, userId, item, datetime));
                                            notifyShoppingListDataChangedListeners(shoppingListMap);
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
                Log.e(SHOPPING_LIST_TAG, "failed to read user and group id", error.toException());
            }
        });
    }

    public static HashMap<String, ShoppingListEntry> getShoppingListMap() {
        return shoppingListMap;
    }

    public void addShoppingListDataChangedListeners(ShoppingListDataListener listener) {
        shoppingListDataListeners.add(listener);
    }
    public void removeShoppingListDataChangedListeners(ShoppingListDataListener listener) {
        shoppingListDataListeners.remove(listener);
    }
    protected void notifyShoppingListDataChangedListeners(HashMap<String, ShoppingListEntry> shoppingListMap) {
        for (ShoppingListDataListener listener: this.shoppingListDataListeners) {
            listener.onShoppingListDataChanged(shoppingListMap);
        }
    }
}
