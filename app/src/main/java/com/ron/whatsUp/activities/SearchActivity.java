package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.ron.whatsUp.R;

import com.ron.whatsUp.adapters.SearchingAdapter;
import com.ron.whatsUp.callbacks.Callback_find_account;

import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.MyContact;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserChat;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;


import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends AppCompatActivity {
    // TODO: 10/02/2023 get chats details from activity chats !!!
    private HashMap<String, String> my_contacts = new HashMap<>();
    private MyUser myUser;
    private ArrayList<MyContact> find_lst = new ArrayList<>();
    private ArrayList<MyContact> all_contacts = new ArrayList<>();
    private MyContact current_contact;
    private HashMap<String, Chat> my_chats = new HashMap<String, Chat>();
    private SearchView speaking_SV;
    private RecyclerView search_LST;
    private SearchingAdapter searchingAdapter;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        myUser = DataManager.getDataManager().get_account();
        HashMap<String, String> tmp = DataManager.getDataManager().get_my_users();
        MyDB.getInstance().setCallback_find_user(callback_find_account);
        if (tmp == null) {
            get_all_contacts();
        } else {
            my_contacts = tmp;
            my_contacts.forEach(
                    (ph, name) -> {
                        all_contacts.add(new MyContact().setName(name).setPhone(ph));
                    }
            );
            my_chats = DataManager.getDataManager().getMy_chats_map();
            for (int i = 0; i < all_contacts.size(); i++) {
                String id = Chat.make_chat_id(myUser.getPhone(), all_contacts.get(i).getPhone());
                if (my_chats.get(id) != null)
                    all_contacts.get(i).setPhoto(my_chats.get(id).getOther_user().getPhone());

            }
        }
        init_tool_bar();
        findViews();

        searchingAdapter = new SearchingAdapter(SearchActivity.this, find_lst);
        searchingAdapter.setChatListener(userListener);
        search_LST.setAdapter(searchingAdapter);

        init_actions();

    }

    private void findViews() {
        search_LST = findViewById(R.id.search_LST);
        speaking_SV = findViewById(R.id.speaking_SV);
    }

    private void init_tool_bar() {
        toolbar = findViewById(R.id.searching_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
                onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void get_all_contacts() {
        my_contacts = MyServices.getInstance().get_all_contacts(this);
        DataManager.getDataManager().setMy_contacts(my_contacts);
        my_chats = DataManager.getDataManager().getMy_chats_map();
        my_contacts.forEach(
                (ph, name) -> {
                    all_contacts.add(new MyContact().setName(name).setPhone(ph));
                }
        );
        my_chats = DataManager.getDataManager().getMy_chats_map();
        for (int i = 0; i < all_contacts.size(); i++) {
            String id = Chat.make_chat_id(myUser.getPhone(), all_contacts.get(i).getPhone());
            if (my_chats.get(id) != null)
                all_contacts.get(i).setPhoto(my_chats.get(id).getOther_user().getPhone());

        }

    }

    private Callback_find_account callback_find_account = new Callback_find_account() {
        @Override
        public void account_found(MyUser account) {
            if (account == null) {
                Toast.makeText(SearchActivity.this, "This contact didn't have an account ", Toast.LENGTH_SHORT).show();
                return;
            }
            String chat_id = Chat.make_chat_id(myUser.getPhone(),account.getPhone());
            ChatDB chat_newDB = account.getChats().get(chat_id);
            if (chat_newDB != null){
                DataManager.getDataManager().setCurrent_chat(new Chat(chat_newDB,myUser.getPhone()));
                go_next(SpeakingActivity.class);
                return;
            }

            ChatDB chatDB = new ChatDB()
                    .setChat_id(Chat.make_chat_id(account.getPhone(),myUser.getPhone()))
                                            .setUser1(myUser.getUserChat())
                                            .setUser2(new UserChat()
                                                            .setContact_name(current_contact.getName()) //TODO add photo? he have?
                                                            .setPhone(current_contact.getPhone()));
            Chat chat = new Chat(chatDB,myUser.getPhone()).setIs_new(true);
            DataManager.getDataManager().setCurrent_chat(chat);
            account.getUserChat().setContact_name(current_contact.getName());
            DataManager.getDataManager().setOtherUserAccount(account);
            go_next(SpeakingActivity.class);
        }

        @Override
        public void account_not_found() {
        }

        @Override
        public void error() {
            Toast.makeText(SearchActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }
    };
    private SearchingAdapter.UserListener userListener = new SearchingAdapter.UserListener() {
        @Override
        public void clicked(MyContact contact, int position) {
            my_chats = DataManager.getDataManager().getMy_chats_map();
            String id = Chat.make_chat_id(contact.getPhone(), SearchActivity.this.myUser.getPhone());
            ChatDB chatDB = my_chats.get(id);
            if (chatDB == null) {
                current_contact = contact;
                MyDB.getInstance().isAccountExists(contact.getPhone());
            } else {
                DataManager.getDataManager().setCurrent_chat(new Chat(chatDB, myUser.getPhone()));
                go_next(SpeakingActivity.class);
            }
        }
    };

    private void init_actions() {
        speaking_SV.setOnQueryTextListener(onQueryTextListener);

    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            find_lst.clear();
            if (s.isEmpty()) {
                searchingAdapter.notifyDataSetChanged();
                return true;
            }

            for (int i = 0; i < all_contacts.size(); i++) {     //TODO: not all add a limit
                if (all_contacts.get(i).getName().contains(s)) {
                    find_lst.add(all_contacts.get(i));
                }
            }
            searchingAdapter.notifyDataSetChanged();
            return true;
        }
    };

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
        finish();
    }

}
