package com.ron.whatsUp.objects;


import java.util.HashMap;

public class MyUser {


    private String id= "";
    private String nick_name = "";
    private String phone = "";
    private String img_uri = "";
    private String lang= "";
    private UserChat userChat= new UserChat();
    private HashMap<String,ChatDB> chats = new HashMap<>();

    public MyUser() {
    }

    public String getId() {
        return id;
    }

    public MyUser setId(String id) {
        this.id = id;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public MyUser setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getNick_name() {
        return nick_name;
    }

    public MyUser setNick_name(String nick_name) {
        this.nick_name = nick_name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public HashMap<String, ChatDB> getChats() {
        return chats;
    }

    public MyUser setChats(HashMap<String, ChatDB> chats) {
        this.chats = chats;
        return this;
    }

    public MyUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserChat getUserChat() {
        return userChat;
    }

    public MyUser setUserChat(UserChat userChat) {
        this.userChat = userChat;
        return this;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public MyUser setImg_uri(String img_uri) {
        this.img_uri = img_uri;
        return this;
    }
}
