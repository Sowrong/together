package de.sowrong.together.ui.shoppingList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShoppingListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShoppingListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the shopping list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}