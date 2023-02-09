package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;


import com.ron.whatsUp.R;

import com.ron.whatsUp.adapters.SearchingAdapter;
import com.ron.whatsUp.callbacks.Callback_find_account;

import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;



import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    private final int PHONE_LEN = 13;
    private ArrayList<String> phones = new ArrayList<>();
    private ArrayList<MyUser> myUsers = new ArrayList<>();
    private MyUser myUser;
    private ArrayList<MyUser> find_lst = new ArrayList<>();
    private SearchView speaking_SV;
    private RecyclerView search_LST;
    private SearchingAdapter searchingAdapter;
    private int num_of_get_api = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        myUser = DataManager.getDataManager().get_account();
        ArrayList<MyUser> tmp = DataManager.getDataManager().get_my_users();
        if(tmp == null){
            MyDB.getInstance().setCallback_find_user(callback_find_account);
            get_all_contacts();
        }
        else{
            myUsers = tmp;
        }
        findViews();
        searchingAdapter = new SearchingAdapter(SearchActivity.this, find_lst);
        searchingAdapter.setChatListener(userListener);
        search_LST.setAdapter(searchingAdapter);
        init_actions();
    }

    private void findViews() {
        search_LST =  findViewById(R.id.search_LST);
        speaking_SV =  findViewById(R.id.speaking_SV);
    }


    private void get_all_contacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String phone = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //String name = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if(phone.contains("+972")){
                phone = phone.replaceAll("[^\\d]", "");
                phone = "+"+phone;
                if(phone.length() == PHONE_LEN)
                    this.phones.add(phone);
            }

        }
        phones.close();
        num_of_get_api = this.phones.size();
        this.phones.forEach(
                ph -> {
                    MyDB.getInstance().isAccountExists(ph);
                }
        );

    }
    private Callback_find_account callback_find_account = new Callback_find_account() {
        @Override
        public synchronized void account_found(MyUser account) {
            myUsers.add(account);
            if(myUsers.size() == num_of_get_api){
                ArrayList<MyUser> tmp_users = new ArrayList<>();
                myUsers.forEach(
                        user -> {
                            if(user != null)
                                tmp_users.add(user);

                        }
                );
                myUsers = tmp_users;
                DataManager.getDataManager().setMy_users(myUsers);
            }
        }

        @Override
        public void account_not_found() {
        }

        @Override
        public void error() {
            Toast.makeText(SearchActivity.this, "Some error occurred, Please check your internet", Toast.LENGTH_SHORT).show();
        }
    };
    private SearchingAdapter.UserListener userListener =  new SearchingAdapter.UserListener() {
        @Override
        public void clicked(MyUser myUser, int position) {
            String id = Chat.make_chat_id(myUser.getPhone(),SearchActivity.this.myUser.getPhone());
           ChatDB chatDB =  myUser.getChats().get(id);
           if(chatDB == null){
               DataManager.getDataManager().setCurrent_chat(new Chat());
               DataManager.getDataManager().setOtherUser(myUser);
               go_next(SpeakingActivity.class);
           }
           else{
               DataManager.getDataManager().setCurrent_chat(new Chat(chatDB, myUser.getPhone()));
               go_next(SpeakingActivity.class);
           }

        }
    };

    private void init_actions() {
        speaking_SV.setOnQueryTextListener(onQueryTextListener);

    }
    private SearchView.OnQueryTextListener onQueryTextListener =  new SearchView.OnQueryTextListener() {
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
            myUsers.forEach(user -> {

                if (user.getPhone().contains(s)) {
                    find_lst.add(user);
                } else {
                    find_lst.remove(user);
                }

            });
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