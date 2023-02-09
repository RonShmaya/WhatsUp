package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.MyUser;

public interface Callback_update_account {
    void account_updated(MyUser account);
    void error();
}
