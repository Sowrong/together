package de.sowrong.together.data;

import android.media.Image;

public class User {
    private String id;
    private String groupId;
    private String name;
    private double balance;

    //private Image avatar;


    public User(String id, String groupId, String name) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
