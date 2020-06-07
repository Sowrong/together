package de.sowrong.together.ui.wallet;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;

import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.data.Members;

public class WalletViewModel extends ViewModel {
    private MutableLiveData<HashMap<String, Member>> members;

    public WalletViewModel() {
        members = new MutableLiveData<>();
        members.setValue(Members.getInstance().getMembersMap());
        Group.getInstance().addMemberDataChangedListeners(members::setValue);
    }

    public LiveData<HashMap<String, Member>> getMembers() {
        return members;
    }
}