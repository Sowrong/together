package de.sowrong.together.data;

import java.util.HashMap;

public interface ShoppingListDataListener {
        public void onShoppingListDataChanged(HashMap<String, ShoppingListEntry> shoppingList);
    }