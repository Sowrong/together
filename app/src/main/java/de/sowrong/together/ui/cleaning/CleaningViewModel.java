package de.sowrong.together.ui.cleaning;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import de.sowrong.together.data.Calendar;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Cleaning;
import de.sowrong.together.data.CleaningDataListener;
import de.sowrong.together.data.CleaningWeek;
import de.sowrong.together.data.Duty;
import de.sowrong.together.data.Group;

public class CleaningViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, CleaningWeek>> cleaning;
    private MutableLiveData<HashMap<String, ArrayList<Duty>>> duties;

    public CleaningViewModel() {
        cleaning = new MutableLiveData<>();
        cleaning.setValue(Cleaning.getInstance().getCleaningMap());

        duties = new MutableLiveData<>();
        duties.setValue(Cleaning.getInstance().getDutiesMap());

        Group.getInstance().addCleaningDataChangedListeners(new CleaningDataListener() {
            @Override
            public void onCleaningDataChanged(HashMap<String, CleaningWeek> cleaningMap) {
                cleaning.setValue(cleaningMap);
            }
            @Override
            public void onDutyDataChanged(HashMap<String, ArrayList<Duty>> duties) { }
        });
    }

    public LiveData<HashMap<String, CleaningWeek>> getClening() {
        return cleaning;
    }
    public LiveData<HashMap<String, ArrayList<Duty>>> getDuties() {
        return duties;
    }
}
