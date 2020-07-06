package de.sowrong.together.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.ui.wallet.WalletViewModel;

public class CalendarFragment extends Fragment {
    private CalendarViewModel model;
    private HashMap<String, CalendarEntry> calendarMap;
    private View root;
    private LayoutInflater inflater;
    private Long millis;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tasks_calendar, container, false);
        this.inflater = inflater;

        model = ViewModelProviders.of(this).get(CalendarViewModel.class);

        CalendarView calendarView = root.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            // months are indexed 0-11
            LocalDateTime time = LocalDateTime.of(year, (month + 1), day, 0, 0, 0);
            millis = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            redrawCalendarList();
        });

        model.getCalendar().observe(this, calendarMap -> {
            this.calendarMap = calendarMap;
            millis = calendarView.getDate();
            redrawCalendarList();
        });

        return root;
    }


    private void redrawCalendarList() {
        ViewGroup calendarGroup = root.findViewById(R.id.calendarItems);
        calendarGroup.removeAllViews();

        if (calendarMap == null || calendarMap.isEmpty())
            return;

        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        calendarMap.entrySet().stream()
                .filter(element -> element.getValue().getDatetime().format(dayFormatter).equals(date.format(dayFormatter)))
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> calendarGroup.addView(createCalendarItem(inflater, entry.getValue())));
    }

    private View createCalendarItem(LayoutInflater inflater, CalendarEntry calendarEntry) {
        View calendarItem = inflater.inflate(R.layout.calendar_item, null);
        TextView timeView = calendarItem.findViewById(R.id.time);
        TextView labelView = calendarItem.findViewById(R.id.label);

        timeView.setText(calendarEntry.getTime());
        labelView.setText(calendarEntry.getTitle());

        Context context = getActivity();

        calendarItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsCalenderEntryActivity.class);
            String calendarEntryId = calendarEntry.getEntryId();
            intent.putExtra(MainActivity.CALENDAR_ENTRY_ID, calendarEntryId);
            startActivity(intent);
        });

        calendarItem.setOnLongClickListener(view -> {
            Intent intent = new Intent(context, NewEditCalenderEntryActivity.class);
            String calendarEntryId = calendarEntry.getEntryId();
            intent.putExtra(MainActivity.CALENDAR_ENTRY_ID, calendarEntryId);
            startActivity(intent);
            return false;
        });

        return calendarItem;
    }

    @Override
    public void onResume() {
        super.onResume();
        redrawCalendarList();
    }
}
