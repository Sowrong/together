package de.sowrong.together.data;

import java.util.ArrayList;
import java.util.HashMap;

public class Member {

    private static ArrayList<MemberDataListener> listeners;


    public void addMemberDataChangedListeners(MemberDataListener listener) {
        // Add the listener to the list of registered listeners
        listeners.add(listener);
    }
    public void removeMemberDataChangedListeners(MemberDataListener listener) {
        // Remove the listener from the list of the registered listeners
        listeners.remove(listener);
    }

    protected void notifyMemberDataChangedListeners(HashMap<String, Member> memberDataList) {
        // Notify each of the listeners in the list of registered listeners
        for (MemberDataListener listener: this.listeners) {
            listener.onMemberDataChanged(memberDataList);
        }
    }
}
