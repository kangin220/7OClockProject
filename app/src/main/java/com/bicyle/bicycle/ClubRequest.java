package com.bicyle.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClubRequest extends AppCompatActivity {

    ClubInfo clubInfo;

    ArrayAdapter mRequestAdapter;
    List<ProfDataSet> mRequestInfoList = new ArrayList<>();
    ArrayList<String> mRequestList = new ArrayList<>(); // show for Listview
    ListView requestListView;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_request);

        clubInfo = (ClubInfo) getIntent().getSerializableExtra("clubInfo");

        requestListView = (ListView) findViewById(R.id.clubRequestListView);

        mDatabase.child("clubRequest").child(clubInfo.getClubName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> requestUidList = new ArrayList<>();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    String requestUid = dataSnap.child("uid").getValue().toString();
//                    Toast.makeText(ClubActivity.this, clubName, Toast.LENGTH_SHORT).show();
                    requestUidList.add(requestUid);
                }
                getRequestInfo(requestUidList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRequestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mRequestList);
        requestListView.setAdapter(mRequestAdapter);
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //프로필 띄우기
                Intent profPopIntent = new Intent(getApplicationContext(), ProfilePopup.class);
                profPopIntent.putExtra("reqProf", mRequestInfoList.get(position));
                profPopIntent.putExtra("preAct", "request");
                startActivityForResult(profPopIntent, 22);
                mRequestList.remove(position);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if(requestCode == 22) {
            final ProfDataSet prof = (ProfDataSet) data.getSerializableExtra("prof");

            Query query = mDatabase.child("clubRequest").child(clubInfo.getClubName()).orderByChild("uid").equalTo(prof.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (resultCode == 23) {
                        mDatabase.child("myClubList").child(prof.getUid()).push().child("clubName").setValue(clubInfo.getClubName());
                        mDatabase.child("clubMemberList").child(clubInfo.getClubName()).push().child("uid").setValue(prof.getUid());
                    }
                    dataSnapshot.child(prof.getUid()).child("uid").getRef().removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return;
    }

    private void getRequestInfo(List<String> requestUidList) {
        mRequestAdapter.clear();
        mRequestInfoList.clear();
        mRequestList.clear();
        for (String uid : requestUidList) {
            Query query = mDatabase.child("Profiles").child(uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfDataSet memberProf = dataSnapshot.getValue(ProfDataSet.class);
                    if (memberProf != null) {
                        mRequestInfoList.add(memberProf);
                        mRequestList.add(memberProf.getNickname());
                        mRequestAdapter.notifyDataSetChanged();
                    }
                    requestListView.setAdapter(mRequestAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}