package com.ron.whatsUp;

import android.app.Application;

import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyServices.initHelper(this);

    }



}