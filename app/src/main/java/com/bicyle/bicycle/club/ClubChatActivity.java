package com.bicyle.bicycle.club;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bicyle.bicycle.ClubInfo;
import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.R;
import com.bicyle.bicycle.util.MyUtil;
import com.bicyle.bicycle.util.UserChatData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClubChatActivity extends AppCompatActivity {
    ClubChatAdapter ccAdpater;
    private String CHAT_NAME; // 채팅방 이름(동호회명이어야함. 지금은 임시로 지역이름)
    private String USER_NAME; // 채팅하는 사용자 이름
    private SimpleDateFormat cChatTime;
    //    private ClubChatAdapter adapter;
    private TextView chat_name; // 채팅방 이름(원래 동호회명/현재 지역명)
    private ListView chat_view; // 메세지 로그(닉네임/메세지) + 시간
    private EditText chat_edit; // 메세지 내용 입력
    private Button chat_send; // 메세지 전송 버튼
    private TextView clubNotice;

    ArrayList<ClubChatDTO> chatList= new ArrayList<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubchat);

        final ClubInfo clubInfo = (ClubInfo) getIntent().getSerializableExtra("clubInfo");

        // 위젯 ID 참조
        clubNotice = (TextView) findViewById(R.id.club_notice);
        chat_name = (TextView) findViewById(R.id.chat_name);
        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        //chat_name = findViewById(R.id.CHAT_NAME);
        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        CHAT_NAME = clubInfo.getClubName();

        chat_name.setText(CHAT_NAME+" 동호회의 채팅방");
        databaseReference.child("clubInfo").child(clubInfo.getClubName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clubNotice.setText("동호회 공지: " + dataSnapshot.child("clubNotice").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        USER_NAME = DataManager.getInstance().getNickname();
        // 채팅 방 입장
        openChat(CHAT_NAME);

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;

                //long chatTime = System.currentTimeMillis(); // userchat에서 가져와본거
                ClubChatDTO clubChat = new ClubChatDTO(USER_NAME, chat_edit.getText().toString(), MyUtil.getDate()); //ChatDTO를 이용해서 데이터 묶음
                databaseReference.child("clubChat").child(CHAT_NAME).push().setValue(clubChat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화

            }
        });

        if(clubInfo.getOwnerUid().equals(DataManager.getInstance().getUid())) {
            clubNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View clubNoticeEditView;
                    clubNoticeEditView = View.inflate(ClubChatActivity.this, R.layout.club_notice_edit, null);

                    final EditText clubNoticeET = clubNoticeEditView.findViewById(R.id.clubNoticeEdit);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ClubChatActivity.this);

                    builder.setTitle("클럽 공지");
                    builder.setView(clubNoticeEditView);

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clubNotice.setText(clubNoticeET.getText().toString());
                            databaseReference.child("clubInfo").child(clubInfo.getClubName()).child("clubNotice").setValue(clubNoticeET.getText().toString());
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    builder.show();

                }
            });
        }

    }

    private void addMessage(DataSnapshot dataSnapshot) {
        ClubChatDTO chatDTO = dataSnapshot.getValue(ClubChatDTO.class);
        Log.d("taaaaa",chatDTO.getTime());
        ccAdpater.add(chatDTO);
        chat_view.smoothScrollToPosition(ccAdpater.getCount());
//        chatList.add(chatDTO);
//        ccAdpater.notifyDataSetChanged();
        // ListView.smoothScrollToPosition(Adapter.getCount()); - 하는중
    }

    private void removeMessage(DataSnapshot dataSnapshot) {
        ClubChatDTO chatDTO = dataSnapshot.getValue(ClubChatDTO.class);
        ccAdpater.remove(chatDTO);
        chat_view.smoothScrollToPosition(ccAdpater.getCount());
//        chatList.remove(chatDTO);
//        ccAdpater.notifyDataSetChanged();
    }

    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅

        ccAdpater = new ClubChatAdapter(this,0,DataManager.getInstance().getNickname());
        chat_view.setAdapter(ccAdpater);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("clubChat").child(chatName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}