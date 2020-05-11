package de.sowrong.together.ui.cleaning;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CleaningViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CleaningViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the cleaning fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}