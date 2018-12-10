package com.bicyle.bicycle.util;

public class RandomMatching {
    private String uid;
    private String location;

    public RandomMatching() {

    }
    public RandomMatching(String uid, String location) {
        this.uid = uid;
        this.location = location;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public String getLocation() {
        return location;
    }

}
