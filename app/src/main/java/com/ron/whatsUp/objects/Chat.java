package com.ron.whatsUp.objects;

import android.icu.util.Calendar;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class Chat extends ChatDB {
    private UserChat current_user = new UserChat();
    private UserChat other_user = new UserChat();
    private boolean is_new;


    public Chat() {
        super();
    }

    public Chat(ChatDB chatDB, String current_user_phone) {
        super(chatDB);
        make_current_user(current_user_phone);

    }

    public UserChat getCurrent_user() {
        return current_user;
    }

    public Chat setCurrent_user(UserChat current_user) {
        this.current_user = current_user;
        return this;
    }

    public UserChat getOther_user() {
        return other_user;
    }

    public Chat setOther_user(UserChat other_user) {
        this.other_user = other_user;
        return this;
    }


    public void make_current_user(String user_phone) {
        if (user_phone.equals(this.getUser1().getPhone())) {
            current_user = this.getUser1();
            other_user = this.getUser2();
        } else {
            current_user = this.getUser2();
            other_user = this.getUser1();
        }
    }

    public boolean isIs_new() {
        return is_new;
    }

    public Chat setIs_new(boolean is_new) {
        this.is_new = is_new;
        return this;
    }

    public boolean is_connected() {
        return other_user.isConnected();
    }

    public boolean is_typing() {
        return other_user.isTyping();
    }

    public MyTime last_seen() {
        return other_user.getLast_seen();
    }

    public int unread_messages() {
        return current_user.getUnread();
    }

    public boolean is_last_msg_read() {
        return last_msg.isMsg_seen();
    }

    public String get_last_msg() {
        return last_msg.getContent();
    }

    public String get_last_msg_calender_output() {

        Calendar cal_now = Calendar.getInstance();
        Calendar msg_cal = last_msg.get_msg_calender();
        int diff = msg_cal.fieldDifference(cal_now.getTime(), Calendar.DATE);
        if (diff > 1) {
            return new SimpleDateFormat("HH:mm dd.MM.yy", Locale.ROOT).format(msg_cal);
        }
        if (diff == 1) {
            return "Yesterday";
        }
        return new SimpleDateFormat("HH:mm", Locale.ROOT).format(msg_cal.getTimeInMillis());
    }

    public static String make_chat_id(String phone1, String phone2) {
        int res = phone1.compareTo(phone2);
        if (res == 1)
            return phone1 + "-" + phone2;
        return phone2 + "-" + phone1;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return current_user.getPhone().equals(chat.current_user.getPhone()) && other_user.getPhone().equals(chat.other_user.getPhone());
    }

}
