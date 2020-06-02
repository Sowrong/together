package de.sowrong.together.ui.wallet;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;

public class WalletViewModel extends ViewModel {
    private MutableLiveData<HashMap<String, User>> users;

    public WalletViewModel() {
        users = new MutableLiveData<>();
        users.setValue(new HashMap<>());

        Users.getInstance().addUserDataChangedListeners(usersMap -> {
            this.users.setValue(usersMap);

            for (Map.Entry<String, User> entry : usersMap.entrySet()) {
                User user = entry.getValue();
                Log.d("WalletViewModel", user.getName());
            }
        });
    }

    /*
    private void updateData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(user.getUid()))  {
                                    username.setValue((String)document.getData().get("nickname"));
                                }
                            }
                        }
                    }
                });

        db.collection("groups/QkSmCjLb1uchbLGRgbmg/money")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(user.getUid()))  {
                                    Log.d("Firebase", "Balance: " + document.getData().get("balance"));
                                    balance.setValue((Double)document.getData().get("balance"));
                                }
                                Log.d("Firebase", "Queried data");
                            }
                        } else {
                            Log.w("Firebase", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
     */

    public LiveData<HashMap<String, User>> getUsers() {
        return users;
    }
}