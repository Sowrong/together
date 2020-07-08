package de.sowrong.together.ui;

import android.content.res.ColorStateList;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.sowrong.together.R;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;

public class SettingsActivity extends AppCompatActivity {
    final String avatarPrefix = "head_";
    Map<String, Drawable> avatarResources;
    ArrayList<String> avatarNames;

    final String cleaningTaskPrefix = "ic_task_";
    Map<AdapterView, ImageView> cleaningTaskIconMap;
    Map<String, Drawable> cleaningTaskResources;
    ArrayList<String> cleaningTaskNames;

    ArrayList<Pair<Duty, View>> dutiesViews;
    int numberDuties;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        User ownUser = Users.getInstance().getUserById(Users.getOwnId());
        Member ownMember = Members.getInstance().getMemberMap().get(Users.getOwnId());
        String role = ownMember.getRole();


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

        ImageView avatarImageView = findViewById(R.id.imageViewAvatarSettings);
        Spinner avatarSpinner = findViewById(R.id.spinnerAvatarSettings);

        avatarResources = new HashMap<>();
        avatarNames = new ArrayList<>();

        avatarResources = allResources.entrySet()
                .stream()
                .filter(map -> map.getKey().contains(avatarPrefix))
                .collect(Collectors.toMap(map -> map.getKey().substring(avatarPrefix.length()), map -> map.getValue()));
        avatarNames = new ArrayList<>(avatarResources.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, avatarNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        avatarSpinner.setAdapter(adapter);

        avatarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Drawable newIcon = avatarResources.get(avatarNames.get(position));
                avatarImageView.setImageDrawable(newIcon);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        avatarSpinner.setSelection(avatarNames.indexOf(ownUser.getAvatar().substring(avatarPrefix.length())));

        EditText usernameTextView = findViewById(R.id.editTextUsername);
        usernameTextView.setText(ownMember.getName());

        isAdmin = role.equals("admin");

        if (!isAdmin) {
            ViewGroup settingsRoot = (ViewGroup) findViewById(R.id.settingsRoot);
            settingsRoot.removeView(findViewById(R.id.cardViewTasks));

            findViewById(R.id.save).setOnClickListener(v -> {
                Users.getInstance().update(usernameTextView.getText().toString(),
                        avatarPrefix + avatarNames.get(avatarSpinner.getSelectedItemPosition()));
                finish();
            });
        } else {
            dutiesViews = new ArrayList<>();
            cleaningTaskIconMap = new HashMap<>();

            HashMap<String, Duty> dutiesMap = Cleaning.getInstance().getDutiesMap();
            numberDuties = dutiesMap.size();

            cleaningTaskResources = allResources.entrySet()
                    .stream()
                    .filter(map -> map.getKey().contains(cleaningTaskPrefix))
                    .collect(Collectors.toMap(map -> map.getKey().substring(cleaningTaskPrefix.length()), map -> map.getValue()));

            cleaningTaskNames = new ArrayList<>(cleaningTaskResources.keySet());

            findViewById(R.id.leftArrowImageView).setOnClickListener(v -> {
                if (numberDuties > 0) {
                    numberDuties--;

                    Pair<Duty, View> elementToRemove = dutiesViews.get(dutiesViews.size() - 1);
                    cleaningTaskIconMap.remove(elementToRemove.second.findViewById(R.id.spinner));

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

                    duty.setIcon(cleaningTaskPrefix + cleaningTaskNames.get(spinner.getSelectedItemPosition()));
                    duty.setTitle(titleTextView.getText().toString());

                    newDutiesMap.put(duty.getId(), duty);
                });

                Cleaning cleaningInstance = Cleaning.getInstance();

                cleaningInstance.setCleaningMap(newDutiesMap);
                cleaningInstance.deleteCurrentWeek();

                cleaningInstance.syncCleaning();

                Users.getInstance().update(usernameTextView.getText().toString(),
                        avatarPrefix + avatarNames.get(avatarSpinner.getSelectedItemPosition()));
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
                android.R.layout.simple_spinner_item, cleaningTaskNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = spinnerView.findViewById(R.id.spinner);

        spinner.setAdapter(adapter);

        cleaningTaskIconMap.put(spinner, spinnerView.findViewById(R.id.imageView));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Drawable newIcon = cleaningTaskResources.get(cleaningTaskNames.get(position));
                ImageView imageView = cleaningTaskIconMap.get(parent);
                imageView.setImageDrawable(newIcon);
                imageView.setImageTintList(null);
                imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (duty != null) {
            String iconName = duty.getIcon().substring(cleaningTaskPrefix.length());

            spinner.setSelection(cleaningTaskNames.indexOf(iconName));

            TextView nameTextView = spinnerView.findViewById(R.id.editTextName);
            nameTextView.setText(duty.getTitle());
        }

        return spinnerView;
    }
}
