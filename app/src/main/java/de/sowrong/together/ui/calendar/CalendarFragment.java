package de.sowrong.together.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.sowrong.together.R;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private ViewGroup calendarGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks_calendar, container, false);


        calendarGroup = root.findViewById(R.id.calendarItems);

        calendarGroup.addView(createCalendarItem(inflater, "09:00", "Frühstück"));
        calendarGroup.addView(createCalendarItem(inflater, "11:00", "Sport"));
        calendarGroup.addView(createCalendarItem(inflater, "12:00", "Mittagessen"));
        calendarGroup.addView(createCalendarItem(inflater, "17:00", "Einkaufen"));
        calendarGroup.addView(createCalendarItem(inflater, "18:00", "Abendessen"));
        calendarGroup.addView(createCalendarItem(inflater, "20:00", "Spieleabend"));
        calendarGroup.addView(createCalendarItem(inflater, "24:00", "WG Party"));

        return root;
    }

    private View createCalendarItem(LayoutInflater inflater, String time, String label) {
        View calendarItem = inflater.inflate(R.layout.calendar_item, null);
        TextView timeView = calendarItem.findViewById(R.id.time);
        TextView labelView = calendarItem.findViewById(R.id.label);

        timeView.setText(time);
        labelView.setText(label);

        return calendarItem;
    }
}
