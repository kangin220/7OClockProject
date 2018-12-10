package com.bicyle.bicycle;

import java.io.Serializable;

public class ClubInfo implements Serializable {
    private String ownerUid;
    private String clubIntro;
    private String clubName;
    private String clubLocation;
    private String clubNotice;

    public ClubInfo() {}

    public ClubInfo(String clubName, String ownerUid, String clubLocation, String clubIntro, String clubNotice) {
        this.clubName = clubName;
        this.ownerUid = ownerUid;
        this.clubLocation = clubLocation;
        this.clubIntro = clubIntro;
        this.clubNotice = clubNotice;
    }

    public void setClubName(String clubName) { this.clubName = clubName; }

    public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; }

    public void setClubLocation(String clubLocation) { this.clubLocation = clubLocation; }

    public void setClubIntro(String clubIntro) { this.clubIntro = clubIntro; }

    public void setClubNotice(String clubNotice) { this.clubNotice = clubNotice; }

    public String getClubName() { return clubName; }

    public String getOwnerUid() { return  ownerUid; }

    public String getClubLocation() { return clubLocation; }

    public String getClubIntro() { return clubIntro; }

    public String getClubNotice() { return clubNotice; }
}
