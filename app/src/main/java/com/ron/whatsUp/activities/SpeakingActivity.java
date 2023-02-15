package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.adapters.SpeackingAdapter;
import com.ron.whatsUp.callbacks.Callback_chats_to_messages;
import com.ron.whatsUp.callbacks.Callback_message;
import com.ron.whatsUp.callbacks.Callback_user_status;
import com.ron.whatsUp.dialogs.ImageDialog;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyTime;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserChat;
import com.ron.whatsUp.objects.UserStatus;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.Comparator;
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
    private UserStatus userStatus;
    private RecyclerView speaking_LST;
    private SpeackingAdapter speackingAdapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private boolean is_chat_saved;
    private boolean is_typing;
    private boolean is_already_loaded;

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
        HashMap<String, String> my_contacts = DataManager.getDataManager().get_my_users();
        if (my_contacts == null)
            my_contacts = new HashMap<>();
        String contact_name = my_contacts.get(chat.getOther_user().getPhone());
        if (contact_name != null && !contact_name.isEmpty()) {
            speaking_LBL_title.setText(contact_name);
        } else {
            speaking_LBL_title.setText(chat.getOther_user().getPhone());
        }
        MyDB.getInstance().setCallback_user_status(callback_user_status);
        MyDB.getInstance().start_listen_user_status(chat.getOther_user().getPhone());
    }

    private void exists_chat_state() {
        MyDB.getInstance().start_listen_all_messages(chat);

    }

    @Override
    protected void onStart() {
        super.onStart();
        UserStatus userStatus = new UserStatus().setConnected(true).setImg(myUser.getImg_uri()).setLast_seen(new MyTime().update_my_time_by_calender(Calendar.getInstance())).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        MyDB.getInstance().update_user_status(userStatus);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (is_chat_saved && chat.getCurrent_user().isTyping()) {
            chat.getCurrent_user().setTyping(false);
            is_typing = false;
            ChatDB chatDB = new ChatDB(chat);
            MyDB.getInstance().update_other_user_chat(chatDB, chat.getOther_user().getPhone());
        }
    }

    private void new_chat_state() { //TODO: start listen messages?
        speackingAdapter = new SpeackingAdapter(this, messages, myUser.getPhone());
        speaking_LST.setAdapter(speackingAdapter);
        // TODO: 10/02/2023 add pick
        //speaking_IMG_user.setImageDrawable();

        //speaking_LBL_subtitle.setText(Data);
    }

    private void init_actions() {
        speaking_IMG_backPressed.setOnClickListener(view -> onBackPressed());
        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (!isOpen) {
                        if (is_typing == false) {
                            return;
                        }
                        if (is_chat_saved) {
                            chat.getCurrent_user().setTyping(false);
                            is_typing = false;
                            ChatDB chatDB = new ChatDB(chat);
                            MyDB.getInstance().update_other_user_chat(chatDB, chat.getOther_user().getPhone());
                        }
                    }
                });
        speaking_TIETL_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (is_typing) {
                    return;
                }
                if (is_chat_saved) {
                    chat.getCurrent_user().setTyping(true);
                    is_typing = true;
                    ChatDB chatDB = new ChatDB(chat);
                    MyDB.getInstance().update_other_user_chat(chatDB, chat.getOther_user().getPhone());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        speaking_IMG_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
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
                    chat.getCurrent_user().setTyping(false);
                    is_typing = false;
                    MyDB.getInstance().save_message(chat.getChat_id(), msg);
                    MyDB.getInstance().create_new_chat(chat_new);
                    is_already_loaded = true;
                    MyDB.getInstance().start_listen_all_messages(chat);
                    DataManager.getDataManager().setCallback_chats_to_messages(callback_chats_to_messages);
                    return;

                }
                msg.setReceiver(chat.getOther_user().getPhone());
                messages.add(msg);
                messages.sort(msg_comparator);
                int unread = 0;
                for (int i = messages.size() - 1; i >= 0; i--) {
                    if (!messages.get(i).isMsg_seen() && chat.getCurrent_user().getPhone().equals(messages.get(i).getSender())) {
                        unread++;
                    } else {
                        break;
                    }
                }
                chat.getOther_user().setUnread(unread);
                chat.getCurrent_user().setTyping(false);
                chat.setLast_msg(msg);
                is_typing = false;
                ChatDB chat_new = new ChatDB(chat);

                speackingAdapter.notifyDataSetChanged();
                MyDB.getInstance().create_new_chat(chat_new);
                MyDB.getInstance().save_message(chat.getChat_id(), msg);


            }
        });
        speaking_IMG_user.setOnClickListener(view -> {
            if (this.userStatus != null) {new ImageDialog().show(this, this.userStatus.getPhone(), this.userStatus.getImg());}
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
        DataManager.getDataManager().setCallback_chats_to_messages(null);
        MyDB.getInstance().setCallback_user_status(null);
        MyDB.getInstance().remove_listen_all_messages(chat);
        MyDB.getInstance().remove_user_status_event(chat.getOther_user().getPhone());
        if (is_chat_saved && chat.getCurrent_user().isTyping()) {
            chat.getCurrent_user().setTyping(false);
            is_typing = false;
            ChatDB chatDB = new ChatDB(chat);
            MyDB.getInstance().update_other_user_chat(chatDB, chat.getOther_user().getPhone());
        }

        finish();
    }

    private Callback_message callback_save_message = new Callback_message() {

        @Override
        public void save_msg(Message msg) {
        }

        @Override
        public synchronized void get_all_msgs(HashMap<String, Message> stringMessageHashMap) {
            if (is_already_loaded) {
                make_view_msg_update(stringMessageHashMap);
            } else {
                make_view_msg_first(stringMessageHashMap);
                DataManager.getDataManager().setCallback_chats_to_messages(callback_chats_to_messages);
                is_already_loaded = true;
            }

        }

        @Override
        public void error() {
            Toast.makeText(SpeakingActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void save_chat(ChatDB chat_new) {
            DataManager.getDataManager().update_net_chat_created(chat);
            is_chat_saved = true;
        }
    };

    private void make_view_msg_update(HashMap<String, Message> stringMessageHashMap) {
        if (stringMessageHashMap == null || stringMessageHashMap.isEmpty())
            return;
        ArrayList<Message> msg_tmp = new ArrayList<>(stringMessageHashMap.values());
        msg_tmp.sort(msg_comparator);

        if (msg_tmp.get(msg_tmp.size() - 1).getSender().equals(chat.getCurrent_user().getPhone())) {
            if (!msg_tmp.get(msg_tmp.size() - 1).isMsg_seen()) {
            } else {
                msg_tmp.forEach(
                        msg -> {
                            msg.setMsg_seen(true);
                        }
                );
                messages.clear();
                messages.addAll(msg_tmp);
                runOnUiThread(() -> speackingAdapter.notifyDataSetChanged());

            }
            return;
        }
        if (msg_tmp.get(msg_tmp.size() - 1).isMsg_seen()) {
            return;
        }
        msg_tmp.forEach(
                msg -> {
                    msg.setMsg_seen(true);
                }
        );
        messages.clear();
        messages.addAll(msg_tmp);
        runOnUiThread(() -> speackingAdapter.notifyDataSetChanged());

        stringMessageHashMap.clear();
        for (int i = 0; i < messages.size(); i++) {
            stringMessageHashMap.put(messages.get(i).getMessage_id(), messages.get(i));
        }

        chat = set_get_chat(null);
        chat.getCurrent_user().setUnread(0);
        chat.getLast_msg().setMsg_seen(true);
        ChatDB chat_new = new ChatDB(chat);
        MyDB.getInstance().create_new_chat(chat_new);
        MyDB.getInstance().update_chat_messages(chat, stringMessageHashMap);


    }

    private synchronized Chat set_get_chat(Chat chat) {
        if (chat != null)
            this.chat = chat;
        return this.chat;
    }

    private void make_view_msg_first(HashMap<String, Message> stringMessageHashMap) {
        messages = new ArrayList<>(stringMessageHashMap.values());
        messages.sort(msg_comparator);
        if (!messages.isEmpty())
            chat.setLast_msg(messages.get(messages.size() - 1));
        boolean is_seen_updated = false;
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (!messages.get(i).isMsg_seen() && chat.getOther_user().getPhone().equals(messages.get(i).getSender())) {
                messages.get(i).setMsg_seen(true);
                is_seen_updated = true;
            } else {
                break;
            }
        }
        if (!myUser.getImg_uri().isEmpty() && !chat.getCurrent_user().getImg().equals(myUser.getImg_uri())) {
            is_seen_updated = true;
        }
        if (is_seen_updated) {
            chat.getCurrent_user().setUnread(0);
            chat.getCurrent_user().setImg(myUser.getImg_uri());
            MyDB.getInstance().update_chat_messages(chat, stringMessageHashMap);
            ChatDB chat_new = new ChatDB(chat);
            MyDB.getInstance().create_new_chat(chat_new);
        }
        speackingAdapter = new SpeackingAdapter(SpeakingActivity.this, messages, myUser.getPhone());
        speaking_LST.setAdapter(speackingAdapter);
        is_chat_saved = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyDB.getInstance().setCallback_user_status(null);
        MyDB.getInstance().remove_listen_all_messages(chat);
        DataManager.getDataManager().setCallback_chats_to_messages(null);
        MyDB.getInstance().remove_user_status_event(chat.getOther_user().getPhone());
        chat.getCurrent_user().setUnread(0);
        ChatDB chat_new = new ChatDB(chat);
        MyDB.getInstance().create_new_chat(chat_new);
    }

    public void update_sub_title(UserStatus userStatus) {
        this.userStatus = userStatus;
        if (userStatus == null) {
            return;
        }
        if (chat.getOther_user().isTyping()) {
            speaking_LBL_subtitle.setText(R.string.typing_TAG);
            return;
        }
        if (userStatus.isConnected()) {
            speaking_LBL_subtitle.setText(R.string.connected_TAG);
            return;
        }
        speaking_LBL_subtitle.setText(userStatus.get_last_msg_calender_output());
    }

    public void update_sub_title() {
        if (this.userStatus == null) {
            return;
        }
        if (chat.getOther_user().isTyping()) {
            speaking_LBL_subtitle.setText(R.string.typing_TAG);
            return;
        }
        if (this.userStatus.isConnected()) {
            speaking_LBL_subtitle.setText(R.string.connected_TAG);
            return;
        }
        speaking_LBL_subtitle.setText(userStatus.get_last_msg_calender_output());
    }

    private Callback_user_status callback_user_status = new Callback_user_status() {
        @Override
        public void user_status_updated(UserStatus userStatus) {

            update_sub_title(userStatus);
            Log.d("MyLog",userStatus.toString());
            if (userStatus != null && userStatus.getImg() != null && !userStatus.getImg().isEmpty()) {
                Log.d("MyLog","userStatus.toString())");
                Glide.with(SpeakingActivity.this).load(userStatus.getImg()).placeholder(R.drawable.ic_user).into(speaking_IMG_user);
            }
        }

        @Override
        public void error() {
            update_sub_title(null);
        }
    };
    private Callback_chats_to_messages callback_chats_to_messages = new Callback_chats_to_messages() {

        @Override
        public void chat_may_updated(Chat chat_new) {
            if (chat_new == null)
                return;
            set_get_chat(chat_new);
            update_sub_title();

        }
    };
    private Comparator<Message> msg_comparator = new Comparator<Message>() {
        @Override
        public int compare(Message message, Message t1) {
            if (message.get_msg_calender().before(t1.get_msg_calender())) {
                return -1;
            }
            return 1;
        }
    };
}