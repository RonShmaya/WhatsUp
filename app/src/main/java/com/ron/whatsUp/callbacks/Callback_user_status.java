package com.ron.whatsUp.callbacks;

import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserStatus;

public interface Callback_user_status {
    void user_status_updated(UserStatus userStatus);
    void error();
}
