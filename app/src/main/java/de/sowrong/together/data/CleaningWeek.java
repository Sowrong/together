package de.sowrong.together.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CleaningWeek implements Comparable<CleaningWeek> {
    private LocalDateTime date;
    private HashMap<String, CleaningWeekUserTask> userTasks;

    public CleaningWeek(String week) {
        date = parseStringAsCalendarWeek(week).truncatedTo(ChronoUnit.DAYS);
        userTasks = new HashMap<>();
    }

    public static LocalDateTime parseStringAsCalendarWeek(String input) {
        DateTimeFormatter weekFormater = new DateTimeFormatterBuilder()
                .appendValue(IsoFields.WEEK_BASED_YEAR, 4)
                .appendLiteral('-')
                .appendLiteral('W')
                .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
                .appendLiteral(' ')
                .appendValue(ChronoField.DAY_OF_WEEK)
                .appendLiteral(' ')
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR)
                .toFormatter();

        //dateFormater = DateTimeFormatter.ofPattern("yyyy-'W'-ww E");
        // always start at first day of week, this is not encoded in database
        return LocalDateTime.parse(input + " 1 0:00", weekFormater);
    }

    public static String getWeekStringFromLocalDate(LocalDateTime localDate) {
        DateTimeFormatter weekFormater = new DateTimeFormatterBuilder()
                .appendValue(IsoFields.WEEK_BASED_YEAR, 4)
                .appendLiteral('-')
                .appendLiteral('W')
                .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
                .toFormatter();

        return localDate.format(weekFormater);
    }

    public static String getCurrentWeekString() {
        return getWeekStringFromLocalDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
    }

    public void addUserTask(String userTaskId, CleaningWeekUserTask cleaningWeekUserTask) {
        userTasks.put(userTaskId, cleaningWeekUserTask);
    }

    public LocalDateTime getDate() {
        return date.truncatedTo(ChronoUnit.DAYS);
    }

    public HashMap<String, CleaningWeekUserTask> getUserTasks() {
        return userTasks;
    }

    public void save() {
        Cleaning.getInstance().syncCleaning();
    }

    @Override
    public int compareTo(CleaningWeek other) {
        return this.date.truncatedTo(ChronoUnit.DAYS).compareTo(other.getDate().truncatedTo(ChronoUnit.DAYS));
    }
}
