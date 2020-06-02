package de.sowrong.together.data;

import java.util.HashMap;

public interface UserDataListener {
    public void onUserDataChanged(HashMap<String, User> users);
}