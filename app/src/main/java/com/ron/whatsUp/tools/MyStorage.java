package com.ron.whatsUp.tools;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ron.whatsUp.callbacks.Callback_upload_img;

public class MyStorage {
    public static final String ACCOUNTS_PHOTO = "ACCOUNTS_PHOTO";

    private static MyStorage _instance = new MyStorage();
    private Callback_upload_img callback_upload_profile_img;
    private FirebaseStorage myStorage;
    private StorageReference ref_account;


    private MyStorage() {
        myStorage = FirebaseStorage.getInstance();
        ref_account = myStorage.getReference(ACCOUNTS_PHOTO);
    }

    public MyStorage setCallback_upload_profile_img(Callback_upload_img callback_upload_profile_img) {
        this.callback_upload_profile_img = callback_upload_profile_img;
        return this;
    }


    public void uploadImageProfile(String photoId, Uri resultUri) {
        if (resultUri != null) {
            ref_account.child(photoId).putFile(resultUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref_account.child(photoId).getDownloadUrl().addOnSuccessListener(uri -> {
                                if (callback_upload_profile_img != null) {
                                    callback_upload_profile_img.img_uploaded(uri.toString());
                                }
                            });
                        } else if (callback_upload_profile_img != null) {
                            callback_upload_profile_img.failed();
                        }
                    });
        }
    }


    public static MyStorage getInstance() {
        return _instance;
    }

}