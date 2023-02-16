package com.ron.whatsUp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_creating_account;
import com.ron.whatsUp.callbacks.Callback_find_account;
import com.ron.whatsUp.objects.MyTime;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserChat;
import com.ron.whatsUp.objects.UserStatus;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton login_BTN_login;
    private TextInputLayout login_TIL_lan;
    private AutoCompleteTextView login_ACTV_lan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyDB.getInstance().setCallback_account_creating(callback_creating_account);
        MyDB.getInstance().setCallback_find_user(callback_find_account);
        findViews();
        init_autoCompleteTextView();
        init_actions();
    }

    private void init_actions() {
        login_BTN_login.setOnClickListener(onClickListener);
    }

    private void findViews() {
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_TIL_lan = findViewById(R.id.login_TIL_lan);
        login_ACTV_lan = findViewById(R.id.login_ACTV_lan);
    }

    private void init_autoCompleteTextView() {
        login_ACTV_lan.setText(DataManager.ENGLISH);
        ArrayList<String> lang_list = DataManager.getDataManager().getLang();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.list_item, lang_list);
        login_ACTV_lan.setAdapter(adapter);

    }

    private View.OnClickListener onClickListener = view -> make_FirebaseAuth();

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> connect_to_user()
    );

    private void make_FirebaseAuth() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)      // Set logo drawable
                .setTheme(R.style.Theme_MyApplication)      // Set theme
                .setTosAndPrivacyPolicyUrls("https://firebase.google.com/docs/auth/android/firebaseui?hl=en&authuser=0", "https://firebase.google.com/docs/auth/android/firebaseui?hl=en&authuser=0")
                .build();
        signInLauncher.launch(signInIntent);
    }

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        MyDB.getInstance().setCallback_account_creating(null);
        MyDB.getInstance().setCallback_find_user(null);
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
        finish();
    }

    private void connect_to_user() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            MyDB.getInstance().isAccountExists(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
    }

    private Callback_creating_account callback_creating_account = new Callback_creating_account() {
        @Override
        public void account_created(MyUser myUser) {
            DataManager.getDataManager().set_account(myUser);
            go_next(ChatsActivity.class);
        }

        @Override
        public void error() {
            Toast.makeText(LoginActivity.this, "some error occurred, please check your internet", Toast.LENGTH_LONG).show();
        }
    };
    private Callback_find_account callback_find_account = new Callback_find_account() {
        @Override
        public void account_found(MyUser account) {
            if (account != null) {
                DataManager.getDataManager().set_account(account);
                go_next(ChatsActivity.class);
                return;
            }
            MyUser myUser = new MyUser()
                    .setId(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setLang(login_ACTV_lan.getText().toString())
                    .setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .setUserChat(new UserChat().setName(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setTyping(false).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                    .setNick_name(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            MyDB.getInstance().create_account(myUser);
        }


        @Override
        public void account_not_found() {
        }

        @Override
        public void error() {
            Toast.makeText(LoginActivity.this, "Error occurred, please check your internet", Toast.LENGTH_SHORT).show();
        }
    };
}