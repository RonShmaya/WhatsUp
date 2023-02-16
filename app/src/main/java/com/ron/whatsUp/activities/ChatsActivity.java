package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.adapters.ChatsAdapter;
import com.ron.whatsUp.callbacks.CallBack_get_all_chats;
import com.ron.whatsUp.callbacks.Callback_message;
import com.ron.whatsUp.callbacks.Callback_permissions;
import com.ron.whatsUp.callbacks.Callback_user_data_changed;
import com.ron.whatsUp.dialogs.ImageDialog;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyTime;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserStatus;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;
import com.ron.whatsUp.tools.Permissions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ChatsActivity extends AppCompatActivity {
    private MyUser myUser;
    private HashMap<String, String> my_contacts = new HashMap<>();
    private MaterialToolbar toolbar;
    private RecyclerView chats_LST;
    private ChatsAdapter chatsAdapter;
    private HashMap<String, Chat> chats = new HashMap<>();
    private ArrayList<Chat> my_chats_list = new ArrayList<>();
    private boolean is_listen;
    private boolean is_need_to_stop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myUser = DataManager.getDataManager().get_account();
        MyServices.getInstance().update_app_lang(myUser.getLang(), this);
        super.onCreate(savedInstanceState);
        MyServices.getInstance().setService_app_compact(this);
        setContentView(R.layout.activity_chats);
        DataManager.getDataManager().setMain_activity(this);
        init_tool_bar();
        findViews();
        DataManager.getDataManager().setCallback_message(callback_save_message);
        Permissions.initHelper(this);
        if (!Permissions.getPermissions().all_permissions_ok()) {
            Permissions.getPermissions().setCallback_permissions(callback_permissions);
            Permissions.getPermissions().requestCAMERA();
            return;
        }
        Intent intent = new Intent(this, WhatsAppService.class);
        intent.setAction(WhatsAppService.START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
       // MyDB.getInstance().setCallBack_get_all_chats(callBack_get_all_chats);
       // MyDB.getInstance().getAllChats(myUser);
        MyDB.getInstance().setCallback_user_data_changed(callback_user_data_changed);
        MyDB.getInstance().start_listen_chats_changes(myUser, null);


    }

    private void findViews() {
        chats_LST = findViewById(R.id.chats_LST);
    }

    private void init_tool_bar() {
        toolbar = findViewById(R.id.chats_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chats_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            DataManager.getDataManager().set_all_chats(chats);
            go_next(SearchActivity.class);
        } else if (id == R.id.action_setting) {
            go_next(SettingActivity.class);
        } else if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        UserStatus userStatus = new UserStatus().setConnected(false).setImg(myUser.getImg_uri()).setLast_seen(new MyTime().update_my_time_by_calender(Calendar.getInstance())).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        MyDB.getInstance().update_user_status(userStatus);
        Intent intent = new Intent(this, WhatsAppService.class);
        intent.setAction(WhatsAppService.STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        FirebaseAuth.getInstance().signOut();
        MyDB.getInstance().clear_callbacks();
        MyDB.getInstance().remove_event(myUser, chats);
        DataManager.getDataManager().reloaded();
        go_next(EnterAppActivity.class);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserStatus userStatus = new UserStatus().setConnected(true).setImg(myUser.getImg_uri()).setLast_seen(new MyTime().update_my_time_by_calender(Calendar.getInstance())).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        MyDB.getInstance().update_user_status(userStatus);
        is_need_to_stop=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null && is_need_to_stop){
            UserStatus userStatus = new UserStatus().setConnected(false).setImg(myUser.getImg_uri()).setLast_seen(new MyTime().update_my_time_by_calender(Calendar.getInstance())).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            MyDB.getInstance().update_user_status(userStatus);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyDB.getInstance().remove_event(myUser, chats);
        MyDB.getInstance().setCallback_user_data_changed(null);
    }

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
    private synchronized boolean set_get_listen(Boolean is_listen){
        if(is_listen != null)
            this.is_listen = is_listen;
        return this.is_listen;

    }
    private Callback_user_data_changed callback_user_data_changed = new Callback_user_data_changed() {
        @Override
        public  void chat_change(HashMap<String, ChatDB> chatDBHashMap) {
            if(set_get_listen(null)){
                make_chats_update(chatDBHashMap);
            }
            else{
                set_get_listen(true);
                start_chats(chatDBHashMap);
            }
        }
    };



    private ChatsAdapter.ChatListener chatListener = new ChatsAdapter.ChatListener() {
        @Override
        public void clicked(Chat chat, int position) {
            DataManager.getDataManager().setCurrent_chat(chat);
            is_need_to_stop = false;
            go_next(SpeakingActivity.class);
        }

        @Override
        public void img_clicked(Chat chat, int position) {
            new ImageDialog().show(ChatsActivity.this, chat.getOther_user().getPhone(),chat.getOther_user().getImg());
        }
    };

    private CallBack_get_all_chats callBack_get_all_chats = new CallBack_get_all_chats() {
        @Override
        public void all_chats(HashMap<String, ChatDB> chatDBHashMap) {
        }

        @Override
        public void error() {
            Toast.makeText(ChatsActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }
    };

    private Callback_permissions callback_permissions = new Callback_permissions() {
        @Override
        public void all_per_ok() {
            Intent intent = new Intent(ChatsActivity.this, WhatsAppService.class);
            intent.setAction(WhatsAppService.START_FOREGROUND_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            MyDB.getInstance().setCallback_user_data_changed(callback_user_data_changed);
            MyDB.getInstance().start_listen_chats_changes(myUser, null);

        }
    };
    private Callback_message callback_save_message = new Callback_message() {

        @Override
        public void save_msg(Message msg) {
        }

        @Override
        public void get_all_msgs(HashMap<String, Message> stringMessageHashMap) {
        }

        @Override
        public void error() {
        }

        @Override
        public void save_chat(ChatDB chat_new) {
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if(!my_chats_list.isEmpty())
            runOnUiThread(() -> chatsAdapter.notifyDataSetChanged());


    }
    public void start_chats(HashMap<String, ChatDB> chatDBHashMap){
        chatDBHashMap.forEach(
                (id, chatDB) -> {
                    chats.put(id, new Chat(chatDB, myUser.getPhone()));
                }
        );
        HashMap<String, String> tmp = DataManager.getDataManager().get_my_users();
        if (tmp == null) {
            my_contacts = MyServices.getInstance().get_all_contacts(ChatsActivity.this);
            DataManager.getDataManager().setMy_contacts(my_contacts);
        }
        my_chats_list.addAll(chats.values());
        my_chats_list.sort(chat_comparator);
        chatsAdapter = new ChatsAdapter(ChatsActivity.this, my_chats_list,my_contacts);
        chatsAdapter.setChatListener(chatListener);
        chats_LST.setAdapter(chatsAdapter);
        DataManager.getDataManager().set_all_chats(chats);
    }
    private void make_chats_update(HashMap<String, ChatDB> chatDBHashMap) {
        chatDBHashMap.forEach(
                (id, chatDB) -> {
                    chats.put(id, new Chat(chatDB, myUser.getPhone()));
                }
        );
        HashMap<String, String> tmp = DataManager.getDataManager().get_my_users();
        my_chats_list.clear();
        my_chats_list.addAll(chats.values());
        my_chats_list.sort(chat_comparator);
        DataManager.getDataManager().set_all_chats(chats);
        runOnUiThread(() -> chatsAdapter.notifyDataSetChanged());
        if(DataManager.getDataManager().getCallback_chats_to_messages() != null){
            DataManager.getDataManager().getCallback_chats_to_messages().chat_may_updated(chats.get(DataManager.getDataManager().getCurrent_chat().getChat_id()));
        }
    }
    private Comparator<Chat> chat_comparator = new Comparator<Chat>() {
        @Override
        public int compare(Chat ch, Chat t1) {
            if (ch.getLast_msg().get_msg_calender().after(t1.getLast_msg().get_msg_calender())) {
                return -1;
            }
            return 1;
        }
    };
}