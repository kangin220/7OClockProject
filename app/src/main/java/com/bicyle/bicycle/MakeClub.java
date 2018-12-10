package com.bicyle.bicycle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bicyle.bicycle.Data.DataManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MakeClub extends AppCompatActivity {

    ClubInfo clubInfo = new ClubInfo();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_make);

        final EditText clubNameEtext = (EditText) findViewById(R.id.clubNameEtext);
        final EditText clubIntroEtext = (EditText) findViewById(R.id.clubIntroEtext);

        Button clubMakeBtn = (Button) findViewById(R.id.clubMakeBtn);

        clubInfo.setClubNotice("");

        final String[] locList = getResources().getStringArray(R.array.locList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locList);
        Spinner locSpinner = (Spinner)findViewById(R.id.clubLocSpin);
        locSpinner.setAdapter(adapter);
        locSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clubInfo.setClubLocation(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clubMakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clubNameEtext.getText().toString().isEmpty() || clubInfo.getClubLocation().equals("")) {
                    Toast.makeText(MakeClub.this, "필요한 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Query query = mDatabase.child("clubInfo").orderByChild("clubName").equalTo(clubNameEtext.getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean existClub = dataSnapshot.exists();
                            if(existClub) {
                                Toast.makeText(MakeClub.this, "이미 존재하는 동호회입니다.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                clubInfo.setClubName(clubNameEtext.getText().toString());
                                clubInfo.setClubIntro(clubIntroEtext.getText().toString());
                                clubInfo.setOwnerUid(DataManager.getInstance().getUid());
                                mDatabase.child("clubInfo").child(clubNameEtext.getText().toString()).setValue(clubInfo);
                                mDatabase.child("myClubList").child(DataManager.getInstance().getUid()).push().child("clubName").setValue(clubNameEtext.getText().toString());
                                mDatabase.child("clubMemberList").child(clubNameEtext.getText().toString()).push().child("uid").setValue(DataManager.getInstance().getUid());

                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

}
