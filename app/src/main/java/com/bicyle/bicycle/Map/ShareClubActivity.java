package com.bicyle.bicycle.Map;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShareClubActivity extends AppCompatActivity {

    ShareClubDTO dto; //선택한 동호회의 정보, 이름과 위치공유한 uid
    ListView listview;
    ShareClubAdapter shareAdapter;
    ArrayList<String> mClubInfoList = new ArrayList<>();
    clubNameEventListener mclubNameEventListener;
    clubMemberEventListener mclubMemberEventListener;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_club);

        //firebaseDatabase 설정
        databaseReference = FirebaseDatabase.getInstance().getReference();


        ArrayList<ShareClubDTO> getShareClub = (ArrayList<ShareClubDTO>) getIntent().getSerializableExtra("clubList");

        Log.d("ShareClubActivity", "onCreate");
        //데이터 베이스에서 동호회 목록을 불러오는 부분
        mclubNameEventListener= new clubNameEventListener();
        databaseReference.child("myClubList").child(DataManager.getInstance().getUid()).addValueEventListener(mclubNameEventListener);

        //데이터 베이스에서 동호회 회원목록을 불러오는 부분
        mclubMemberEventListener= new clubMemberEventListener();


        listview= (ListView)findViewById(R.id.share_club);
        shareAdapter=new ShareClubAdapter(ShareClubActivity.this,R.layout.map_club_row, mClubInfoList,getShareClub);
        listview.setAdapter(shareAdapter);


        //listview 클릭이벤트
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clubName = (String)shareAdapter.getItem(position);
                dto=new ShareClubDTO(clubName);
                Log.d("ShareClubActivity", clubName);
                databaseReference.child("clubMemberList").child(dto.getClubName()).addValueEventListener(mclubMemberEventListener);
            }
        });


    }



    //내가가입한 동호회 이름불러오는 부분
    public class clubNameEventListener implements ValueEventListener
    {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                String clubName = dataSnap.child("clubName").getValue().toString();
                Log.d("ShareClubActivity", clubName);
                mClubInfoList.add(clubName);
                shareAdapter.notifyDataSetChanged();
            }
            databaseReference.child("myClubList").child(DataManager.getInstance().getUid()).removeEventListener(mclubNameEventListener);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    //내가가입한 동호회 회원목록 불러오는 부분
    public class clubMemberEventListener implements ValueEventListener
    {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                String uid = dataSnap.child("uid").getValue().toString();
                //각 동호회당 모든 uid를 뽑음
                Log.d("ShareClubActivity", uid);
                dto.getClubMemberUidList().add(uid);
            }
            databaseReference.child("clubMemberList").child(dto.getClubName()).removeEventListener(mclubMemberEventListener);

            Intent intent = new Intent();
            intent.putExtra("clubInfo",dto);
            intent.putExtra("result", "shareClub");
            setResult(RESULT_OK, intent);
            finish();


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }




}
