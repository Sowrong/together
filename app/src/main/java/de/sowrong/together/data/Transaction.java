package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionEntryId;
    private String userId;
    private String item;
    private LocalDateTime datetime;
    private double value;
    private DateTimeFormatter dateTimeFormatter;

    public Transaction(String transactionEntryId, String userId, String item, String datetime, Double value) {
        this.transactionEntryId = transactionEntryId;
        this.userId = userId;
        this.item = item;
        this.value = value;

        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.datetime = LocalDateTime.parse(datetime, dateTimeFormatter);
    }

    public String getTransactionEntryId() {
        return transactionEntryId;
    }

    public void setTransactionEntryId(String transactionEntryId) {
        this.transactionEntryId = transactionEntryId;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
