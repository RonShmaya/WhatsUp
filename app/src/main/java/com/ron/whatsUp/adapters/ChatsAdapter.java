package com.ron.whatsUp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.ron.whatsUp.R;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.tools.DataManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface ChatListener {
        void clicked(Chat chat, int position);
    }

    //TODO save userchat
    private Activity activity;
    private ChatListener chatListener;

    private ArrayList<Chat> chats = new ArrayList<>();

    public ChatsAdapter(Activity activity, ArrayList<Chat> chats) {
        this.activity = activity;
        this.chats = chats;
    }

    public ChatsAdapter setChatListener(ChatListener chatListener) {
        this.chatListener = chatListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chats, parent, false);
        ChatsHolder chatHolder = new ChatsHolder(view);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ChatsHolder holder = (ChatsHolder) viewHolder;
        Chat chat = getItem(position);

        if (chat.getOther_user().getContact_name().isEmpty()) {
            holder.listChats_LBL_user_name.setText(chat.getOther_user().getName());

        }
        else{
            holder.listChats_LBL_user_name.setText(chat.getOther_user().getContact_name());
        }
        int res;
        if (chat.is_last_msg_read() && chat.getCurrent_user().getPhone().equals(chat.getLast_msg().getSender())) {
            res = DataManager.READ_BLUE;
        } else {
            res = DataManager.READ_REG;
        }
        holder.listChats_IC_vi1.setImageResource(res);
        holder.listChats_IC_vi2.setImageResource(res);

        if (chat.is_typing()) {
            holder.listChats_LBL_last_msg.setText(R.string.typing_TAG);
        } else {
            holder.listChats_LBL_last_msg.setText(chat.get_last_msg());
        }

        holder.listChats_LBL_calender.setText(chat.get_last_msg_calender_output());
        int unread = chat.unread_messages();
        if (unread == 0) {
            holder.listChats_LBL_not_read.setVisibility(View.INVISIBLE);
        } else {
            holder.listChats_LBL_not_read.setVisibility(View.VISIBLE);
            holder.listChats_LBL_not_read.setText("" + unread);
        }
        //listChats_IMG_photo
//        Glide.with(activity).load(bar.getBar_photo()).placeholder(R.drawable.img_placeholder).into(holder.listChats_IMG_photo);

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public Chat getItem(int position) {
        return chats.get(position);
    }


    class ChatsHolder extends RecyclerView.ViewHolder {

        private CircleImageView listChats_IMG_photo;
        private MaterialTextView listChats_LBL_user_name;
        private ImageView listChats_IC_vi1;
        private ImageView listChats_IC_vi2;
        private MaterialTextView listChats_LBL_last_msg;
        private MaterialTextView listChats_LBL_calender;
        private MaterialTextView listChats_LBL_not_read;

        public ChatsHolder(View itemView) {
            super(itemView);
            listChats_IMG_photo = itemView.findViewById(R.id.listChats_IMG_photo);
            listChats_LBL_user_name = itemView.findViewById(R.id.listChats_LBL_user_name);
            listChats_IC_vi1 = itemView.findViewById(R.id.listChats_IC_vi1);
            listChats_IC_vi2 = itemView.findViewById(R.id.listChats_IC_vi2);
            listChats_LBL_last_msg = itemView.findViewById(R.id.listChats_LBL_last_msg);
            listChats_LBL_calender = itemView.findViewById(R.id.listChats_LBL_calender);
            listChats_LBL_not_read = itemView.findViewById(R.id.listChats_LBL_not_read);

            itemView.setOnClickListener(view -> {
                if (chatListener != null) {
                    chatListener.clicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}