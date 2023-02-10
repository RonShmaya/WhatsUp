package com.ron.whatsUp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_find_account;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;

public class EnterAppActivity extends AppCompatActivity {

    private LottieAnimationView enter_app_lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_app);
        MyDB.getInstance().setCallback_find_user(callback_find_account);
        findViews();
        decide_page_to_open();
    }

    private void findViews() {
        enter_app_lottie = findViewById(R.id.enter_app_lottie);
    }

    private void decide_page_to_open() {
        enter_app_lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                verifyUser();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void verifyUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            go_next(LoginActivity.class); // no user connected
        } else {
            MyDB.getInstance().isAccountExists(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
    }

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
        finish();
    }

    private Callback_find_account callback_find_account = new Callback_find_account() {
        @Override
        public void account_found(MyUser account) {
            if (account != null) {
                DataManager.getDataManager().set_account(account);
                go_next(ChatsActivity.class);
                return;
            }
            FirebaseAuth.getInstance().signOut();
            go_next(LoginActivity.class);
        }


        @Override
        public void account_not_found() {

        }

        @Override
        public void error() {
            FirebaseAuth.getInstance().signOut();
            go_next(LoginActivity.class);
        }
    };

}