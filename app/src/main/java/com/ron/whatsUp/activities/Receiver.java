package com.ron.whatsUp.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ron.whatsUp.tools.MyServices;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() !=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent_ser = new Intent(context, WhatsAppService.class);
            intent_ser.setAction(WhatsAppService.START_FOREGROUND_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            return;
        }

    }
}
