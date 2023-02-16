package com.ron.whatsUp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_db_for_service;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class WhatsAppService extends Service {
    public static final String START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public static final int NOTIFICATION_ID = 5;
    private boolean isServiceRunningRightNow = false;
    private ArrayList<Chat> chats = null;
    private String old_chat = "";
    private int num_of_message = 0;
    private NotificationCompat.Builder builder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            return START_NOT_STICKY;
        }
        if (intent.getAction().equals(START_FOREGROUND_SERVICE)) {
            if (set_get_service_alive(null)) {
                return START_STICKY;
            }

            set_get_service_alive(true);
            make_first_notification("WhatsUp", "");
            startListen();
            return START_STICKY;
        }
        if (intent.getAction().equals(STOP_FOREGROUND_SERVICE)) {
            if (!set_get_service_alive(null)) {
                return START_NOT_STICKY;
            }
            set_get_service_alive(false);
            String phone = "";
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            else if (chats != null && !chats.isEmpty()) {
                phone = chats.get(0).getCurrent_user().getPhone();
            } else if (DataManager.getDataManager().get_account() != null) {
                phone = DataManager.getDataManager().get_account().getPhone();
            }

            MyDB.getInstance().service_from_db_remove_event(phone, null);
            MyDB.getInstance().setCallback_db_for_service(null);
            chats = null;
            stopListen();
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;

    }

    private void stopListen() {

    }

    private void make_first_notification(String title, String subtitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", title, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
//        Intent intent = new Intent(this, Receiver.class);
//        intent.setAction("ACTION_NOTIFICATION_CLICK");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.whatsapp_icon)
                //.setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void update_notification(int note_id, String title, String subtitle) {
        Intent notificationIntent = new Intent(this, EnterAppActivity.class);
        notificationIntent.setAction("NOTE");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, note_id, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.whatsapp_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(note_id, builder.build());

    }

    private void startListen() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        MyDB.getInstance().setCallback_db_for_service(callback_db_for_service);
        MyDB.getInstance().find_account_for_service(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        new Thread(() -> {
            while (isServiceRunningRightNow) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Callback_db_for_service callback_db_for_service = new Callback_db_for_service() {
        @Override
        public void account_found(MyUser account) {
            if (account == null)
                return;
            MyDB.getInstance().service_from_db_start_listen_chats_changes(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), null);
        }

        @Override
        public void listen_chats(HashMap<String, ChatDB> chatDBHashMap) {
            if(isAppAlive())
                return;
            if (chatDBHashMap == null)
                return;
            if (chats == null) {
                chats = new ArrayList<>();
                chatDBHashMap.forEach(
                        (id, chatDB) -> {
                            chats.add(new Chat(chatDB, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                        }
                );
                chats.sort(chat_comparator);
                return;
            }
            String old_message = chats.get(chats.size() - 1).getLast_msg().getMessage_id();
            chats.clear();
            chatDBHashMap.forEach(
                    (id, chatDB) -> {
                        chats.add(new Chat(chatDB, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                    }
            );
            chats.sort(chat_comparator);

            if (!old_message.equals(chats.get(chats.size() - 1).getLast_msg().getMessage_id()) && chats.get(chats.size() - 1).getLast_msg().getSender().equals(chats.get(chats.size() - 1).getOther_user().getPhone()) &&
                    !chats.get(chats.size() - 1).getLast_msg().isMsg_seen()) {

                if (chats.isEmpty())
                    return;

                HashMap<String, String> contacts = MyServices.getInstance().service_for_db_get_all_contacts();
                String name = contacts.get(chats.get(chats.size() - 1).getOther_user().getPhone());
                if (name == null) {
                    name = chats.get(chats.size() - 1).getOther_user().getPhone();
                }
                int not_id = NOTIFICATION_ID;
                try {
                    not_id = Integer.parseInt(chats.get(chats.size() - 1).getOther_user().getPhone().replace("+972", ""));
                } catch (Exception exp) {
                    Log.d("MyLog", "verify way" + exp.getMessage());
                    update_notification(NOTIFICATION_ID, name, chats.get(chats.size() - 1).getLast_msg().getContent());
                    return;
                }
                update_notification(not_id, name, chats.get(chats.size() - 1).getLast_msg().getContent());

            }
        }

        @Override
        public void listen_messages(HashMap<String, Message> stringMessageHashMap) {
        }
    };
    private Comparator<Chat> chat_comparator = new Comparator<Chat>() {
        @Override
        public int compare(Chat ch, Chat t1) {
            if (ch.getLast_msg().get_msg_calender().before(t1.getLast_msg().get_msg_calender())) {
                return -1;
            }
            return 1;
        }
    };

    private boolean isAppAlive() {
        AppCompatActivity appCompatActivity = MyServices.getInstance().getService_app_compact();
        if (appCompatActivity == null) {
            return true;
        }
        if (appCompatActivity.isDestroyed()) {
            return false;
        }
        return true;
    }

    public synchronized boolean set_get_service_alive(Boolean value) {
        if (value != null) {
            isServiceRunningRightNow = value;
        }
        return isServiceRunningRightNow;
    }

}
