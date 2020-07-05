package de.sowrong.together.ui.shoppingList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.sowrong.together.MainActivity;
import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.ShoppingList;
import de.sowrong.together.data.ShoppingListEntry;
import de.sowrong.together.ui.calendar.NewEditCalenderEntryActivity;
import de.sowrong.together.ui.wallet.NewEditTransactionActivity;
import de.sowrong.together.ui.wallet.WalletViewModel;

public class ShoppingListFragment extends Fragment {
    private ShoppingListViewModel model;
    private HashMap<String, ShoppingListEntry> shoppingListMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_shopping_list, container, false);

        model = ViewModelProviders.of(requireActivity()).get(ShoppingListViewModel.class);

        model.getShoppingList().observe(this, shoppingListMap -> {
            this.shoppingListMap = shoppingListMap;
            redrawShoppingItem(root, inflater);
        });

        return root;
    }

    private void redrawShoppingItem(View root, @NonNull LayoutInflater inflater) {
        if (shoppingListMap.isEmpty())
            return;

        ViewGroup shoppingListGroup = root.findViewById(R.id.shoppingList);
        shoppingListGroup.removeAllViews();

        shoppingListMap.entrySet().stream()
                .forEach(entry -> shoppingListGroup.addView(createShoppingItem(inflater, entry.getValue())));
    }

    private View createShoppingItem(LayoutInflater inflater, ShoppingListEntry shoppingListEntry) {
        View shoppingListItem = inflater.inflate(R.layout.shopping_list_item, null);
        TextView itemView = shoppingListItem.findViewById(R.id.item);

        itemView.setText(shoppingListEntry.getItem());

        Context context = getActivity();

        shoppingListItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, NewEditTransactionActivity.class);
            intent.putExtra(MainActivity.TRANSACTION_ENTRY_ID, "");
            intent.putExtra(MainActivity.SHOPPING_LIST_ITEM_ID, shoppingListEntry.getEntryId());
            startActivityForResult(intent, MainActivity.TAB_REQUEST_CODE);
        });

        shoppingListItem.setOnLongClickListener(view -> {
            Intent intent = new Intent(context, NewEditShoppingListEntryActivity.class);
            String shoppingListEntryId = shoppingListEntry.getEntryId();
            intent.putExtra(MainActivity.SHOPPING_LIST_ENTRY_ID, shoppingListEntryId);
            startActivity(intent);
            return false;
        });

        return shoppingListItem;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}