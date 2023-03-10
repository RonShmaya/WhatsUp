package com.ron.whatsUp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.ron.whatsUp.R;
import com.ron.whatsUp.callbacks.Callback_update_account;
import com.ron.whatsUp.callbacks.Callback_upload_img;
import com.ron.whatsUp.dialogs.ImageDialog;
import com.ron.whatsUp.objects.MyTime;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.objects.UserStatus;
import com.ron.whatsUp.tools.DataManager;
import com.ron.whatsUp.tools.MyDB;
import com.ron.whatsUp.tools.MyServices;
import com.google.firebase.database.annotations.NotNull;
import com.ron.whatsUp.tools.MyStorage;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView profile_IMG_photo;
    private LottieAnimationView profile_Lottie_photo;
    private MaterialTextView profile_LBL_name;
    private MaterialToolbar toolbar;
    private MaterialTextView profile_LBL_phone;
    private TextInputLayout profile_TIL_lan;
    private AutoCompleteTextView profile_ACTV_lan;
    private MyUser myUser;

    // TODO: 08/02/2023 update name
    // TODO: 08/02/2023 image picker
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        myUser = DataManager.getDataManager().get_account();
        MyStorage.getInstance().setCallback_upload_profile_img(callback_upload_img);
        MyDB.getInstance().setCallback_update_account(callback_update_account);
        init_tool_bar();
        findViews();
        init_data();
        init_autoCompleteTextView();
        init_actions();
    }


    private void init_data() {
        profile_LBL_name.setText(myUser.getNick_name());
        profile_LBL_phone.setText(myUser.getPhone());
        if(!myUser.getImg_uri().isEmpty()){
            Glide.with(this).load(myUser.getImg_uri()).placeholder(R.drawable.ic_user).into(profile_IMG_photo);
        }
    }

    private void findViews() {
        profile_IMG_photo = findViewById(R.id.profile_IMG_photo);
        profile_Lottie_photo = findViewById(R.id.profile_Lottie_photo);
        profile_LBL_name = findViewById(R.id.profile_LBL_name);
        profile_LBL_phone = findViewById(R.id.profile_LBL_phone);
        profile_TIL_lan = findViewById(R.id.profile_TIL_lan);
        profile_ACTV_lan = findViewById(R.id.profile_ACTV_lan);
    }
    private void init_tool_bar() {
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.no_add_menu, menu);
        return true;
    }
    private void init_autoCompleteTextView() {
        profile_ACTV_lan.setText(myUser.getLang());
        ArrayList<String> lang_list = DataManager.getDataManager().getLang();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.list_item, lang_list);
        profile_ACTV_lan.setAdapter(adapter);
    }

    private void init_actions() {
        profile_ACTV_lan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                MyDB.getInstance().update_account_lang(profile_ACTV_lan.getText().toString(),myUser);
            }
        });
        profile_IMG_photo.setOnClickListener(view -> new ImageDialog().show(this,myUser.getPhone(),myUser.getImg_uri()));
        profile_Lottie_photo.setOnClickListener(view -> add_photo());
    }

    private <T extends AppCompatActivity> void go_next(Class<T> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(DataManager.getDataManager().isIs_changed_lang()){
            DataManager.getDataManager().killAppCompatActivity();
            DataManager.getDataManager().set_lang_changed(false);
            go_next(ChatsActivity.class);
            return;
        }
        finish();
    }

    private Callback_update_account callback_update_account = new Callback_update_account() {
        @Override
        public void account_updated(MyUser account) {
            MyServices.getInstance().update_app_lang(account.getLang(), SettingActivity.this);
            DataManager.getDataManager().set_lang_changed(true);
            go_next(SettingActivity.class);
        }

        @Override
        public void error() {
            Toast.makeText(SettingActivity.this, "Some error occurred, please check your internet", Toast.LENGTH_SHORT).show();
        }
    };
    private void add_photo() {
        ImagePicker.Companion.with(this)
                .crop()
                .cropOval()
                .maxResultSize(512, 512, true)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog((Function1) (new Function1() {
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                }));
    }
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = result.getData().getData();
            profile_IMG_photo.setImageURI(uri);
            MyStorage.getInstance().uploadImageProfile(myUser.getPhone() , uri);
        } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "image upload failed please, try again", Toast.LENGTH_SHORT).show();
        }
    });

    private Callback_upload_img callback_upload_img =new Callback_upload_img() {
        @Override
        public void img_uploaded(String image_uri) {

            if(image_uri != null && !image_uri.isEmpty()){
                myUser.setImg_uri(image_uri);
                UserStatus userStatus = new UserStatus().setConnected(true).setImg(image_uri).setLast_seen(new MyTime().update_my_time_by_calender(Calendar.getInstance())).setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                MyDB.getInstance().update_user_status(userStatus);
                MyDB.getInstance().update_account_photo(image_uri,myUser);
            }
        }

        @Override
        public void failed() {
            Toast.makeText(SettingActivity.this, "Image upload failed, please try again", Toast.LENGTH_SHORT).show();
        }
    };
}