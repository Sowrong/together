package de.sowrong.together.ui.wallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.Calendar;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.ShoppingList;
import de.sowrong.together.data.ShoppingListEntry;
import de.sowrong.together.data.Transaction;
import de.sowrong.together.data.Transactions;

public class NewEditTransactionActivity extends AppCompatActivity {
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    Transaction transaction;

    EditText editTextItem;
    EditText editTextValue;
    EditText editTextDate;
    EditText editTextTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_edit_transaction);

        Intent intent = getIntent();
        String entryId = intent.getStringExtra(MainActivity.TRANSACTION_ENTRY_ID);
        String shoppingItemId = intent.getStringExtra(MainActivity.SHOPPING_LIST_ITEM_ID);

        editTextItem = findViewById(R.id.editTextItem);
        editTextValue = findViewById(R.id.editTextValue);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);

        transaction = Transactions.getInstance().getTransaction(entryId);

        if (transaction == null) {
            getSupportActionBar().setTitle("Neue Transaktion");
            findViewById(R.id.delete).setVisibility(View.INVISIBLE);

            ShoppingListEntry shoppingListEntry = ShoppingList.getInstance().getShoppingListEntry(shoppingItemId);

            if (shoppingListEntry != null) {
                transaction = new Transaction(shoppingListEntry.getItem());
            } else {
                transaction = new Transaction("");
            }

            Transactions.getInstance().addTransaction(transaction);
        } else {
            getSupportActionBar().setTitle("Transaktion bearbeiten");
        }


        editTextDate.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus == false) {
                datePicker.hide();
                return;
            }
            datePicker = new DatePickerDialog(NewEditTransactionActivity.this,
                    (viewDatePicker, year, monthOfYear, dayOfMonth) -> {
                        editTextDate.setText(String.format("%4d-%02d-%02d", year, monthOfYear, dayOfMonth));

                        LocalDateTime dateTime = transaction.getDatetime();

                        dateTime = dateTime.withYear(year);
                        dateTime = dateTime.withMonth(monthOfYear);
                        dateTime = dateTime.withDayOfMonth(dayOfMonth);

                        transaction.setDatetime(dateTime);
                    }, transaction.getDatetime().getYear(), transaction.getDatetime().getMonthValue(), transaction.getDatetime().getDayOfMonth());
            datePicker.show();
        });


        editTextTime.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus == false) {
                timePicker.hide();
                return;
            }
            timePicker = new TimePickerDialog(NewEditTransactionActivity.this,
                    (viewTimePicker, hourOfDay, minute) -> {
                        editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                        LocalDateTime dateTime = transaction.getDatetime();

                        dateTime = dateTime.withHour(hourOfDay);
                        dateTime = dateTime.withMinute(minute);

                        transaction.setDatetime(dateTime);
                    }, transaction.getDatetime().getHour(), transaction.getDatetime().getMinute(), true);
            timePicker.show();
        });

        editTextItem.setText(transaction.getItem());
        editTextDate.setText(transaction.getDate());
        editTextTime.setText(transaction.getTime());

        if (transaction.getValue() > 0) {
            editTextValue.setText(transaction.getValueString());
        }

        View saveView = findViewById(R.id.save);

        saveView.setOnClickListener(view -> {
            transaction.setItem(editTextItem.getText().toString());
            transaction.setValue(Double.parseDouble(editTextValue.getText().toString()));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime datetime = LocalDateTime.parse(editTextDate.getText().toString() + " " + editTextTime.getText().toString(), dateTimeFormatter);
            transaction.setDatetime(datetime);

            transaction.save();

            ShoppingListEntry shoppingListEntry = ShoppingList.getInstance().getShoppingListEntry(shoppingItemId);
            if (shoppingListEntry != null) {
                shoppingListEntry.delete();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.GOTO_TAB, String.valueOf(MainActivity.TAB_WALLET));
            setResult(Activity.RESULT_OK, resultIntent);

            finish();
        });

        View deleteView = findViewById(R.id.delete);

        deleteView.setOnClickListener(view -> {
            transaction.delete();
            finish();
        });
    }
}
