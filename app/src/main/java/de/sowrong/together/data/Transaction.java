package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Comparable<Transaction> {
    private String transactionEntryId;
    private String userId;
    private String item;
    private LocalDateTime datetime;
    private double value;
    private DateTimeFormatter dateTimeFormatter;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter hourMinutesFormater;

    public Transaction(String item) {
        this.transactionEntryId = Group.randomId();
        this.userId = Users.getInstance().getOwnId();
        this.item = item;
        this.value = 0.0d;

        this.datetime = LocalDateTime.now();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.hourMinutesFormater = DateTimeFormatter.ofPattern("HH:mm");
    }

    public Transaction(String transactionEntryId, String userId, String item, String datetime, Double value) {
        this.transactionEntryId = transactionEntryId;
        this.userId = userId;
        this.item = item;
        this.value = value;

        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.hourMinutesFormater = DateTimeFormatter.ofPattern("HH:mm");
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

    public String getDatetimeString() {
        return datetime.format(dateTimeFormatter);
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

    public String getDate() {
        return datetime.format(dateFormatter);
    }

    public String getTime() {
        return datetime.format(hourMinutesFormater);
    }

    public String getValueString() {
        return String.format("%.2f", value);
    }

    public void save() {
        Transactions.getInstance().syncTransactions();
    }

    public void delete() {
        Transactions.getInstance().deleteTransaction(this.getTransactionEntryId());
        save();
    }

    @Override
    public int compareTo(Transaction other) {
        return this.datetime.compareTo(other.getDatetime());
    }

}
