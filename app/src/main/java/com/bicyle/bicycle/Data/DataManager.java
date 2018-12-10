package com.bicyle.bicycle.Data;

import com.bicyle.bicycle.Board.BoardDTO;
import com.bicyle.bicycle.Board.ReplyDTO;
import com.bicyle.bicycle.ClubInfo;
import com.bicyle.bicycle.ProfDataSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class DataManager {



    //임시 userName
    //public String userName = "user" + new Random().nextInt(10000);  // 랜덤한 유저 이름 설정 ex) user1234
    private String uid = "";
    private String nickname = "";
    private String location = "";
    private String age = "";
    private String gender = "";
    private String deviceToken = "";

    private List<ProfDataSet> mProfileDataList = new ArrayList<>(); //내가 추가한 친구목록


    public List<ProfDataSet> getmProfileDataList() {
        return mProfileDataList;
    }

    public void setmProfileDataList(List<ProfDataSet> mProfileDataList) {
        this.mProfileDataList = mProfileDataList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void setDataManager(DataManager dataManager) {
        DataManager.dataManager = dataManager;
    }

    private static DataManager dataManager = new DataManager();
    public static DataManager getInstance(){
        return dataManager;
    }

    public FirebaseDatabase firebaseDatabase ;
    public DatabaseReference databaseReference;

    public DescendingBoard descendingBoard = new DescendingBoard();
    public DescendingReply descendingReply = new DescendingReply();
    public LikeFilterBoard likeFilterBoard = new LikeFilterBoard();


    class DescendingBoard implements Comparator<BoardDTO>
    {
        @Override
        public int compare(BoardDTO o1, BoardDTO o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }

    class DescendingReply implements Comparator<ReplyDTO>
    {
        @Override
        public int compare(ReplyDTO o1, ReplyDTO o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }

    class LikeFilterBoard implements Comparator<BoardDTO>
    {
        @Override
        public int compare(BoardDTO o1, BoardDTO o2) {
            if (o2.getLikeNum() < o1.getLikeNum()) {
                return -1;
            } else if (o2.getLikeNum() > o1.getLikeNum()) {
                return 1;
            }
            return 0;

        }
    }

}
