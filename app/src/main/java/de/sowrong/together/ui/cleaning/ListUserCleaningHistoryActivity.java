package de.sowrong.together.ui.cleaning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.CleaningWeek;
import de.sowrong.together.data.CleaningWeekUserTask;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Transaction;
import de.sowrong.together.data.Transactions;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;
import de.sowrong.together.ui.wallet.NewEditTransactionActivity;

public class ListUserCleaningHistoryActivity extends AppCompatActivity {
    /*
    private static HashMap<String, CleaningWeek> cleaningMap;
    private static HashMap<String, ArrayList<Duty>> dutiesMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_user_cleaning_history);

        cleaningMap = Cleaning.getInstance().getCleaningMap();
        dutiesMap = Cleaning.getInstance().getDutiesMap();

        ViewGroup userCleaningHistoryGroup = findViewById(R.id.userCleaningHistoryContainer);
        userCleaningHistoryGroup.removeAllViews();

        String selectedUserId = ...;
        String selectedUsername = Users.getInstance().getUserById(selectedUserId).getName();

        cleaningMap.entrySet().stream()
                .sorted(Map.Entry.<String, CleaningWeek>comparingByValue().reversed())
                .forEach(entry -> {
                    ArrayList<CleaningWeekUserTask> cleaningWeek = entry.getValue().getUserTasks();
                    cleaningWeek.forEach(userTask -> {
                        if (userTask.getUserId().equals(selectedUserId)) {
                            addUserCleaningHistoryItem(userTask);
                            //TODO List add element
                        }
                    });
                });

        getSupportActionBar().setTitle("Putzverlauf von " + selectedUsername);
    }

    View addUserCleaningHistoryItem(CleaningWeekUserTask userTask) {
        View transactionItem = getLayoutInflater().inflate(R.layout.cleaning_history_item, null);

        userTask.
        TextView nameTextView = transactionItem.findViewById(R.id.name);
        TextView dateTextView = transactionItem.findViewById(R.id.date);

        TextView itemTextView = transactionItem.findViewById(R.id.item);
        TextView valueTextView = transactionItem.findViewById(R.id.value);

        User transactionUser = Users.getInstance().getUserById(transaction.getUserId());
        String username;

        if (transactionUser != null) {
            username = transactionUser.getName();
        }
        else {
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
*/
}
