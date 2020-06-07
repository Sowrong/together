package de.sowrong.together.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Users {
    private static Users instance;
    private static HashMap<String, User> usersMap;
    private static String ownId;
    private static ArrayList<UserDataListener> listeners;

    private final String DB_CONNECTOR_TAG = "Firebase";

    public Users() {
        ownId = "";
        usersMap = new HashMap<>();
        listeners = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference usersReference = db.getReference().child("users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    usersMap.clear();

                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        String id = childSnapshot.getKey();
                        String groupId = "";
                        String name = "";

                        for (DataSnapshot userDetailSnapshot: childSnapshot.getChildren()) {
                            switch (userDetailSnapshot.getKey()) {
                                case "groupId":
                                    groupId = (String)userDetailSnapshot.getValue();
                                    break;
                                case "name":
                                    name = (String)userDetailSnapshot.getValue();
                                    break;
                            }
                        }

                        User nextUser = new User(id, groupId, name);
                        usersMap.put(id, nextUser);

                        if (user.getUid().equals(id)) {
                            ownId = id;
                        }
                    }
                }

                notifyUserDataChangedListeners(usersMap);

                Log.d(DB_CONNECTOR_TAG, "users updated");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(DB_CONNECTOR_TAG, "failed to update users", error.toException());
            }
        };

        usersReference.addValueEventListener(valueEventListener);

        // grab initial data
        usersReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public static String getOwnId() {
        if (ownId.isEmpty())
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            return ownId;
    }

    public static User getUserById(String id) {
        return usersMap.get(id);
    }

    public static HashMap<String, User> getUsers() {
        if (instance == null) {
            instance = new Users();
        }

        return usersMap;
    }

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }

        return instance;
    }

    public void addUserDataChangedListeners(UserDataListener listener) {
        // Add the listener to the list of registered listeners
        listeners.add(listener);
    }
    public void removeUserDataChangedListeners(UserDataListener listener) {
        // Remove the listener from the list of the registered listeners
        listeners.remove(listener);
    }

    protected void notifyUserDataChangedListeners(HashMap<String, User> userList) {
        // Notify each of the listeners in the list of registered listeners
        for (UserDataListener listener: this.listeners) {
            listener.onUserDataChanged(userList);
        }
    }
}
