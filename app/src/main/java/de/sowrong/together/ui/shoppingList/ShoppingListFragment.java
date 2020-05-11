package de.sowrong.together.ui.shoppingList;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;

import de.sowrong.together.R;

public class ShoppingListFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ArrayList<String> stringArrayList = new ArrayList<>();
    LinearLayout coordinatorLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_shopping_list, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        coordinatorLayout = root.findViewById(R.id.coordinatorLayout);

        populateRecyclerView();
        enableSwipeToDeleteAndUndo();

        return root;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final String item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void populateRecyclerView() {
        stringArrayList.add("Zucker");
        stringArrayList.add("Öl");
        stringArrayList.add("Schwämme");
        stringArrayList.add("Spülmittel");
        stringArrayList.add("Toilettenpaier");
        stringArrayList.add("Klobürste");

        mAdapter = new RecyclerViewAdapter(stringArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}