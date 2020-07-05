package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShoppingListEntry {
    private String entryId;
    private String item;

    public ShoppingListEntry() {
        this.entryId = Group.randomId();
        this.item = "";
    }

    public ShoppingListEntry(String entryId, String item) {
        this.entryId = entryId;
        this.item = item;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void save() {
        ShoppingList.getInstance().syncShoppingList();
    }

    public void delete() {
        ShoppingList.getInstance().deleteShoppingListEntry(this.getEntryId());
        save();
    }
}
