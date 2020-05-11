package de.sowrong.together.ui.cleaning;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.sowrong.together.R;

public class CleaningFragment extends Fragment {
    private static ViewGroup cleaningGroup;
    private static int backgroundColorFinished;
    private static int backgroundColorUnfinished;
    private static int textColorFinished;
    private static int textColorUnfinished;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_cleaning, container, false);

        cleaningGroup = root.findViewById(R.id.cleaningItems);
        backgroundColorFinished = getResources().getColor(R.color.colorPrimary);
        backgroundColorUnfinished = getResources().getColor(R.color.white);
        textColorFinished = getResources().getColor(R.color.white);
        textColorUnfinished = getResources().getColor(R.color.darkGrey);

        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_bathroom, "Marco", false));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_living_room, "Lisa", true));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_kitchen, "Daniel", false));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_stairway, "Simon", false));


        /*
        ** Example: read data from ViewModel **
        walletViewModel = ViewModelProviders.of(this).get(WalletViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks_wallet, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        walletViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        return root;
    }

    public static void fabClickListener() {
        final View firstCleaningItem = cleaningGroup.findViewById(R.id.cleaningItemView);
        setDone(firstCleaningItem, true);
    }

    private View createCleaningItem(LayoutInflater inflater, int icon, String name, boolean done) {
        View cleaningItem = inflater.inflate(R.layout.cleaning_item, null);
        ImageView iconView = cleaningItem.findViewById(R.id.icon);
        TextView nameView = cleaningItem.findViewById(R.id.name);

        iconView.setImageDrawable(getResources().getDrawable(icon));
        nameView.setText(name);

        setDone(cleaningItem, done);

        return cleaningItem;
    }

    private static void setDone(View view, boolean done) {
        CardView cardView = view.findViewById(R.id.card);
        ImageView iconView = view.findViewById(R.id.icon);
        TextView nameView = view.findViewById(R.id.name);

        if (done) {
            cardView.setCardBackgroundColor(backgroundColorFinished);
            nameView.setTextColor(textColorFinished);
            iconView.setImageTintList(null);
            iconView.setImageTintList(ColorStateList.valueOf(textColorFinished));
        }
        else {
            cardView.setCardBackgroundColor(backgroundColorUnfinished);
            nameView.setTextColor(textColorUnfinished);
            iconView.setImageTintList(null);
            iconView.setImageTintList(ColorStateList.valueOf(textColorUnfinished));
        }
    }
}
