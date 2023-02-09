package com.ron.whatsUp.objects;

import com.firebase.ui.auth.data.model.User;

import java.util.HashMap;
import java.util.Objects;

public class ChatDB {
    private UserChat user1 = new UserChat();
    private UserChat user2= new UserChat();
    protected String chat_id="";
    protected Message last_msg = new Message();
    protected HashMap<String,String> messages_id = new HashMap<>();


    public ChatDB() {
    }

    public ChatDB(ChatDB chatDB) {
        this.user1 = chatDB.user1;
        this.user2 = chatDB.user2;
        this.chat_id = chatDB.chat_id;
        this.messages_id = chatDB.messages_id;
        this.last_msg = chatDB.last_msg;
    }
    public ChatDB(Chat chat) {
        this.user1 = chat.getUser1();
        this.user2 = chat.getUser2();
        this.chat_id = chat.chat_id;
        this.messages_id = chat.messages_id;
        this.last_msg = chat.last_msg;
    }

    public String getChat_id() {
        return chat_id;
    }

    public ChatDB setChat_id(String chat_id) {
        this.chat_id = chat_id;
        return this;
    }

    public HashMap<String, String> getMessages_id() {
        return messages_id;
    }

    public ChatDB setMessages_id(HashMap<String, String> messages_id) {
        this.messages_id = messages_id;
        return this;
    }

    public UserChat getUser1() {
        return user1;
    }

    public ChatDB setUser1(UserChat user1) {
        this.user1 = user1;
        return this;
    }

    public UserChat getUser2() {
        return user2;
    }

    public ChatDB setUser2(UserChat user2) {
        this.user2 = user2;
        return this;
    }

    public void update_id(){
        int res = user1.getPhone().compareTo(user2.getPhone());
        if(res == 1){
            chat_id=user1.getPhone()+"-"+user2.getPhone();
        }
        else{
            chat_id=user2.getPhone()+"-"+user1.getPhone();
        }

    }
    public Message getLast_msg() {
        return last_msg;
    }

    public ChatDB setLast_msg(Message last_msg) {
        this.last_msg = last_msg;
        return this;
    }


    public void add_msg(String msg) {
        this.messages_id.put(msg, msg);
    }
}
