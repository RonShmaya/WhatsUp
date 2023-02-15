package com.ron.whatsUp.tools;


import androidx.appcompat.app.AppCompatActivity;

import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_chats_to_messages;
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
    private HashMap<String,String> my_contacts = null;
    private Chat current_chat;
    private AppCompatActivity main_activity;
    private Callback_chats_to_messages callback_chats_to_messages;
    private static DataManager _instance = new DataManager();
    private MyUser other_user;
    private Callback_message callback_message;
    private HashMap<String, Chat> my_chats_map;
    private boolean is_changed_lang;

    public DataManager setCallback_message(Callback_message callback_message) {
        this.callback_message = callback_message;
        return this;
    }
    public void update_net_chat_created(Chat chat){
        if(this.callback_message != null){
            this.callback_message.save_chat(chat);
        }
    }

    public DataManager setCallback_chats_to_messages(Callback_chats_to_messages callback_chats_to_messages) {
        this.callback_chats_to_messages = callback_chats_to_messages;
        return this;
    }

    public Callback_chats_to_messages getCallback_chats_to_messages() {
        return callback_chats_to_messages;
    }

    private DataManager() {
    }
    private DataManager(HashMap<String,String> my_contacts) {
        this.my_contacts = my_contacts;
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

    public HashMap<String,String> get_my_users() {
        return my_contacts;
    }

    public DataManager setMy_contacts(HashMap<String,String> my_contacts) {
        this.my_contacts = my_contacts;
        return this;
    }

    public void setOtherUserAccount(MyUser otherUser) {
        this.other_user = otherUser;
    }

    public MyUser getOther_user() {
        return other_user;
    }

    public void set_all_chats(HashMap<String, Chat> my_chats_map) {
        this.my_chats_map = my_chats_map;
    }

    public HashMap<String, Chat> getMy_chats_map() {
        return my_chats_map;
    }

    public void set_lang_changed(boolean lang_changed) {
        this.is_changed_lang = lang_changed;
    }

    public boolean isIs_changed_lang() {
        return is_changed_lang;
    }

    public AppCompatActivity getMain_activity() {
        return main_activity;
    }

    public DataManager setMain_activity(AppCompatActivity main_activity) {
        this.main_activity = main_activity;
        return this;
    }
    public void killAppCompatActivity() {
        this.main_activity.finish();
    }

    public void reloaded() {
        callback_message = null;
        main_activity = null;
        callback_chats_to_messages = null;
        _instance = new DataManager(my_contacts);
    }
}