package de.sowrong.together.ui.calendar;

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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.ui.wallet.WalletViewModel;

public class CalendarFragment extends Fragment {
    private CalendarViewModel model;
    private ViewGroup calendarGroup;
    private HashMap<String, CalendarEntry> calendarMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_calendar, container, false);

        model = ViewModelProviders.of(this).get(CalendarViewModel.class);

        CalendarView calendarView = root.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            // months are indexed 0-11
            LocalDateTime time = LocalDateTime.of(year, (month+1), day, 0, 0, 0);
            long millis = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            redrawCalendarList(root, inflater, millis);
        });

        model.getCalendar().observe(this, calendarMap -> {
            this.calendarMap = calendarMap;
            redrawCalendarList(root, inflater, calendarView.getDate());
        });

        return root;
    }


    private void redrawCalendarList(View root, @NonNull LayoutInflater inflater, Long millis) {
        if (calendarMap.isEmpty())
            return;

        calendarGroup = root.findViewById(R.id.calendarItems);
        calendarGroup.removeAllViews();

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

        return calendarItem;
    }
}
