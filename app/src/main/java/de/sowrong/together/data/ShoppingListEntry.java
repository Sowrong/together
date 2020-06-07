package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShoppingListEntry {
    private String entryId;
    private String userId;
    private String item;
    private LocalDateTime datetime;
    private DateTimeFormatter dateTimeFormatter;

    public ShoppingListEntry(String entryId, String userId, String item, String datetime) {
        this.entryId = entryId;
        this.userId = userId;
        this.item = item;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.datetime = LocalDateTime.parse(datetime, dateTimeFormatter);
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
