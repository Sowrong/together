package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarEntry implements Comparable<CalendarEntry> {
    private String entryId;
    private String userId;
    private String title;
    private String description;
    private LocalDateTime datetime;
    private DateTimeFormatter dateTimeFormatter;
    private DateTimeFormatter hourMinutesFormater;

    public CalendarEntry(String entryId, String userId, String title, String description, String datetime) {
        this.entryId = entryId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.hourMinutesFormater = DateTimeFormatter.ofPattern("HH:mm");
        this.datetime = LocalDateTime.parse(datetime, dateTimeFormatter);
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getTime() {
        return datetime.format(hourMinutesFormater);
    }

    @Override
    public int compareTo(CalendarEntry other) {
        return this.datetime.compareTo(other.getDatetime());
    }
}
