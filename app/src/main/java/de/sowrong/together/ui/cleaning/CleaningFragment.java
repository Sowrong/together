package de.sowrong.together.ui.cleaning;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.CleaningWeek;
import de.sowrong.together.data.CleaningWeekUserTask;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;
import de.sowrong.together.data.Transaction;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;
import de.sowrong.together.ui.calendar.CalendarViewModel;

public class CleaningFragment extends Fragment {
    private static ViewGroup cleaningGroup;
    private static int backgroundColorFinished;
    private static int backgroundColorUnfinished;
    private static int textColorFinished;
    private static int textColorUnfinished;
    public static CleaningFragment instance;

    private Users users;
    private CleaningViewModel model;
    private HashMap<String, Member> membersMap;
    private HashMap<String, CleaningWeek> cleaningMap;
    private HashMap<String, Duty> dutiesMap;
    private LocalDateTime displayDateTime;
    private View root;
    private Context context;
    private LayoutInflater inflater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tasks_cleaning, container, false);
        this.inflater = inflater;
        instance = this;
        context = getActivity().getApplicationContext();

        setDisplayTimeToNow();

        cleaningGroup = root.findViewById(R.id.cleaningItems);
        backgroundColorFinished = getResources().getColor(R.color.colorPrimary);
        backgroundColorUnfinished = getResources().getColor(R.color.white);
        textColorFinished = getResources().getColor(R.color.white);
        textColorUnfinished = getResources().getColor(R.color.darkGrey);


        model = ViewModelProviders.of(this).get(CleaningViewModel.class);

        model.getMembers().observe(this, membersMap -> {
            this.membersMap = membersMap;
            redrawCleaningList();
        });

        model.getCleaning().observe(this, cleaningMap -> {
            this.cleaningMap = cleaningMap;
            redrawCleaningList();
        });

        model.getDuties().observe(this, dutiesMap -> {
            this.dutiesMap = dutiesMap;
            redrawCleaningList();
        });

        root.findViewById(R.id.leftArrowImageView).setOnClickListener(view -> {
            displayDateTime = displayDateTime.minusWeeks(1);
            redrawCleaningList();
        });

        root.findViewById(R.id.rightArrowImageView).setOnClickListener(view -> {
            displayDateTime = displayDateTime.plusWeeks(1);
            redrawCleaningList();
        });

        root.findViewById(R.id.calendarWeekTextView).setOnClickListener(view -> {
            setDisplayTimeToNow();
            redrawCleaningList();
        });

        return root;
    }

    public static CleaningFragment getInstance() {
        return instance;
    }

    private void setDisplayTimeToNow() {
        displayDateTime = LocalDateTime.now();
    }

    private void redrawCleaningList() {
        TextView calendarWeekTextView = root.findViewById(R.id.calendarWeekTextView);

        String selectedWeekDateString = CleaningWeek.getWeekStringFromLocalDate(displayDateTime);

        ImageView nextWeekArrow = root.findViewById(R.id.rightArrowImageView);

        if (selectedWeekDateString.equals(CleaningWeek.getCurrentWeekString())) {
            nextWeekArrow.setEnabled(false);
            nextWeekArrow.setVisibility(View.INVISIBLE);
            calendarWeekTextView.setText("jetzt");
        } else {
            nextWeekArrow.setEnabled(true);
            nextWeekArrow.setVisibility(View.VISIBLE);
            calendarWeekTextView.setText(selectedWeekDateString);
        }

        cleaningGroup.removeAllViews();


        if (cleaningMap == null)
            return;

        String displayWeekString = CleaningWeek.getWeekStringFromLocalDate(displayDateTime);

        if (cleaningMap.isEmpty() || !cleaningMap.containsKey(displayWeekString)) {
            CleaningWeek cleaningWeek = new CleaningWeek(displayWeekString);

            DateTimeFormatter weekFormater = new DateTimeFormatterBuilder()
                    .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR).toFormatter();

            int week = Integer.parseInt(displayDateTime.format(weekFormater));

            if (cleaningWeek.initUserTasks(week)) {
                cleaningMap.put(displayWeekString, cleaningWeek);
                cleaningWeek.save();
            }
        }

        cleaningMap.entrySet().stream()
                .filter(element -> {
                    String listElementWeekString = CleaningWeek.getWeekStringFromLocalDate(element.getValue().getDate());
                    return displayWeekString.equals(listElementWeekString);
                })
                .forEach(selectedWeekData -> {
                    HashMap<String, CleaningWeekUserTask> userTasks = selectedWeekData.getValue().getUserTasks();

                    userTasks.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(
                            userTaskEntry -> {
                                CleaningWeekUserTask userTask = userTaskEntry.getValue();
                                if (dutiesMap != null && dutiesMap.size() > 0 && dutiesMap.containsKey(userTask.getDutyId())) {
                                    Duty duty = dutiesMap.get(userTask.getDutyId());
                                    if (duty != null) {
                                        boolean finished = userTask.isFinished();
                                        cleaningGroup.addView(createCleaningItem(inflater, userTask, duty, finished));
                                    }
                                }
                            }
                    );
                });
    }

    public void fabClickListener() {
        // goto current week
        setDisplayTimeToNow();

        // mark all own events as done
        cleaningMap.entrySet().stream()
                .filter(element -> {
                    String displayWeekString = CleaningWeek.getWeekStringFromLocalDate(displayDateTime);
                    String listElementWeekString = CleaningWeek.getWeekStringFromLocalDate(element.getValue().getDate());
                    return displayWeekString.equals(listElementWeekString);
                })
                .forEach(selectedWeekData -> {
                    HashMap<String, CleaningWeekUserTask> userTasks = selectedWeekData.getValue().getUserTasks();
                    userTasks.entrySet().stream().forEach(
                            userTaskEntry -> {
                                if (userTaskEntry.getValue().getUserId().equals(Users.getOwnId())) {
                                    boolean oldFinishedValue = userTaskEntry.getValue().isFinished();
                                    userTaskEntry.getValue().setFinished(!oldFinishedValue);
                                }
                            });
                });

        redrawCleaningList();
        Cleaning.getInstance().syncCleaning();
    }

    private View createCleaningItem(LayoutInflater inflater, CleaningWeekUserTask userTask, Duty duty, boolean done) {
        View cleaningItem = inflater.inflate(R.layout.cleaning_item, null);
        ImageView iconView = cleaningItem.findViewById(R.id.icon);
        TextView nameView = cleaningItem.findViewById(R.id.name);
        TextView dutyNameView = cleaningItem.findViewById(R.id.dutyName);

        String dutyName = duty.getTitle();
        String dutyIcon = duty.getIcon();
        Resources resources = context.getResources();
        final int icon = resources.getIdentifier(dutyIcon, "drawable", context.getPackageName());

        String userId = userTask.getUserId();
        String username = Members.getInstance().getNameById(userId);

        if (username == null) {
            username = "Benutzer nicht in Gruppe";
        }

        iconView.setImageDrawable(getResources().getDrawable(icon));
        nameView.setText(username);
        dutyNameView.setText(dutyName);

        setDone(cleaningItem, done);

        /*
        // it would have been nice to be able to click on each task individually,
        // though this would changing the structure of the CleaningWeekUserTask

        if(userId.equals(Users.getOwnId())) {
            cleaningItem.setOnClickListener(view -> {
                redrawCleaningList();
            });
        }
        */

        return cleaningItem;
    }

    private static void setDone(View view, boolean done) {
        CardView cardView = view.findViewById(R.id.card);
        ImageView iconView = view.findViewById(R.id.icon);
        TextView nameView = view.findViewById(R.id.name);
        TextView dutyNameView = view.findViewById(R.id.dutyName);

        if (done) {
            cardView.setCardBackgroundColor(backgroundColorFinished);
            nameView.setTextColor(textColorFinished);
            dutyNameView.setTextColor(textColorFinished);
            iconView.setImageTintList(null);
            iconView.setImageTintList(ColorStateList.valueOf(textColorFinished));
        }
        else {
            cardView.setCardBackgroundColor(backgroundColorUnfinished);
            nameView.setTextColor(textColorUnfinished);
            dutyNameView.setTextColor(textColorUnfinished);
            iconView.setImageTintList(null);
            iconView.setImageTintList(ColorStateList.valueOf(textColorUnfinished));
        }
    }
}
