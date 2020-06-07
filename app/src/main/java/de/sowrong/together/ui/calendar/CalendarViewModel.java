package de.sowrong.together.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import de.sowrong.together.data.Calendar;
import de.sowrong.together.data.CalendarEntry;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;

public class CalendarViewModel extends ViewModel {
    private MutableLiveData<HashMap<String, CalendarEntry>> calendar;

    public CalendarViewModel() {
        calendar = new MutableLiveData<>();
        calendar.setValue(Calendar.getInstance().getCalendarMap());
        Group.getInstance().addCalendarDataChangedListeners(calendar::setValue);
    }

    public LiveData<HashMap<String, CalendarEntry>> getCalendar() {
        return calendar;
    }
}
