package de.sowrong.together.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarEntry implements Comparable<CalendarEntry> {
    private String entryId;
    private String userId;
    private String title;
    private String details;
    private LocalDateTime datetime;

    private DateTimeFormatter dateTimeFormatter;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter hourMinutesFormater;

    public CalendarEntry() {
        this.entryId = Group.randomId();
        this.userId = Users.getInstance().getOwnId();
        this.title = "";
        this.details = "";
        this.datetime = LocalDateTime.now();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.hourMinutesFormater = DateTimeFormatter.ofPattern("HH:mm");
    }

    public CalendarEntry(String entryId, String userId, String title, String details, String datetime) {
        this.entryId = entryId;
        this.userId = userId;
        this.title = title;
        this.details = details;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.hourMinutesFormater = DateTimeFormatter.ofPattern("HH:mm");
        this.datetime = LocalDateTime.parse(datetime, dateTimeFormatter);
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public String getDate() {
        return datetime.format(dateFormatter);
    }

    public String getTime() {
        return datetime.format(hourMinutesFormater);
    }

    public void save() {
        Calendar.getInstance().syncCalendar();
    }

    public void delete() {
        Calendar.getInstance().deleteCalendarEntry(this.getEntryId());
        save();
    }

    @Override
    public int compareTo(CalendarEntry other) {
        return this.datetime.compareTo(other.getDatetime());
    }
}
