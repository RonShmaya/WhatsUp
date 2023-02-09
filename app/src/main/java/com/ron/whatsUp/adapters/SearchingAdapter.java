package com.ron.whatsUp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.ron.whatsUp.R;
import com.ron.whatsUp.objects.Chat;
import com.ron.whatsUp.objects.MyUser;
import com.ron.whatsUp.tools.DataManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface UserListener {
        void clicked(MyUser myUser, int position);
    }

    private Activity activity;
    private UserListener userListener;

    private ArrayList<MyUser> myUsers = new ArrayList<>();

    public SearchingAdapter(Activity activity, ArrayList<MyUser> myUsers) {
        this.activity = activity;
        this.myUsers = myUsers;
    }

    public SearchingAdapter setChatListener(UserListener userListener) {
        this.userListener = userListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chats, parent, false);
        UserHolder userHolder = new UserHolder(view);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final UserHolder holder = (UserHolder) viewHolder;
        MyUser myUser = getItem(position);


        holder.listSearching_LBL_user_name.setText(myUser.getNick_name());

        //listSearching_IMG_photo
//        Glide.with(activity).load(bar.getBar_photo()).placeholder(R.drawable.img_placeholder).into(holder.listSearching_IMG_photo);

    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }

    public MyUser getItem(int position) {
        return myUsers.get(position);
    }


    class UserHolder extends RecyclerView.ViewHolder {

        private CircleImageView listSearching_IMG_photo;
        private MaterialTextView listSearching_LBL_user_name;

        public UserHolder(View itemView) {
            super(itemView);
            listSearching_IMG_photo = itemView.findViewById(R.id.listSearching_IMG_photo);
            listSearching_LBL_user_name = itemView.findViewById(R.id.listSearching_LBL_user_name);

            itemView.setOnClickListener(view -> {
                if (userListener != null) {
                    userListener.clicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}