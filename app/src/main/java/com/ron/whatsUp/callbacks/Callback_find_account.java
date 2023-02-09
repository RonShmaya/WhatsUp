package com.ron.whatsUp.callbacks;


import com.ron.whatsUp.objects.MyUser;

public interface Callback_find_account {
    void account_found(MyUser account);
    void account_not_found();
    void error();
}