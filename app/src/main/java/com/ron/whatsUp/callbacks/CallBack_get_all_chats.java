package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.ChatDB;

import java.util.HashMap;

public interface CallBack_get_all_chats {
    void all_chats(HashMap<String, ChatDB> chatDBHashMap);
    void error();
}
