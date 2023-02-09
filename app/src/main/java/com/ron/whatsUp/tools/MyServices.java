package com.ron.whatsUp.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MyServices {
    private static MyServices _instance = null;
    private Context context;

    private MyServices(Context context) {
        this.context = context.getApplicationContext();

    }

    public static void initHelper(Context context) {
        if (_instance == null) {
            _instance = new MyServices(context);
        }
    }

    public static MyServices getInstance(){
        return _instance;
    }
    public void update_app_lang(String lan, AppCompatActivity appCompatActivity){
        if(!lan.equals(DataManager.HEBREW) && !lan.equals(DataManager.ENGLISH))
            return;

        String locale_lang = null;


        if(lan.equals(DataManager.ENGLISH)){
            locale_lang = "en";
        }
        else if(lan.equals(DataManager.HEBREW)){
            locale_lang = "iw";
        }

        Resources res = appCompatActivity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(locale_lang));
        res.updateConfiguration(conf, dm);
    }
}
