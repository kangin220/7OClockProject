package com.bicyle.bicycle.club;

public class ClubChatDTO {

    private String userName;
    private String message;
    private String time;
    public ClubChatDTO() {}
    public ClubChatDTO(String userName, String clubMessage,String time) {
        this.userName = userName;
        this.message = clubMessage;
        this.time = time;
    }

    //    long now = System.currentTimeMillis();
//    Date date = new Date(now);
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd a h:mm");
    public String getTime() {
        return time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String clubMessage) {
        this.message = clubMessage;
    }
    /*
        public void setTime(SimpleDateFormat cChatTime) {
            this.cChatTime = cChatTime;
        }

        public long getTime() {
            return cChatTime;
        }
    */
    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}