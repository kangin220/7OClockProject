package com.bicyle.bicycle.Map;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bicyle.bicycle.ClubInfo;
import com.bicyle.bicycle.ProfDataSet;
import com.bicyle.bicycle.R;

import java.util.ArrayList;
import java.util.List;

public class ShareClubAdapter extends BaseAdapter {

    Context mContext;
    int layout;
    LayoutInflater inflater;
    ArrayList<String> inAdapterClubInfoList;
    ArrayList<ShareClubDTO> getShareClub;
    public ShareClubAdapter(Context mContext, int layout, ArrayList<String> inAdapterClubInfoList, ArrayList<ShareClubDTO> getShareClub) {
        this.mContext = mContext;
        this.layout = layout;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inAdapterClubInfoList = inAdapterClubInfoList;
        this.getShareClub= getShareClub;
    }

    @Override
    public int getCount() {
        return inAdapterClubInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return inAdapterClubInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView map_club_name;
        if(convertView==null)
        {
            convertView=inflater.inflate(layout,null);

        }


        map_club_name=convertView.findViewById(R.id.map_club_name);

        String dto=inAdapterClubInfoList.get(position);

        map_club_name.setText(dto);

        for(int i=0; i<getShareClub.size(); i++)
        {
            if(getShareClub.get(i).getClubName().equals(dto))
            {
                map_club_name.setTextColor(Color.GRAY);
            }
        }
        return convertView;
    }
}
