package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.MyUser;

public interface Callback_creating_account {
    void account_created(MyUser myUser);
    void error();
}
