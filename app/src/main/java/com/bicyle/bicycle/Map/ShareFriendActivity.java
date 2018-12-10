package com.bicyle.bicycle.Map;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.MapActivity;
import com.bicyle.bicycle.ProfDataSet;
import com.bicyle.bicycle.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShareFriendActivity extends AppCompatActivity{


    ListView listview;
    ShareFriendAdapter shareAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_friend);

        //firebaseDatabase 설정
        DataManager.getInstance().firebaseDatabase = FirebaseDatabase.getInstance();
        DataManager.getInstance().databaseReference = DataManager.getInstance().firebaseDatabase.getReference();

        final myValueEventListener valueEventListener = new myValueEventListener();

        ArrayList<String> shareList= (ArrayList<String>) getIntent().getSerializableExtra("friendList");

        listview= (ListView)findViewById(R.id.share_friend);
        shareAdapter=new ShareFriendAdapter(ShareFriendActivity.this,R.layout.map_friend_row, (ArrayList<ProfDataSet>) DataManager.getInstance().getmProfileDataList(), shareList);
        listview.setAdapter(shareAdapter);


        //listview 클릭이벤트
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ProfDataSet curItem = (ProfDataSet)shareAdapter.getItem(position);

                DataManager.getInstance().databaseReference.child("otherLocation").child(curItem.getUid()).addValueEventListener(valueEventListener);

            }
        });


    }



    public class myValueEventListener implements ValueEventListener
    {
        public myValueEventListener() {
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean sharegps = (Boolean) dataSnapshot.child("sharegps").getValue();
            String friend_uid = dataSnapshot.child("uid").getValue().toString();
            String nickname = dataSnapshot.child("nickname").getValue().toString();
            String latitude =  dataSnapshot.child("latitude").getValue().toString();
            String longitude = dataSnapshot.child("longitude").getValue().toString();
            Log.d("ShareFriendActivity", friend_uid+"\n" + sharegps);

            if (sharegps) //허용이면
            {
                Intent intent = new Intent();
                intent.putExtra("uid",friend_uid);
                intent.putExtra("nickname",nickname);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("result", "shareFriend");
                setResult(RESULT_OK, intent);
                finish();

            } else
            {
                Toast.makeText(getApplicationContext(),"상대방이 위치공유를 허용하고있지 않습니다.",Toast.LENGTH_SHORT).show();
            }

            DataManager.getInstance().databaseReference.child("otherLocation").child(friend_uid).removeEventListener(this);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    }
}
