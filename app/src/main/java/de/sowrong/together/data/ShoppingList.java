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

    ValueEventListener getShoppingListEventListener(String groupId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    shoppingListMap.clear();
                    for (DataSnapshot shoppingListEntrySnapshot : dataSnapshot.getChildren()) {
                        ShoppingListEntry shoppingListEntry = shoppingListEntrySnapshot.getValue(ShoppingListEntry.class);
                        shoppingListMap.put(shoppingListEntrySnapshot.getKey(), shoppingListEntry);
                        notifyShoppingListDataChangedListeners(shoppingListMap);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(SHOPPING_LIST_TAG, "failed to read user and group id", error.toException());
            }
        };
    }

    void populate(Group group) {
        String groupId = group.getGroupId();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("groups/" + Group.getInstance().getGroupId() + "/shoppingList");
        ref.addListenerForSingleValueEvent(getShoppingListEventListener(groupId));
        ref.addValueEventListener(getShoppingListEventListener(groupId));
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
        for (ShoppingListDataListener listener : this.shoppingListDataListeners) {
            listener.onShoppingListDataChanged(shoppingListMap);
        }
    }

    public ShoppingListEntry getShoppingListEntry(String entryId) {
        return shoppingListMap.get(entryId);
    }

    public void addShoppingListEntry(ShoppingListEntry shoppingListEntry) {
        shoppingListMap.put(shoppingListEntry.getEntryId(), shoppingListEntry);
    }

    public void deleteShoppingListEntry(String id) {
        shoppingListMap.remove(id);
    }

    public void syncShoppingList() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("groups/" + Group.getInstance().getGroupId() + "/shoppingList");
        ref.setValue(shoppingListMap);
    }
}
