package de.sowrong.together.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Users;

public class DetailsCalenderEntryActivity extends AppCompatActivity {
    CalendarEntry calendarEntry;
    final int maxDetailsHeight = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_calendar_entry);

        Intent intent = getIntent();
        String entryId = intent.getStringExtra(MainActivity.CALENDAR_ENTRY_ID);

        TextView textViewCreator = findViewById(R.id.textViewCreator);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDetails = findViewById(R.id.textViewDetails);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewTime = findViewById(R.id.textViewTime);

        textViewDetails.setMaxLines(maxDetailsHeight);

        calendarEntry = de.sowrong.together.data.Calendar.getInstance().getCalendarEntry(entryId);

        if (calendarEntry == null) {
            finish();
        } else {
            getSupportActionBar().setTitle("Termin Details");
        }

        String userName = Users.getInstance().getUserById(calendarEntry.getUserId()).getName();
        textViewCreator.setText(userName);
        textViewTitle.setText(calendarEntry.getTitle());
        textViewDetails.setText(calendarEntry.getDetails());
        textViewDate.setText(calendarEntry.getTime());
        textViewTime.setText(calendarEntry.getDate());
    }
}
