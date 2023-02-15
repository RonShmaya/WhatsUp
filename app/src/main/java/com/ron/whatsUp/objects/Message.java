package com.ron.whatsUp.objects;

import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class Message {
    private String sender="";
    private String receiver="";
    private String content="";
    private String message_id="";
    private boolean msg_seen =false;
    private MyTime myTime= new MyTime();



    public Message() {

    }


    public String getSender() {
        return sender;
    }

    public Message setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public Message setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isMsg_seen() {
        return msg_seen;
    }

    public Message setMsg_seen(boolean msg_seen) {
        this.msg_seen = msg_seen;
        return this;
    }

    public MyTime getMyTime() {
        return myTime;
    }

    public Message setMyTime(MyTime myTime) {
        this.myTime = myTime;
        return this;
    }

    public String getMessage_id() {
        return message_id;
    }

    public Message setMessage_id(String message_id) {
        this.message_id = message_id;
        return this;
    }
    public void update_by_calender_now(){
        this.myTime.update_my_time_by_calender(Calendar.getInstance());
    }
    public Calendar get_msg_calender(){
        return myTime.parse_my_time_to_calender();
    }
    public String get_hour_minutes_str(){
         return new SimpleDateFormat("HH:mm", Locale.ROOT).format(get_msg_calender().getTimeInMillis());
    }



}
