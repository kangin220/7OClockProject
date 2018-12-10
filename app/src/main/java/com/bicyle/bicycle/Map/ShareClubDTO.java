package com.bicyle.bicycle.Map;

import java.io.Serializable;
import java.util.ArrayList;

public class ShareClubDTO implements Serializable {

    public String clubName ; //동호회 이름

    ArrayList<String> clubMemberUidList= new ArrayList<>(); // 위치공유를 허락한 멤버들



    public ShareClubDTO(String clubName) {
        this.clubName = clubName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public ArrayList<String> getClubMemberUidList() {
        return clubMemberUidList;
    }

    public void setClubMemberUidList(ArrayList<String> clubMemberUidList) {
        this.clubMemberUidList = clubMemberUidList;
    }
}
