package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyUser;

import java.util.HashMap;

public interface Callback_db_for_service {
    void account_found(MyUser account);
    void listen_chats(HashMap<String, ChatDB> chatDBHashMap);
    void listen_messages(HashMap<String, Message> stringMessageHashMap);
}
