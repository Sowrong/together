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
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;

public class CleaningViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, Member>> members;
    private MutableLiveData<HashMap<String, CleaningWeek>> cleaning;
    private MutableLiveData<HashMap<String, ArrayList<Duty>>> duties;

    public CleaningViewModel() {
        members = new MutableLiveData<>();
        members.setValue(Members.getInstance().getMembersMap());

        cleaning = new MutableLiveData<>();
        cleaning.setValue(Cleaning.getInstance().getCleaningMap());

        duties = new MutableLiveData<>();
        duties.setValue(Cleaning.getInstance().getDutiesMap());

        Members.getInstance().addMemberDataChangedListeners(membersMap -> members.setValue(membersMap));

        Group.getInstance().addCleaningDataChangedListeners(new CleaningDataListener() {
            @Override
            public void onCleaningDataChanged(HashMap<String, CleaningWeek> cleaningMap) {
                cleaning.setValue(cleaningMap);
            }
            @Override
            public void onDutyDataChanged(HashMap<String, ArrayList<Duty>> dutiesMap) {
                duties.setValue(dutiesMap);
            }
        });
    }

    public LiveData<HashMap<String, Member>> getMembers() {
        return members;
    }
    public LiveData<HashMap<String, CleaningWeek>> getCleaning() {
        return cleaning;
    }
    public LiveData<HashMap<String, ArrayList<Duty>>> getDuties() {
        return duties;
    }
}
