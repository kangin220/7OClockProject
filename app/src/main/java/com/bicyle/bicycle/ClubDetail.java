package com.bicyle.bicycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.club.ClubChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClubDetail extends AppCompatActivity {

    ArrayAdapter mMemberAdapter;
    List<ProfDataSet> mMemberInfoList = new ArrayList<>();
    ArrayList<String> mMemberList = new ArrayList<>(); // show for Listview

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    Button clubReqBtn;

    TextView clubNameText;
    TextView clubIntroText;
    TextView clubMemberNum;
    TextView clubLocText;
    TextView clubLeaderText;
    ListView clubMemberListView;
    Button clubChatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_detail);

        final ClubInfo clubInfo = (ClubInfo) getIntent().getSerializableExtra("clubInfo");

//        Toast.makeText(ClubDetail.this, clubInfo.getClubName(), Toast.LENGTH_SHORT).show();


        clubNameText = (TextView) findViewById(R.id.clubNameText);
        clubIntroText = (TextView) findViewById(R.id.clubIntroText);
        clubMemberNum = (TextView) findViewById(R.id.clubMemberNum);
        clubLocText = (TextView) findViewById(R.id.clubLocText);
        clubLeaderText = (TextView) findViewById(R.id.clubLeaderText);
        clubMemberListView = (ListView) findViewById(R.id.clubMemberListView);
        clubChatBtn = (Button) findViewById(R.id.clubChatBtn);
        clubReqBtn = (Button) findViewById(R.id.clubReqBtn);

        clubReqBtn.setVisibility(View.INVISIBLE);

        if (DataManager.getInstance().getUid().equals(clubInfo.getOwnerUid())) {
            clubReqBtn.setVisibility(View.VISIBLE);
            mDatabase.child("clubRequest").child(clubInfo.getClubName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clubReqBtn.setText("가입 요청( "+dataSnapshot.getChildrenCount()+" )");
//                    reqCnt = dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            clubReqBtn.setText("가입 요청( "+reqCnt+" )");

            clubReqBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clubReqIntent = new Intent(getApplicationContext(), ClubRequest.class);
                    clubReqIntent.putExtra("clubInfo", clubInfo);
                    startActivity(clubReqIntent);
                }
            });

            clubMemberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                    android.support.v7.app.AlertDialog.Builder alt_bld = new android.support.v7.app.AlertDialog.Builder(view.getContext());
                    alt_bld.setMessage("동호회에서 추방하시겠습니까?").setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String memberName = (String)parent.getAdapter().getItem(position);
                                    Query query = mDatabase.child("Profiles").orderByChild("nickname").equalTo(memberName);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                                String delMemberUid = dataSnap.child("uid").getValue().toString();
                                                Query qry = mDatabase.child("clubMemberList").child(clubInfo.getClubName()).orderByChild("uid").equalTo(delMemberUid);
                                                qry.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                                        for(DataSnapshot inDataSanp : inDataSnapshot.getChildren()) {
                                                            inDataSanp.child("uid").getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                qry = mDatabase.child("myClubList").child(delMemberUid).orderByChild("clubName").equalTo(clubInfo.getClubName());
                                                qry.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                                        for(DataSnapshot inDataSanp : inDataSnapshot.getChildren()) {
                                                            inDataSanp.child("clubName").getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                mMemberList.remove(memberName);
                                                mMemberAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    dialog.cancel();
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alt_bld.create();

                    alert.setTitle("동호회 추방");

                    alert.show();
                    return true;
                }
            });

            clubIntroText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View clubIntroEditView;
                    clubIntroEditView = View.inflate(ClubDetail.this, R.layout.club_intro_edit, null);

                    final EditText clubIntroET = clubIntroEditView.findViewById(R.id.clubIntroEdit);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ClubDetail.this);

                    builder.setTitle("클럽 소개글");
                    builder.setView(clubIntroEditView);

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clubIntroText.setText(clubIntroET.getText().toString());
                            mDatabase.child("clubInfo").child(clubInfo.getClubName()).child("clubIntro").setValue(clubIntroET.getText().toString());
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    builder.show();

                }
            });

            clubLocText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                    getMenuInflater().inflate(R.menu.location_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            clubLocText.setText(item.getTitle().toString());
                            mDatabase.child("clubInfo").child(clubInfo.getClubName()).child("clubLocation").setValue(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        mDatabase.child("clubInfo").child(clubInfo.getClubName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clubNameText.setText(dataSnapshot.child("clubName").getValue().toString());
                clubIntroText.setText(dataSnapshot.child("clubIntro").getValue().toString());
                clubLocText.setText(dataSnapshot.child("clubLocation").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mDatabase.child("clubMemberList").child(clubInfo.getClubName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int memberCnt = 0;
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    memberCnt++;
                }
                clubMemberNum.setText("동호회 회원 : " +String.valueOf(memberCnt)+" 명");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("clubInfo").child(clubInfo.getClubName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ownerUid = dataSnapshot.child("ownerUid").getValue().toString();

                mDatabase.child("Profiles").child(ownerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot innerDataSnapshot) {
                        String ownerName = innerDataSnapshot.child("nickname").getValue().toString();
                        clubLeaderText.setText(ownerName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("clubMemberList").child(clubInfo.getClubName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> memberUidList = new ArrayList<>();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    String memberUid = dataSnap.child("uid").getValue().toString();
//                    Toast.makeText(ClubActivity.this, clubName, Toast.LENGTH_SHORT).show();
                    memberUidList.add(memberUid);
                }
                getMemberInfo(memberUidList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMemberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mMemberList);
        clubMemberListView.setAdapter(mMemberAdapter);
        clubMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //프로필 띄우기
                Intent profPopIntent = new Intent(getApplicationContext(), ProfilePopup.class);
                profPopIntent.putExtra("reqProf", mMemberInfoList.get(position));
                profPopIntent.putExtra("preAct", "detail");
                startActivityForResult(profPopIntent, 22);
            }
        });

        clubChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clubChatIntent = new Intent(ClubDetail.this, ClubChatActivity.class);
                clubChatIntent.putExtra("clubInfo", clubInfo);
                startActivity(clubChatIntent);
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 22) {
            ProfDataSet prof = (ProfDataSet) getIntent().getSerializableExtra("prof");
        }
        return;
    }

    private void getMemberInfo(List<String> memberUidList) {
        mMemberList.clear();
        for (String uid : memberUidList) {
            Query query = mDatabase.child("Profiles").child(uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfDataSet memberProf = dataSnapshot.getValue(ProfDataSet.class);
                    if (memberProf != null) {
                        mMemberInfoList.add(memberProf);
                        mMemberList.add(memberProf.getNickname());
                        mMemberAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
