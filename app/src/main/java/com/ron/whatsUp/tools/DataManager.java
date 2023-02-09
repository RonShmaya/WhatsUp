package com.ron.whatsUp.tools;


import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_message;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataManager {

    public static final String ENGLISH = "English";
    public static final String HEBREW = "Hebrew";
    public static final int READ_REG = R.drawable.ic_readed;
    public static final int READ_BLUE = R.drawable.ic_readed_blue;
    private MyUser my_current_user = null;
    private ArrayList<MyUser> my_users = null;
    private Chat current_chat;
    private static DataManager _instance = new DataManager();
    private MyUser other_user;
    private Callback_message callback_message;

    public DataManager setCallback_message(Callback_message callback_message) {
        this.callback_message = callback_message;
        return this;
    }
    public void update_net_chat_created(Chat chat){
        if(this.callback_message != null){
            this.callback_message.save_chat(chat);
        }
    }

    private DataManager() {
    }


    public static DataManager getDataManager() {
        return _instance;
    }

    public DataManager set_account(MyUser account) {
        my_current_user = account;
        return this;
    }
    public MyUser get_account() {
        return my_current_user;
    }
    public ArrayList<String> getLang() {
        return new ArrayList<String>(Arrays.asList(ENGLISH,HEBREW));
    }

    public Chat getCurrent_chat() {
        return current_chat;
    }

    public DataManager setCurrent_chat(Chat current_chat) {
        this.current_chat = current_chat;
        return this;
    }

    public ArrayList<MyUser> get_my_users() {
        return my_users;
    }

    public DataManager setMy_users(ArrayList<MyUser> my_users) {
        this.my_users = my_users;
        return this;
    }

    public void setOtherUser(MyUser otherUser) {
        this.other_user = otherUser;
    }

    public MyUser getOther_user() {
        return other_user;
    }
}