package com.ron.whatsUp.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.ron.whatsUp.R;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.tools.DataManager;

import java.util.ArrayList;

public class SpeackingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String my_user_color = "#E7FFDB";
    private final String other_user_color = "#FFFFFF";
    private Activity activity;
    private String current_user_phone;
    private ArrayList<Message> messages = new ArrayList<>();

    public SpeackingAdapter(Activity activity, ArrayList<Message> messages, String current_user) {
        this.activity = activity;
        this.messages = messages;
        this.current_user_phone = current_user;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_speaking, parent, false);
        MessagesHolder messagesHolder = new MessagesHolder(view);
        messagesHolder.setIsRecyclable(false);
        return messagesHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MessagesHolder holder = (MessagesHolder) viewHolder;
        Message meassage = getItem(position);

        boolean is_current_user_send = meassage.getSender().equals(current_user_phone);

        if (meassage.getSender().equals(current_user_phone)) {
            holder.listSpeaking_PAR.setGravity(Gravity.START);
            holder.listSpeaking_MTV.setCardBackgroundColor(Color.parseColor(my_user_color));
        } else {
            holder.listSpeaking_PAR.setGravity(Gravity.END);
            holder.listSpeaking_MTV.setCardBackgroundColor(Color.parseColor(other_user_color));

        }
        holder.listSpeaking_LBL_msg.setText(meassage.getContent());
        holder.listSpeaking_LBL_calender.setText(meassage.get_hour_minutes_str());
        int res;
        if(meassage.isMsg_seen() && is_current_user_send){
            res = DataManager.READ_BLUE;
        }
        else{
            res = DataManager.READ_REG;
        }
        holder.listSpeaking_IC_vi1.setImageResource(res);
        holder.listSpeaking_IC_vi2.setImageResource(res);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public Message getItem(int position) {
        return messages.get(position);
    }


    class MessagesHolder extends RecyclerView.ViewHolder {
        private MaterialCardView listSpeaking_MTV;
        private LinearLayout listSpeaking_PAR;
        private MaterialTextView listSpeaking_LBL_msg;
        private MaterialTextView listSpeaking_LBL_calender;
        private ImageView listSpeaking_IC_vi1;
        private ImageView listSpeaking_IC_vi2;


        public MessagesHolder(View itemView) {
            super(itemView);
            listSpeaking_PAR = itemView.findViewById(R.id.listSpeaking_PAR);
            listSpeaking_MTV = itemView.findViewById(R.id.listSpeaking_MTV);
            listSpeaking_LBL_msg = itemView.findViewById(R.id.listSpeaking_LBL_msg);
            listSpeaking_LBL_calender = itemView.findViewById(R.id.listSpeaking_LBL_calender);
            listSpeaking_IC_vi1 = itemView.findViewById(R.id.listSpeaking_IC_vi1);
            listSpeaking_IC_vi2 = itemView.findViewById(R.id.listSpeaking_IC_vi2);
        }
    }
}