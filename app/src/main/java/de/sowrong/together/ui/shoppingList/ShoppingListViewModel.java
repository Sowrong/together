package de.sowrong.together.ui.shoppingList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.ShoppingList;
import de.sowrong.together.data.ShoppingListEntry;

public class ShoppingListViewModel extends ViewModel {
    private MutableLiveData<HashMap<String, ShoppingListEntry>> shoppingList;

    public ShoppingListViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(ShoppingList.getInstance().getShoppingListMap());
        Group.getInstance().addShoppingListDataChangedListeners(value -> shoppingList.setValue(value));
    }

    public LiveData<HashMap<String, ShoppingListEntry>> getShoppingList() {
        return shoppingList;
    }
}