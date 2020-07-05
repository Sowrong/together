package de.sowrong.together.ui.wallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.Map;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Transaction;
import de.sowrong.together.data.Transactions;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;
import de.sowrong.together.ui.calendar.DetailsCalenderEntryActivity;
import de.sowrong.together.ui.calendar.NewEditCalenderEntryActivity;
import de.sowrong.together.ui.shoppingList.NewEditShoppingListEntryActivity;

public class ListTransactionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_wallet_transations);

        getSupportActionBar().setTitle("Transaktionsverlauf");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Map<String, Transaction> transactionMap = Transactions.getInstance().getShoppingListMap();

        ViewGroup transactionsGroup = findViewById(R.id.transactionsContainer);
        transactionsGroup.removeAllViews();

        transactionMap.entrySet().stream()
                .sorted(Map.Entry.<String, Transaction>comparingByValue().reversed())
                .forEach(entry -> transactionsGroup.addView(addTransaction(entry.getValue())));
    }


    View addTransaction(Transaction transaction) {
        View transactionItem = getLayoutInflater().inflate(R.layout.transaction_item, null);
        TextView nameTextView = transactionItem.findViewById(R.id.name);
        TextView dateTextView = transactionItem.findViewById(R.id.date);

        TextView itemTextView = transactionItem.findViewById(R.id.item);
        TextView valueTextView = transactionItem.findViewById(R.id.value);

        User transactionUser = Users.getInstance().getUserById(transaction.getUserId());
        String username;

        if (transactionUser != null) {
            username = transactionUser.getName();
        } else {
            username = transaction.getUserId();
        }
        String datetime = transaction.getDatetimeString();
        nameTextView.setText(username);
        dateTextView.setText(datetime);

        itemTextView.setText(transaction.getItem());
        valueTextView.setText(transaction.getValueString() + "€");

        transactionItem.setOnLongClickListener(v -> {
            Intent intent = new Intent(this, NewEditTransactionActivity.class);
            String transactionEntryId = transaction.getTransactionEntryId();
            intent.putExtra(MainActivity.TRANSACTION_ENTRY_ID, transactionEntryId);
            startActivity(intent);

            return false;
        });

        transactionItem.setMinimumHeight(100);

        return transactionItem;
    }

}
