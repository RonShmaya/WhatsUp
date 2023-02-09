package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.MyUser;

import java.util.HashMap;

public interface Callback_user_data_changed {
    void chat_change(ChatDB chatDB);
}
