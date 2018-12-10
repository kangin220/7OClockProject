package com.bicyle.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MakeFriend extends AppCompatActivity {

    String friendNickName = "";
    String myNickname = "";
    String friendUid = "";
    String senderUid = "";
    String myUid;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    TextView make_friend_nickname;
    TextView make_friend_gender;
    TextView make_friend_age;
    TextView make_friend_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_friend);

        Intent intent = getIntent();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myNickname = intent.getStringExtra("myNickName");
        senderUid = intent.getStringExtra("senderUid");
        friendNickName = intent.getStringExtra("senderNickName");
        make_friend_nickname = (TextView) findViewById(R.id.make_friend_id);
        make_friend_gender = (TextView) findViewById(R.id.make_friend_gender);
        make_friend_location = (TextView) findViewById(R.id.make_friend_location);
        make_friend_age = (TextView) findViewById(R.id.make_friend_age);
        Button positiveBtn = findViewById(R.id.postiveBtn);

        mDatabase.child("Profiles").child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("make_friend","asdf");
                make_friend_nickname.setText("아이디 : " +dataSnapshot.child("nickname").getValue().toString());
                make_friend_location.setText("지역 : " +dataSnapshot.child("location").getValue().toString());
                make_friend_gender.setText("성별 : "+dataSnapshot.child("gender").getValue().toString());
                make_friend_age.setText("나이 : "+dataSnapshot.child("age").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                mDatabase.child("FrdRelship").child(myUid).push().child("uid").setValue(senderUid);
                mDatabase.child("FrdRelship").child(senderUid).push().child("uid").setValue(myUid);
                finish();
            }
        });

        Button negativeBtn = (Button) findViewById(R.id.negativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
    }

}