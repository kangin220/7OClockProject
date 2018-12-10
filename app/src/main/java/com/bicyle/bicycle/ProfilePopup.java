package com.bicyle.bicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ProfilePopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_popup);

        TextView nickText = findViewById(R.id.popNickname);
        TextView ageText = findViewById(R.id.popAge);
        TextView locText = findViewById(R.id.popLocation);
        TextView genText = findViewById(R.id.popGender);
        Button posBtn = findViewById(R.id.reqPosBtn);
        Button negBtn = findViewById(R.id.reqNegBtn);
        Button confBtn = findViewById(R.id.confirmBtn);

        final ProfDataSet prof = (ProfDataSet) getIntent().getSerializableExtra("reqProf");
        String preAct = getIntent().getStringExtra("preAct");

        if(preAct.equals("request")) {
            posBtn.setVisibility(View.VISIBLE);
            negBtn.setVisibility(View.VISIBLE);
            confBtn.setVisibility(View.GONE);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("prof", prof);
                    setResult(23, intent);
                    finish();
                }
            });

            negBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("prof", prof);
                    setResult(24, intent);
                    finish();
                }
            });
        }
        else if(preAct.equals("detail")) {
            posBtn.setVisibility(View.GONE);
            negBtn.setVisibility(View.GONE);
            confBtn.setVisibility(View.VISIBLE);
            confBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        nickText.setText(prof.getNickname());
        ageText.setText(prof.getAge());
        locText.setText(prof.getLocation());
        genText.setText(prof.getGender());

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}