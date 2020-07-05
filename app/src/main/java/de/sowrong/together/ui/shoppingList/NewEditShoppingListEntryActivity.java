package de.sowrong.together.ui.shoppingList;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.ShoppingList;
import de.sowrong.together.data.ShoppingListEntry;
import de.sowrong.together.data.Transactions;

public class NewEditShoppingListEntryActivity extends AppCompatActivity {
    ShoppingListEntry shoppingListEntry;
    EditText editTextItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_edit_shopping_list_entry);

        Intent intent = getIntent();
        String entryId = intent.getStringExtra(MainActivity.SHOPPING_LIST_ENTRY_ID);
        shoppingListEntry = ShoppingList.getInstance().getShoppingListEntry(entryId);

        if (shoppingListEntry == null) {
            getSupportActionBar().setTitle("Neuer Gegenstand");
            findViewById(R.id.delete).setVisibility(View.INVISIBLE);

            shoppingListEntry = new ShoppingListEntry();
            ShoppingList.getInstance().addShoppingListEntry(shoppingListEntry);
        } else {
            getSupportActionBar().setTitle("Gegenstand bearbeiten");
        }

        editTextItem = findViewById(R.id.editTextItem);

        editTextItem.setText(shoppingListEntry.getItem());

        View saveView = findViewById(R.id.save);

        saveView.setOnClickListener(view -> {
            shoppingListEntry.setItem(editTextItem.getText().toString());

            shoppingListEntry.save();
            finish();
        });

        View deleteView = findViewById(R.id.delete);

        deleteView.setOnClickListener(view -> {
            shoppingListEntry.delete();
            finish();
        });
    }
}
