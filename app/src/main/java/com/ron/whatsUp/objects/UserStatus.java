package com.ron.whatsUp.objects;

import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserStatus {
    private boolean connected;
    private String phone;
    private String img="";
    private MyTime last_seen = new MyTime();


    public UserStatus() {
    }

    public boolean isConnected() {
        return connected;
    }

    public UserStatus setConnected(boolean connected) {
        this.connected = connected;
        return this;
    }

    public MyTime getLast_seen() {
        return last_seen;
    }

    public UserStatus setLast_seen(MyTime last_seen) {
        this.last_seen = last_seen;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserStatus setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public String get_last_msg_calender_output() {

        Calendar cal_now = Calendar.getInstance();
        Calendar msg_cal = last_seen.parse_my_time_to_calender();
        int diff = msg_cal.fieldDifference(cal_now.getTime(), Calendar.DATE);
        if (diff > 1) {
            return new SimpleDateFormat("HH:mm dd.MM.yy", Locale.ROOT).format(msg_cal.getTimeInMillis());
        }
        if (diff == 1) {
            return "Yesterday";
        }
        return new SimpleDateFormat("HH:mm", Locale.ROOT).format(msg_cal.getTimeInMillis());
    }

    public String getImg() {
        return img;
    }

    public UserStatus setImg(String img) {
        this.img = img;
        return this;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "connected=" + connected +
                ", phone='" + phone + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
