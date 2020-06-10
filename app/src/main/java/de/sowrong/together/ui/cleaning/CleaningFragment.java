package de.sowrong.together.ui.cleaning;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.sowrong.together.R;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.CleaningWeek;
import de.sowrong.together.data.CleaningWeekUserTask;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;
import de.sowrong.together.ui.calendar.CalendarViewModel;

public class CleaningFragment extends Fragment {
    private static ViewGroup cleaningGroup;
    private static int backgroundColorFinished;
    private static int backgroundColorUnfinished;
    private static int textColorFinished;
    private static int textColorUnfinished;

    private Users users;
    private CleaningViewModel model;
    private HashMap<String, Member> membersMap;
    private HashMap<String, CleaningWeek> cleaningMap;
    private HashMap<String, ArrayList<Duty>> dutiesMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // TODO make interactive
        String calendarWeek = "2020-W24";
        Users users = Users.getInstance();

        View root = inflater.inflate(R.layout.fragment_tasks_cleaning, container, false);

        cleaningGroup = root.findViewById(R.id.cleaningItems);
        backgroundColorFinished = getResources().getColor(R.color.colorPrimary);
        backgroundColorUnfinished = getResources().getColor(R.color.white);
        textColorFinished = getResources().getColor(R.color.white);
        textColorUnfinished = getResources().getColor(R.color.darkGrey);

        Context context = getActivity().getApplicationContext();

        model = ViewModelProviders.of(this).get(CleaningViewModel.class);

        model.getMembers().observe(this, membersMap -> {
            this.membersMap = membersMap;
            redrawCleaningList(root, inflater, context, calendarWeek);
        });

        model.getCleaning().observe(this, cleaningMap -> {
            this.cleaningMap = cleaningMap;
            redrawCleaningList(root, inflater, context, calendarWeek);
        });

        model.getDuties().observe(this, dutiesMap -> {
            this.dutiesMap = dutiesMap;
            redrawCleaningList(root, inflater, context, calendarWeek);
        });

        /*

        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_bathroom, "Marco", false));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_living_room, "Lisa", true));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_kitchen, "Daniel", false));
        cleaningGroup.addView(createCleaningItem(inflater, R.drawable.ic_task_stairway, "Simon", false));

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


    private void redrawCleaningList(View root, @NonNull LayoutInflater inflater, Context context, String calendarWeek) {
        if (cleaningMap == null || cleaningMap.isEmpty())
            return;

        cleaningGroup.removeAllViews();

        LocalDate selectedWeekDate = CleaningWeek.parseStringAsCalendarWeek(calendarWeek);

        cleaningMap.entrySet().stream()
                //.filter(element -> element.getValue().getDate() == selectedWeekDate)
                .forEach(selectedWeekData -> {
                    ArrayList<CleaningWeekUserTask> userTasks = selectedWeekData.getValue().getUserTasks();
                    for (CleaningWeekUserTask userTask: userTasks) {
                        if (dutiesMap.size() > 0 && dutiesMap.containsKey(userTask.getDutyId())) {
                            // TODO use duty names
                            ArrayList<Duty> duties = dutiesMap.get(userTask.getDutyId());
                            if (duties.size() > 0) {
                                for (Duty duty: duties) {
                                    String dutyName = duty.getTitle();
                                    String dutyIcon = duty.getIcon();
                                    Resources resources = context.getResources();
                                    final int resourceId = resources.getIdentifier(dutyIcon, "drawable", context.getPackageName());
                                    boolean finished = userTask.isFinished();

                                    String username = Members.getNameById(membersMap, userTask.getUserId());

                                    if (username != null) {
                                        cleaningGroup.addView(createCleaningItem(inflater, resourceId, username, finished));
                                    }
                                }
                            }
                        }
                    }
                });
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
