package com.bicyle.bicycle.Map;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bicyle.bicycle.ProfDataSet;
import com.bicyle.bicycle.R;

import java.util.ArrayList;

public class ShareFriendAdapter extends BaseAdapter {

    Context mContext;
    int layout;
    ArrayList<ProfDataSet> friendList;
    ArrayList<String> share_friend ;
    LayoutInflater inflater;



    public ShareFriendAdapter(Context context, int layout, ArrayList<ProfDataSet> friendList,ArrayList<String> share_friend) //layout int는 id값
    {
        mContext=context;
        this.layout=layout;
        this.friendList=friendList;
        this.share_friend =share_friend;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //친구list와, 현재표시중인 친구list를 받아옴
        TextView map_friend_nickname;
        if(convertView==null)
        {
            convertView=inflater.inflate(layout,null);

        }


        map_friend_nickname=convertView.findViewById(R.id.map_friend_nickname);

        ProfDataSet dto=friendList.get(position);

        map_friend_nickname.setText(dto.getNickname());
        for(int i=0; i<share_friend.size(); i++)
        {
            if(share_friend.get(i).trim().equals(dto.getUid()))
            map_friend_nickname.setTextColor(Color.GRAY);
        }

        return convertView;
    }
}
