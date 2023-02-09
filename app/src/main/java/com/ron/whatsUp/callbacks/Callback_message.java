package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;

import java.util.HashMap;

public interface Callback_message {
    void save_msg(Message msg);
    void get_all_msgs(HashMap<String, Message> stringMessageHashMap);
    void error();

    void save_chat(ChatDB chat_new);
}
