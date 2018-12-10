package com.bicyle.bicycle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bicyle.bicycle.Board.BoardActivity;
import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.util.UserChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClubActivity extends AppCompatActivity {
    ListView listView;
    Toolbar mainToolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;
    long pressTime;
    ListView clubListView;
    EditText clubEditText;
    ImageButton clubSrchBtn;

    ArrayAdapter mClubAdapter;
    List<ClubInfo> mClubInfoList = new ArrayList<>();
    ArrayList<String> mClubList = new ArrayList<>(); // show for Listview

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - pressTime <2000){
            finishAffinity();
            return;
        }
        Toast.makeText(this,"한 번더 누르시면 앱이 종료됩니다",Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_main);

        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.addDrawerListener(dtToggle);

        final String[] naviMenuList = getResources().getStringArray(R.array.naviClubMenu);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.navi_listview, naviMenuList);
        actionBar.setTitle("동호회 목록");

        listView = (ListView) findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Intent mainIntent = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(mainIntent);
                        finish();
                        break;
                    case 1:
                        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(mapIntent);
                        finish();
                        break;
                    case 2:
                        Intent boardIntent = new Intent(getApplicationContext(), BoardActivity.class);
                        startActivity(boardIntent);
                        finish();
                        break;
                }
                dlDrawer.closeDrawer(Gravity.LEFT);
            }
        });

        clubSrchBtn = findViewById(R.id.clubSrchBtn);
        mClubAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mClubList);
        clubListView = findViewById(R.id.clubList);
        clubListView.setAdapter(mClubAdapter);
        clubListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent clubDetailIntent = new Intent(ClubActivity.this, ClubDetail.class);
                clubDetailIntent.putExtra("clubInfo", mClubInfoList.get(position));
                startActivity(clubDetailIntent);
            }
        });

        clubListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                if (DataManager.getInstance().getUid().equals(mClubInfoList.get(position).getOwnerUid())) {
                    mDatabase.child("clubMemberList").child(mClubInfoList.get(position).getClubName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount() == 1) {
                                AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                                alt_bld.setMessage("동호회에서 탈퇴하시겠습니까?").setCancelable(false)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mDatabase.child("clubInfo").child(mClubInfoList.get(position).getClubName()).removeValue();
                                                mDatabase.child("clubMemberList").child(mClubInfoList.get(position).getClubName()).removeValue();
                                                Query query = mDatabase.child("myClubList").child(DataManager.getInstance().getUid()).orderByChild("clubName").equalTo(mClubInfoList.get(position).getClubName());
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                                        for(DataSnapshot inDataSnap: inDataSnapshot.getChildren()) {
                                                            inDataSnap.getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                mClubInfoList.remove(position);
                                                mClubAdapter.notifyDataSetChanged();
                                                dialog.cancel();
                                            }
                                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = alt_bld.create();

                                alert.setTitle("동호회 탈퇴");

                                alert.show();
                            }
                            else {
                                AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                                alt_bld.setMessage("동호회원이 남아있으면 탈퇴가 불가능합니다.").setCancelable(false)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = alt_bld.create();

                                alert.setTitle("동호회 탈퇴");

                                alert.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                    alt_bld.setMessage("동호회에서 탈퇴하시겠습니까?").setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Query query = mDatabase.child("myClubList").child(DataManager.getInstance().getUid()).orderByChild("clubName").equalTo(mClubInfoList.get(position).getClubName());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                            for(DataSnapshot inDataSnap: inDataSnapshot.getChildren()) {
                                                inDataSnap.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    query = mDatabase.child("clubMemberList").child(mClubInfoList.get(position).getClubName()).orderByChild("uid").equalTo(DataManager.getInstance().getUid());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                            for(DataSnapshot inDataSnap: inDataSnapshot.getChildren()) {
                                                inDataSnap.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    mClubInfoList.remove(position);
                                    mClubAdapter.notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = alt_bld.create();

                    alert.setTitle("동호회 탈퇴");

                    alert.show();
                }
                return true;
            }
        });

        //데이터 베이스에서 동호회 목록을 불러오는 부분
        mDatabase.child("myClubList").child(DataManager.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> clubNameList = new ArrayList<>();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    String clubName = dataSnap.child("clubName").getValue().toString();
//                    Toast.makeText(ClubActivity.this, clubName, Toast.LENGTH_SHORT).show();
                    clubNameList.add(clubName);
                }
                getClubInfo(clubNameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        clubEditText = (EditText) findViewById(R.id.clubSrchText);

        //데이터 베이스에서 동호회 이름을 검색해서 가입 요청을 보내는 부분
        clubSrchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mClubList.contains(clubEditText.getText().toString())) {
                    Query query = mDatabase.child("clubInfo").orderByChild("clubName").equalTo(clubEditText.getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            boolean existClub = dataSnapshot.exists();
                            if (existClub) {
                                AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                                alt_bld.setMessage("동호회 가입 요청을 하시겠습니까?").setCancelable(false)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mDatabase.child("clubRequest").child(clubEditText.getText().toString()).child(DataManager.getInstance().getUid()).child("uid").setValue(DataManager.getInstance().getUid());
                                                dialog.cancel();
                                            }
                                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = alt_bld.create();

                                alert.setTitle("동호회 요청");

                                alert.show();

                            } else {
                                AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                                alt_bld.setMessage("존재하지 않는 동호회입니다.").setCancelable(false)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = alt_bld.create();

                                alert.setTitle("동호회 요청");

                                alert.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                    alt_bld.setMessage("이미 가입한 동호회입니다.").setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();

                    alert.setTitle("동호회 신청");

                    alert.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.club_func_menu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.clubMake:
                Intent clubMakeIntent = new Intent(getApplicationContext(), MakeClub.class);
                startActivity(clubMakeIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getClubInfo(List<String> clubNameList) {
        mClubAdapter.clear();
        mClubInfoList.clear();
        mClubList.clear();
        for (String clubName : clubNameList) {
            Query query = mDatabase.child("clubInfo").child(clubName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);
                    if (clubInfo != null) {
                        mClubInfoList.add(clubInfo);
                        mClubList.add(clubInfo.getClubName());
                        mClubAdapter.notifyDataSetChanged();
                    }
                    clubListView.setAdapter(mClubAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}