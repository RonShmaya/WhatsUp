package com.ron.whatsUp.tools;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ron.whatsUp.callbacks.CallBack_get_all_chats;
import com.ron.whatsUp.callbacks.Callback_creating_account;
import com.ron.whatsUp.callbacks.Callback_find_account;
import com.ron.whatsUp.callbacks.Callback_message;
import com.ron.whatsUp.callbacks.Callback_update_account;
import com.ron.whatsUp.callbacks.Callback_user_data_changed;
import com.ron.whatsUp.callbacks.Callback_user_status;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.ChatDB;
import com.ron.whatsUp.objects.Message;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDB {
    public static final String ACCOUNTS = "ACCOUNTS";
    public static final String CHATS = "CHATS";
    public static final String USER_STATUS = "USER_STATUS";

    private static MyDB _instance = new MyDB();
    private FirebaseDatabase database;
    private DatabaseReference refAccounts;
    private DatabaseReference refChats;
    private DatabaseReference refUserStatus;
    private boolean is_chat_listener_set;
    private boolean is_user_status_listener_set;
    private boolean is_all_msg_listen;
    private Callback_find_account callback_find_user;
    private Callback_creating_account callback_account_creating;
    private Callback_update_account callback_update_account;
    private Callback_user_data_changed callback_user_data_changed;
    private CallBack_get_all_chats callBack_get_all_chats;
    private Callback_message callback_save_message;
    private Callback_user_status callback_user_status;

    private MyDB() {
        database = FirebaseDatabase.getInstance("https://keepie-cb5ec-default-rtdb.europe-west1.firebasedatabase.app");
        refAccounts = database.getReference(ACCOUNTS);
        refChats = database.getReference(CHATS);
        refUserStatus = database.getReference(USER_STATUS);
    }

    public static MyDB getInstance() {
        return _instance;
    }

    public MyDB setCallback_find_user(Callback_find_account callback_find_user) {
        this.callback_find_user = callback_find_user;
        return this;
    }

    public MyDB setCallback_save_message(Callback_message callback_save_message) {
        this.callback_save_message = callback_save_message;
        return this;
    }

    public MyDB setCallback_account_creating(Callback_creating_account callback_account_creating) {
        this.callback_account_creating = callback_account_creating;
        return this;
    }

    public MyDB setCallBack_get_all_chats(CallBack_get_all_chats callBack_get_all_chats) {
        this.callBack_get_all_chats = callBack_get_all_chats;
        return this;
    }

    public MyDB setCallback_update_account(Callback_update_account callback_update_account) {
        this.callback_update_account = callback_update_account;
        return this;
    }

    public MyDB setCallback_user_data_changed(Callback_user_data_changed callback_user_data_changed) {
        this.callback_user_data_changed = callback_user_data_changed;
        return this;
    }

    public MyDB setCallback_user_status(Callback_user_status callback_user_status) {
        this.callback_user_status = callback_user_status;
        return this;
    }

    public void isAccountExists(String phoneID) {
        if (this.callback_find_user != null) {
            refAccounts.child(phoneID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MyUser myUser = null;
                    myUser = dataSnapshot.getValue(MyUser.class);
                    callback_find_user.account_found(myUser);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callback_find_user.error();
                }
            });
        }
    }


    public void create_account(MyUser myUser) {
        if (this.callback_account_creating != null) {
            refAccounts.child(myUser.getPhone()).setValue(myUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback_account_creating.account_created(myUser);
                        return;
                    }
                    callback_account_creating.error();
                }
            });
        }
    }

    public void update_account_lang(String lang, MyUser myUser) {
        if (this.callback_update_account != null) {
            refAccounts.child(myUser.getPhone()).child("lang").setValue(lang).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        myUser.setLang(lang);
                        DataManager.getDataManager().set_account(myUser);
                        callback_update_account.account_updated(myUser);
                        return;
                    }
                    callback_update_account.error();
                }
            });
        }
    }

    public void start_listen_chats_changes(MyUser myUser, HashMap<String, ChatDB> chats) {
        if (is_chat_listener_set)
            return;
        is_chat_listener_set = true;
//        chats.forEach(
//                (id, chatDB) -> {
        DatabaseReference databaseReference = refAccounts.child(myUser.getPhone()).child("chats");
        databaseReference.addValueEventListener(valueEventListener);
//                }
//        );


    }

//    public void add_one_listen_chats_changes(MyUser myUser, ChatDB chat) {
//        DatabaseReference databaseReference = refAccounts.child(myUser.getPhone()).child("chats");
//        databaseReference.addValueEventListener(valueEventListener);
//
//    }

    public void remove_event(MyUser myUser, HashMap<String, Chat> chats) {
//        chats.forEach(
//                (id, chat) -> {
        DatabaseReference databaseReference = refAccounts.child(myUser.getPhone()).child("chats");
        databaseReference.removeEventListener(valueEventListener);
//                }
//        );
        is_chat_listener_set = false;

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (callback_user_data_changed != null) {
                GenericTypeIndicator<HashMap<String, ChatDB>> dataType = new GenericTypeIndicator<HashMap<String, ChatDB>>() {
                };
                HashMap<String, ChatDB> chatDBHashMap = dataSnapshot.getValue(dataType);
                if (chatDBHashMap == null)
                    chatDBHashMap = new HashMap<>();
                callback_user_data_changed.chat_change(chatDBHashMap);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public void getAllChats(MyUser myUser) {
        if (this.callBack_get_all_chats != null) {
            refAccounts.child(myUser.getPhone()).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<HashMap<String, ChatDB>> dataType = new GenericTypeIndicator<HashMap<String, ChatDB>>() {
                    };
                    HashMap<String, ChatDB> chatDBHashMap = dataSnapshot.getValue(dataType);
                    if (chatDBHashMap == null)
                        chatDBHashMap = new HashMap<>();
                    callBack_get_all_chats.all_chats(chatDBHashMap);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callBack_get_all_chats.error();
                }
            });
        }
    }

    public void clear_callbacks() {
        callback_find_user = null;
        callback_account_creating = null;
        callback_update_account = null;
        callback_user_data_changed = null;
        callBack_get_all_chats = null;
        callback_save_message = null;
        callback_user_status = null;

    }

    public void save_message(String chat_id, Message msg) {
        if (this.callback_save_message != null) {
            refChats.child(chat_id).child("messages").child(msg.getMessage_id()).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback_save_message.save_msg(msg);
                    }
                }
            });
        }
    }

    public void start_listen_all_messages(Chat chat) {
        if (is_all_msg_listen)
            return;
        is_all_msg_listen = true;
        DatabaseReference databaseReference = refChats.child(chat.getChat_id()).child("messages");
        databaseReference.addValueEventListener(allMessagesEventListener);

    }

    public void remove_listen_all_messages(Chat chat) {
        DatabaseReference databaseReference = refChats.child(chat.getChat_id()).child("messages");
        databaseReference.removeEventListener(allMessagesEventListener);
        is_all_msg_listen = false;

    }
    ValueEventListener allMessagesEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (callback_save_message != null) {
                GenericTypeIndicator<HashMap<String, Message>> dataType = new GenericTypeIndicator<HashMap<String, Message>>() {
                };
                HashMap<String, Message> stringMessageHashMap = dataSnapshot.getValue(dataType);
                if (stringMessageHashMap == null)
                    stringMessageHashMap = new HashMap<>();
                callback_save_message.get_all_msgs(stringMessageHashMap);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public void create_new_chat(ChatDB chat_new) {
        if (this.callback_save_message != null) {
            refAccounts.child(chat_new.getUser1().getPhone()).child("chats").child(chat_new.getChat_id()).setValue(chat_new).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback_save_message.save_chat(chat_new);
                    }
                }
            });
            refAccounts.child(chat_new.getUser2().getPhone()).child("chats").child(chat_new.getChat_id()).setValue(chat_new).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    }
                }
            });
        }
    }

    public void update_chat_messages(Chat chat, HashMap<String, Message> messages) {
        if (this.callback_save_message != null) {
            refChats.child(chat.getChat_id()).child("messages").setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback_save_message.save_msg(null);
                    }
                }
            });
        }
    }

    public void update_other_user_chat(ChatDB chatDB, String other_user_phone) {
        if (this.callback_save_message != null) {
            refAccounts.child(other_user_phone).child("chats").child(chatDB.getChat_id()).setValue(chatDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback_save_message.save_msg(null);
                    }
                }
            });
        }
    }

    public void update_user_status(UserStatus userStatus) {
        refUserStatus.child(userStatus.getPhone()).setValue(userStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (callback_user_status != null) {
                        return;
                    }
                }
                if (callback_user_status != null) {
                    callback_user_status.error();
                }
            }
        });
    }

    public void start_listen_user_status(String phone) {
        if (is_user_status_listener_set)
            return;
        is_user_status_listener_set = true;
        DatabaseReference databaseReference = refUserStatus.child(phone);
        databaseReference.addValueEventListener(userStatusEventListener);

    }

    public void remove_user_status_event(String phone) {
        DatabaseReference databaseReference = refUserStatus.child(phone);
        databaseReference.removeEventListener(userStatusEventListener);
        is_user_status_listener_set = false;

    }
    ValueEventListener userStatusEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (callback_user_status != null) {
                UserStatus user_status = dataSnapshot.getValue(UserStatus.class);
                callback_user_status.user_status_updated(user_status);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public void update_account_photo(String image_uri, MyUser myUser) {
        if (this.callback_update_account != null) {
            refAccounts.child(myUser.getPhone()).child("img_uri").setValue(image_uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        myUser.setImg_uri(image_uri);
                        DataManager.getDataManager().set_account(myUser);
                        return;
                    }
                    callback_update_account.error();
                }
            });
        }
    }
}