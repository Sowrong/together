package de.sowrong.together.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.sowrong.together.R;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.CleaningWeek;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;
import de.sowrong.together.data.Users;

public class SettingsActivity extends AppCompatActivity {
    final String prefix = "ic_task_";
    Map<AdapterView, ImageView> iconMap;
    Map<String, Drawable> taskResources;
    ArrayList<String> taskNames;
    ArrayList<Pair<Duty, View>> dutiesViews;
    int numberDuties;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Member self = Members.getInstance().getMemberMap().get(Users.getOwnId());
        String role = self.getRole();

        EditText usernameTextView = findViewById(R.id.editTextUsername);
        usernameTextView.setText(self.getName());

        isAdmin = role.equals("admin");

        if (!isAdmin) {
            ViewGroup settingsRoot = (ViewGroup) findViewById(R.id.settingsRoot);
            settingsRoot.removeView(findViewById(R.id.cardViewTasks));

            findViewById(R.id.save).setOnClickListener(v -> {
                Users.getInstance().updateOwnName(usernameTextView.getText().toString());
                finish();
            });
        } else {
            dutiesViews = new ArrayList<>();
            iconMap = new HashMap<>();

            HashMap<String, Duty> dutiesMap = Cleaning.getInstance().getDutiesMap();
            numberDuties = dutiesMap.size();

            Field[] drawables = de.sowrong.together.R.drawable.class.getFields();

            HashMap<String, Drawable> allResources = new HashMap<>();

            for (Field field : drawables) {
                try {
                    Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                    allResources.put(field.getName(), getResources().getDrawable(field.getInt(null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            taskResources = allResources.entrySet()
                    .stream()
                    .filter(map -> map.getKey().contains(prefix))
                    .collect(Collectors.toMap(map -> map.getKey().substring(prefix.length()), map -> map.getValue()));

            taskNames = new ArrayList<>(taskResources.keySet());

            findViewById(R.id.leftArrowImageView).setOnClickListener(v -> {
                if (numberDuties > 0) {
                    numberDuties--;

                    Pair<Duty, View> elementToRemove = dutiesViews.get(dutiesViews.size() - 1);
                    iconMap.remove(elementToRemove.second.findViewById(R.id.spinner));

                    dutiesViews.remove(elementToRemove);

                    redrawDutiesView();
                }
            });

            findViewById(R.id.rightArrowImageView).setOnClickListener(v -> {
                numberDuties++;
                dutiesViews.add(new Pair(null, createSpinner(null)));

                redrawDutiesView();
            });

            findViewById(R.id.save).setOnClickListener(v -> {
                HashMap<String, Duty> newDutiesMap = new HashMap<>();

                dutiesViews.forEach(dutyViewPair -> {
                    Duty duty = dutyViewPair.first;
                    View dutyView = dutyViewPair.second;

                    if (duty == null) {
                        duty = new Duty();
                        duty.setId(Group.randomId(8));
                    }

                    Spinner spinner = dutyView.findViewById(R.id.spinner);
                    TextView titleTextView = dutyView.findViewById(R.id.editTextName);

                    duty.setIcon(prefix + taskNames.get(spinner.getSelectedItemPosition()));
                    duty.setTitle(titleTextView.getText().toString());

                    newDutiesMap.put(duty.getId(), duty);
                });

                Cleaning cleaningInstance = Cleaning.getInstance();

                cleaningInstance.setCleaningMap(newDutiesMap);
                cleaningInstance.deleteCurrentWeek();

                cleaningInstance.syncCleaning();

                Users.getInstance().updateOwnName(usernameTextView.getText().toString());
                finish();
            });


            dutiesMap.entrySet().forEach(entry -> {
                Duty duty = entry.getValue();
                dutiesViews.add(new Pair(duty, createSpinner(duty)));
            });

            redrawDutiesView();
        }
    }

    void redrawDutiesView() {
        ViewGroup dutiesList = findViewById(R.id.dutyList);
        dutiesList.removeAllViews();
        dutiesViews.forEach(pair -> dutiesList.addView(pair.second));
        TextView numberDutiesTextView = findViewById(R.id.numberDuties);
        numberDutiesTextView.setText(String.valueOf(numberDuties));
    }

    View createSpinner(Duty duty) {
        View spinnerView = LayoutInflater.from(this).inflate(R.layout.spinner_item, null);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, taskNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = spinnerView.findViewById(R.id.spinner);

        spinner.setAdapter(adapter);

        iconMap.put(spinner, spinnerView.findViewById(R.id.imageView));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Drawable newIcon = taskResources.get(taskNames.get(position));
                ImageView imageView = iconMap.get(parent);
                imageView.setImageDrawable(newIcon);
                imageView.setImageTintList(null);
                imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (duty != null) {
            String iconName = duty.getIcon().substring(prefix.length());

            spinner.setSelection(taskNames.indexOf(iconName));

            TextView nameTextView = spinnerView.findViewById(R.id.editTextName);
            nameTextView.setText(duty.getTitle());
        }

        return spinnerView;
    }
}
