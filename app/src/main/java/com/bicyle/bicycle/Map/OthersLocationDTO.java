package com.bicyle.bicycle.Map;

public class OthersLocationDTO {
    private String uid;
    private String nickname;
    private double latitude;
    private double longitude;
    private boolean sharegps;

    public OthersLocationDTO(String uid, String nickname,double latitude, double longitude, boolean sharegps) {
        this.uid = uid;
        this.nickname=nickname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sharegps = sharegps;
    }



    public OthersLocationDTO()
    {

    }

    public void setSharegps(boolean sharegps) {
        this.sharegps = sharegps;
    }

    public boolean getSharegps() {
        return sharegps;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}