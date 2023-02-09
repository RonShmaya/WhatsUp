package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;
import com.ron.whatsUp.tools.Permissions;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatsActivity extends AppCompatActivity {
    private MyUser myUser;
    private MaterialToolbar toolbar;
    private RecyclerView chats_LST;
    private ChatsAdapter chatsAdapter;
    private HashMap<String, Chat> chats = new HashMap<>();
    private ArrayList<Chat> my_chats_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myUser = DataManager.getDataManager().get_account();
        MyServices.getInstance().update_app_lang(myUser.getLang(), this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        init_tool_bar();
        findViews();
        Permissions.initHelper(this);
        if (!Permissions.getPermissions().all_permissions_ok()) {
            Permissions.getPermissions().setCallback_permissions(callback_permissions);
            Permissions.getPermissions().requestCAMERA();
            return;
        }

        MyDB.getInstance().setCallBack_get_all_chats(callBack_get_all_chats);
        MyDB.getInstance().getAllChats(myUser);


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
            go_next(SearchActivity.class);
        } else if (id == R.id.action_setting) {
            go_next(SettingActivity.class);
        } else if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        MyDB.getInstance().clear_callbacks();
        MyDB.getInstance().remove_event(myUser, chats);
        DataManager.getDataManager().set_account(null);
        go_next(EnterAppActivity.class);
        finish();
    }

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }

    private Callback_user_data_changed callback_user_data_changed = new Callback_user_data_changed() {
        @Override
        public void chat_change(ChatDB chatDB) {
            Chat chat = new Chat(chatDB, myUser.getPhone());
            chats.put(chat.getChat_id(), chat);

            my_chats_list.remove(chat);
            my_chats_list.add(chat);

            runOnUiThread(() -> chatsAdapter.notifyDataSetChanged());

        }
    };
    private ChatsAdapter.ChatListener chatListener = new ChatsAdapter.ChatListener() {
        @Override
        public void clicked(Chat chat, int position) {
            DataManager.getDataManager().setCurrent_chat(chat);
            go_next(SpeakingActivity.class);
        }
    };

    private CallBack_get_all_chats callBack_get_all_chats = new CallBack_get_all_chats() {
        @Override
        public void all_chats(HashMap<String, ChatDB> chatDBHashMap) {
            MyDB.getInstance().setCallback_user_data_changed(callback_user_data_changed);
            MyDB.getInstance().start_listen_chats_changes(myUser, chatDBHashMap);
            chatDBHashMap.forEach(
                    (id, chatDB) -> {
                        chats.put(id, new Chat(chatDB, myUser.getPhone()));
                    }
            );
            my_chats_list = new ArrayList<>(chats.values());
            chatsAdapter = new ChatsAdapter(ChatsActivity.this, new ArrayList<>(my_chats_list));
            chatsAdapter.setChatListener(chatListener);
            chats_LST.setAdapter(chatsAdapter);
        }

        @Override
        public void error() {
            Toast.makeText(ChatsActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }
    };

    private Callback_permissions callback_permissions = new Callback_permissions() {
        @Override
        public void all_per_ok() {
            MyDB.getInstance().setCallBack_get_all_chats(callBack_get_all_chats);
            MyDB.getInstance().getAllChats(myUser);

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
            Chat cht = chats.get(chat_new.getChat_id());
            if (cht == null) {
                MyDB.getInstance().add_one_listen_chats_changes(myUser, chat_new);
                chats.put(chat_new.getChat_id(), (Chat) chat_new);
                my_chats_list.add((Chat) chat_new);
            }

            chatsAdapter.notifyDataSetChanged();
        }
    };
}