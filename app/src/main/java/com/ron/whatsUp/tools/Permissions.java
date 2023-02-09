package com.ron.whatsUp.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ron.whatsUp.callbacks.Callback_permissions;

public class Permissions {
    private static Permissions _instance = null;
    private AppCompatActivity appCompatActivity;
    private final String CONTACT_PERMISSION = Manifest.permission.READ_CONTACTS;
    private final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private String current_ask = "";
    private Callback_permissions callback_permissions;

    public Permissions setCallback_permissions(Callback_permissions callback_permissions) {
        this.callback_permissions = callback_permissions;
        return this;
    }

    private Permissions(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        requestPermissionLauncher = this.appCompatActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), permissionCallBack);
        manuallyPermissionResultLauncher = this.appCompatActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(callback_permissions != null )
                            if( all_permissions_ok())
                                callback_permissions.all_per_ok();
                            else if(!current_ask.equals(CONTACT_PERMISSION))
                                requestContacts();
                    }
                });
    }
    public static Permissions getPermissions() {
        return _instance;
    }
    public static void initHelper(AppCompatActivity appCompatActivity) {
        if (_instance == null) {
            _instance = new Permissions(appCompatActivity);
        }
        else{
            _instance.appCompatActivity=appCompatActivity;
        }
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    ActivityResultCallback<Boolean> permissionCallBack = new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean isGranted) {
            if (!isGranted) {
                requestPermissionWithRationaleCheck();
            }else{
                if(callback_permissions != null )
                    if( all_permissions_ok())
                        callback_permissions.all_per_ok();
                    else if(!current_ask.equals(CONTACT_PERMISSION))
                        requestContacts();
            }
        }
    };
    ActivityResultLauncher<String> requestPermissionLauncher;

    public void requestContacts() {
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        current_ask = CONTACT_PERMISSION;
    }
    public void requestCAMERA() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        current_ask = CAMERA_PERMISSION;

    }
    public boolean verifyPermission(String permission){

        return permission.isEmpty() ? true : ContextCompat.checkSelfPermission(appCompatActivity, permission) == PackageManager.PERMISSION_GRANTED ;
    }
    public boolean all_permissions_ok(){

        return  ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissionWithRationaleCheck() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, current_ask)) {

            String message = "it's necessary to give the permissions";
            AlertDialog alertDialog =
                    new AlertDialog.Builder(appCompatActivity)
                            .setMessage(message)
                            .setPositiveButton(appCompatActivity.getString(android.R.string.ok),
                                    (dialog, which) -> {
                                        if(current_ask.equals(CAMERA_PERMISSION))
                                            requestCAMERA();
                                        else if(current_ask.equals(CONTACT_PERMISSION))
                                            requestContacts();
                                        dialog.cancel();
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // disabled functions due to denied permissions
                                }
                            })
                            .show();
            alertDialog.setCanceledOnTouchOutside(true);
        } else {
            openPermissionSettingDialog();
        }
    }

    private void openPermissionSettingDialog() {
        String message = "Go to setting to accept Camera and Contact";
        AlertDialog alertDialog =
                new AlertDialog.Builder(appCompatActivity)
                        .setMessage(message)
                        .setPositiveButton(appCompatActivity.getString(android.R.string.ok),
                                (dialog, which) -> {
                                    openSettingsManually ();
                                    dialog.cancel();
                                }).show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void openSettingsManually() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", appCompatActivity.getPackageName(), null);
        intent.setData(uri);
        manuallyPermissionResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> manuallyPermissionResultLauncher;
}