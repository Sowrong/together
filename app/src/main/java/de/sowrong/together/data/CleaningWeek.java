package de.sowrong.together.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.IsoFields;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class CleaningWeek implements Comparable<CleaningWeek> {
    private LocalDate date;
    private DateTimeFormatter dateFormater;
    private ArrayList<CleaningWeekUserTask> userTasks;

    public CleaningWeek(String week) {
        dateFormater = new DateTimeFormatterBuilder()
                .appendValue(IsoFields.WEEK_BASED_YEAR, 4)
                .appendLiteral('-')
                .appendLiteral('W')
                .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
                .appendLiteral(' ')
                .appendValue(ChronoField.DAY_OF_WEEK)
                .toFormatter();

        //dateFormater = DateTimeFormatter.ofPattern("yyyy-'W'-ww E");
        // always start at first day of week, this is not encoded in database
        date = LocalDate.parse(week+" 1", dateFormater);

        userTasks = new ArrayList<>();
    }

    public void addUserTask(String entryId, String userId, String dutyId, boolean finished) {
        userTasks.add(new CleaningWeekUserTask(entryId, userId, dutyId, finished));
    }

    public LocalDate getDate() {
        return date;
    }

    public ArrayList<CleaningWeekUserTask> getUserTasks() {
        return userTasks;
    }

    @Override
    public int compareTo(CleaningWeek other) {
        return this.date.compareTo(other.getDate());
    }
}
