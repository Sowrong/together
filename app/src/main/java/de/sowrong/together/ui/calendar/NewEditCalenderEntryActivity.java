package de.sowrong.together.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.Calendar;
import de.sowrong.together.data.CalendarEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewEditCalenderEntryActivity extends AppCompatActivity {
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    CalendarEntry calendarEntry;
    final int maxDetailsHeight = 2;

    EditText editTextTitle;
    EditText editTextDetails;
    EditText editTextDate;
    EditText editTextTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_edit_calendar_entry);

        Intent intent = getIntent();
        String entryId = intent.getStringExtra(MainActivity.CALENDAR_ENTRY_ID);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDetails = findViewById(R.id.editTextDetails);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);

        editTextDetails.setMaxLines(maxDetailsHeight);
        calendarEntry = de.sowrong.together.data.Calendar.getInstance().getCalendarEntry(entryId);

        if (calendarEntry == null) {
            getSupportActionBar().setTitle("Neuer Termin");
            findViewById(R.id.delete).setVisibility(View.INVISIBLE);

            calendarEntry = new CalendarEntry();
            Calendar.getInstance().addCalendarEntry(calendarEntry);
        } else {
            getSupportActionBar().setTitle("Termin bearbeiten");
        }


        editTextDate.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus == false) {
                datePicker.hide();
                return;
            }
            datePicker = new DatePickerDialog(NewEditCalenderEntryActivity.this,
                    (viewDatePicker, year, monthOfYear, dayOfMonth) -> {
                        editTextDate.setText(String.format("%4d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));

                        LocalDateTime dateTime = calendarEntry.getDatetime();

                        dateTime = dateTime.withYear(year);
                        dateTime = dateTime.withMonth(monthOfYear);
                        dateTime = dateTime.withDayOfMonth(dayOfMonth);

                        calendarEntry.setDatetime(dateTime);
                    }, calendarEntry.getDatetime().getYear(), calendarEntry.getDatetime().getMonthValue() - 1, calendarEntry.getDatetime().getDayOfMonth());
            datePicker.show();
        });


        editTextTime.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus == false) {
                timePicker.hide();
                return;
            }
            timePicker = new TimePickerDialog(NewEditCalenderEntryActivity.this,
                    (viewTimePicker, hourOfDay, minute) -> {
                        editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                        LocalDateTime dateTime = calendarEntry.getDatetime();

                        dateTime = dateTime.withHour(hourOfDay);
                        dateTime = dateTime.withMinute(minute);

                        calendarEntry.setDatetime(dateTime);
                    }, calendarEntry.getDatetime().getHour(), calendarEntry.getDatetime().getMinute(), true);
            timePicker.show();
        });

        editTextTitle.setText(calendarEntry.getTitle());
        editTextDetails.setText(calendarEntry.getDetails());
        editTextTime.setText(calendarEntry.getTime());
        editTextDate.setText(calendarEntry.getDate());

        View saveView = findViewById(R.id.save);

        saveView.setOnClickListener(view -> {
            calendarEntry.setTitle(editTextTitle.getText().toString());
            calendarEntry.setDetails(editTextDetails.getText().toString());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime datetime = LocalDateTime.parse(editTextDate.getText().toString() + " " + editTextTime.getText().toString(), dateTimeFormatter);
            calendarEntry.setDatetime(datetime);

            calendarEntry.save();
            finish();
        });

        View deleteView = findViewById(R.id.delete);

        deleteView.setOnClickListener(view -> {
            calendarEntry.delete();
            finish();
        });
    }
}
