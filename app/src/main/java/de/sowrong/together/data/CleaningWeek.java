package de.sowrong.together.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CleaningWeek implements Comparable<CleaningWeek> {
    private LocalDateTime date;
    private HashMap<String, CleaningWeekUserTask> userTasks;

    public CleaningWeek(String week) {
        date = parseStringAsCalendarWeek(week).truncatedTo(ChronoUnit.DAYS);
        userTasks = new HashMap<>();
    }

    public boolean initUserTasks(int weekNumber) {
        HashMap<String, Member> memberMap = Members.getInstance().getMemberMap();

        if (memberMap.size() == 0) {
            return false;
        }

        Collection<Member> values = memberMap.values();
        ArrayList<Member> members = new ArrayList<>(values);

        AtomicInteger memberIndex = new AtomicInteger(0);

        Cleaning.getInstance().getDutiesMap().entrySet().forEach(
                entry -> {
                    String dutyId = entry.getKey();
                    String userId = members.get((memberIndex.getAndIncrement() + weekNumber) % members.size()).getId();

                    CleaningWeekUserTask cleaningWeekUserTask = new CleaningWeekUserTask();
                    cleaningWeekUserTask.setDutyId(dutyId);
                    cleaningWeekUserTask.setUserId(userId);
                    cleaningWeekUserTask.setFinished(false);

                    userTasks.put(Group.randomId(8), cleaningWeekUserTask);
                }
        );

        return true;
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
