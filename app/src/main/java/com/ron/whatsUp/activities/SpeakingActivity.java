package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.ron.whatsUp.R;
import com.ron.whatsUp.adapters.SpeackingAdapter;
import com.ron.whatsUp.callbacks.Callback_message;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyTime;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserChat;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpeakingActivity extends AppCompatActivity {
    private MyUser myUser;
    private Chat chat;
    private CircleImageView speaking_IMG_backPressed;
    private CircleImageView speaking_IMG_user;
    private MaterialTextView speaking_LBL_title;
    private MaterialTextView speaking_LBL_subtitle;
    private CircleImageView speaking_IMG_smiley;
    private TextInputEditText speaking_TIETL_msg;
    private CircleImageView speaking_IMG_send;
    private RecyclerView speaking_LST;
    private SpeackingAdapter speackingAdapter;
    private ArrayList<Message> messages = new ArrayList<>();

    // TODO: 10/02/2023 Verify we listen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);
        myUser = DataManager.getDataManager().get_account();
        chat = DataManager.getDataManager().getCurrent_chat();
        MyDB.getInstance().setCallback_save_message(callback_save_message);
        findViews();
        if (chat.isIs_new()) {
            new_chat_state();

        } else {
            exists_chat_state();
        }
        init_views_data();
        init_actions();
        speaking_LBL_title.setText(DataManager.getDataManager().getCurrent_chat().getOther_user().getContact_name());
    }

    private void exists_chat_state() {
        MyDB.getInstance().getAllMessages(chat);

    }

    private void new_chat_state() {
        speackingAdapter = new SpeackingAdapter(this, messages, myUser.getPhone());
        speaking_LST.setAdapter(speackingAdapter);
        // TODO: 10/02/2023 add pick
        //speaking_IMG_user.setImageDrawable();

        //speaking_LBL_subtitle.setText(Data);
    }

    private void init_actions() {
        speaking_IMG_backPressed.setOnClickListener(view -> onBackPressed());
        speaking_IMG_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                // TODO: 10/02/2023 make sycronized?
                if (speaking_TIETL_msg.getText().toString() == null || speaking_TIETL_msg.getText().toString().isEmpty())
                    return;
                if (myUser.getPhone().equals(chat.getOther_user().getPhone()))
                    return;

                Message msg = new Message()
                        .setMsg_seen(false)
                        .setContent(speaking_TIETL_msg.getText().toString())
                        .setMyTime(new MyTime().update_my_time_by_calender(Calendar.getInstance()))
                        .setMessage_id(UUID.randomUUID().toString())
                        //.setReceiver(chat.getOther_user().getPhone())
                        .setSender(myUser.getPhone());
                speaking_TIETL_msg.setText("");

                if (chat.isIs_new()) {
                    chat.setIs_new(false);
                    MyUser other_user = DataManager.getDataManager().getOther_user();
                    HashMap<String, String> msgs_id = new HashMap<>();
                    msgs_id.put(msg.getMessage_id(), msg.getMessage_id());
                    msg.setReceiver(other_user.getPhone());
                    ChatDB chat_new = new ChatDB()
                            .setLast_msg(msg)
                            .setUser1(myUser.getUserChat())
                            .setUser2(other_user.getUserChat().setUnread(1))
                            .setMessages_id(msgs_id)
                            .setChat_id(Chat.make_chat_id(other_user.getPhone(), myUser.getPhone()));
                    chat = new Chat(chat_new, myUser.getPhone());
                    messages.add(msg);
                    speackingAdapter.notifyDataSetChanged();
                    MyDB.getInstance().save_message(chat.getChat_id(), msg);
                    MyDB.getInstance().create_new_chat(chat_new);
                    return;

                }
                msg.setReceiver(chat.getOther_user().getPhone());
                // TODO: 10/02/2023 listen to changes -> can be updated by the chats activity ,
                //  for typing, last seen, and in case the messages len grow -> go take new msg
                chat.setLast_msg(msg)
                        .add_msg(msg.getMessage_id());
                chat.getOther_user().setUnread(chat.getOther_user().getUnread() + 1);
                ChatDB chat_new = new ChatDB(chat);
                messages.add(msg);
                speackingAdapter.notifyDataSetChanged();
                MyDB.getInstance().save_message(chat.getChat_id(), msg);
                MyDB.getInstance().create_new_chat(chat_new);



            }
        });
    }

    private void init_views_data() {
    }

    private void findViews() {
        speaking_LST = findViewById(R.id.speaking_LST);
        speaking_IMG_backPressed = findViewById(R.id.speaking_IMG_backPressed);
        speaking_IMG_user = findViewById(R.id.speaking_IMG_user);
        speaking_LBL_title = findViewById(R.id.speaking_LBL_title);
        speaking_LBL_subtitle = findViewById(R.id.speaking_LBL_subtitle);
        speaking_IMG_smiley = findViewById(R.id.speaking_IMG_smiley);
        speaking_TIETL_msg = findViewById(R.id.speaking_TIETL_msg);
        speaking_IMG_send = findViewById(R.id.speaking_IMG_send);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private Callback_message callback_save_message = new Callback_message() {

        @Override
        public void save_msg(Message msg) {
        }

        @Override
        public void get_all_msgs(HashMap<String, Message> stringMessageHashMap) {
            messages = new ArrayList<>(stringMessageHashMap.values());
            chat.setLast_msg(messages.get(messages.size()-1));
            chat.getCurrent_user().setUnread(0);
            speackingAdapter = new SpeackingAdapter(SpeakingActivity.this, messages, myUser.getPhone());
            speaking_LST.setAdapter(speackingAdapter);
        }

        @Override
        public void error() {
            Toast.makeText(SpeakingActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void save_chat(ChatDB chat_new) {
            DataManager.getDataManager().update_net_chat_created(chat);
        }
    };

}