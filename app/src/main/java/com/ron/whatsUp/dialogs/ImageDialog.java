package com.ron.whatsUp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.ron.whatsUp.R;

public class ImageDialog {

    private MaterialTextView post_LBL_Name;
    private AppCompatImageView post_IMG;
    private Context context;
    private String name;
    private String url;


    public void show(Context context, String name , String url) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        this.context = context;
        this.name = name;
        this.url = url;
        findViews(dialog);
        dialog.show();
    }

    private void findViews(Dialog dialog) {
        post_LBL_Name = dialog.findViewById(R.id.post_LBL_Name);
        post_LBL_Name.setText(name);

        if (!url.isEmpty()) {
            post_IMG = dialog.findViewById(R.id.post_IMG);
            Glide.with(context).load(url).placeholder(R.drawable.ic_user).into(post_IMG);
        }

    }

}
