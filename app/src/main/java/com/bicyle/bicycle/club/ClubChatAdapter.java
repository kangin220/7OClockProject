package com.bicyle.bicycle.club;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bicyle.bicycle.R;
import com.bicyle.bicycle.util.UserChatData;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ClubChatAdapter extends ArrayAdapter<ClubChatDTO> {
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());
    private final static int TYPE_MY_SELF = 0;
    private final static int TYPE_ANOTHER = 1;
    private String myNickname;

    public ClubChatAdapter(Context context, int resource, String myNick) {
        super(context, resource);
        myNickname = myNick;
    }

    private View setAnotherView(LayoutInflater inflater) {
        View convertView = inflater.inflate(R.layout.listitem_chat, null);
        ViewHolderAnother holder = new ViewHolderAnother();
        holder.bindView(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    private View setMySelfView(LayoutInflater inflater) {
        View convertView = inflater.inflate(R.layout.listitem_chat_my, null);
        ViewHolderMySelf holder = new ViewHolderMySelf();
        holder.bindView(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            if (viewType == TYPE_ANOTHER) {
                convertView = setAnotherView(inflater);
            } else {
                convertView = setMySelfView(inflater);
            }
        }

        if (convertView.getTag() instanceof ViewHolderAnother) {
            if (viewType != TYPE_ANOTHER) {
                convertView = setAnotherView(inflater);
            }
            ((ViewHolderAnother) convertView.getTag()).setData(position);
        } else {
            if (viewType != TYPE_MY_SELF) {
                convertView = setMySelfView(inflater);
            }
            ((ViewHolderMySelf) convertView.getTag()).setData(position);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String nickname = getItem(position).getUserName();
        if (!TextUtils.isEmpty(myNickname) && myNickname.equals(nickname)) {
            return TYPE_MY_SELF; // 나의 채팅내용
        } else {
            return TYPE_ANOTHER; // 상대방의 채팅내용
        }
    }

    private class ViewHolderAnother {
        private TextView mTxtUserName;
        private TextView mTxtMessage;
        private TextView mTxtTime;

        private void bindView(View convertView) {
            mTxtUserName = (TextView) convertView.findViewById(R.id.txt_userName);
            mTxtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            mTxtTime = (TextView) convertView.findViewById(R.id.txt_time);
        }

        private void setData(int position) {
            ClubChatDTO chatData = getItem(position);
            mTxtUserName.setText(chatData.getUserName());
            mTxtMessage.setText(chatData.getMessage());
//            mTxtTime.setText(mSimpleDateFormat.format(chatData.getTime()));
            mTxtTime.setText(""+chatData.getTime());
        }
    }

    private class ViewHolderMySelf {
        private TextView mTxtMessage;
        private TextView mTxtTime;

        private void bindView(View convertView) {
            mTxtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            mTxtTime = (TextView) convertView.findViewById(R.id.txt_time);
        }

        private void setData(int position) {
            ClubChatDTO chatData = getItem(position);
            mTxtMessage.setText(chatData.getMessage());
//            mTxtTime.setText(mSimpleDateFormat.format(chatData.getTime()));
            mTxtTime.setText((chatData.getTime()));
        }
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.listitem_chat, null);
//
//            viewHolder = new ViewHolder();
//            viewHolder.mTxtUserName = (TextView) convertView.findViewById(R.id.txt_userName);
//            viewHolder.mTxtMessage = (TextView) convertView.findViewById(R.id.txt_message);
//            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.txt_time);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        UserChatData chatData = getItem(position);
//        viewHolder.mTxtUserName.setText(chatData.chatUserName);
//        viewHolder.mTxtMessage.setText(chatData.chatMessage);
//        viewHolder.mTxtTime.setText(mSimpleDateFormat.format(chatData.chatTime));
//
//        return convertView;
//    }

//    private class ViewHolder {
//        private TextView mTxtUserName;
//        private TextView mTxtMessage;
//        private TextView mTxtTime;
//    }
}
