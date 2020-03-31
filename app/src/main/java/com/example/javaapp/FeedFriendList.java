package com.example.javaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FeedFriendList extends RecyclerView.Adapter<FeedFriendList.Friend_View_Holder>{
    Context context;
    ArrayList<String> Friend_names;
    ArrayList<String>Friend_emails;



    public FeedFriendList(Context context, ArrayList<String> Friend_names, ArrayList<String> Friend_emails) {
        this.context = context;
        this.Friend_names = Friend_names;
        this.Friend_emails = Friend_emails;
    }

    class Friend_View_Holder extends RecyclerView.ViewHolder {
        private ImageView Friend_profileImg;
        private LinearLayout friendsList;
        private TextView Friend_name, FriendEmail;

        public Friend_View_Holder(@NonNull View itemView) {
            super(itemView);
            friendsList = itemView.findViewById(R.id.FriendsFeedView);
            Friend_profileImg = itemView.findViewById(R.id.friend_image_feed);
            Friend_name = itemView.findViewById(R.id.feed_friend_name);
            FriendEmail = itemView.findViewById(R.id.feed_friend_email);
        }

        public Friend_View_Holder(@NonNull View itemView, ImageView Friend_profileImg, TextView Friend_name, TextView email, LinearLayout friendsList) {
            super(itemView);
            this.Friend_profileImg = Friend_profileImg;
            this.Friend_name = Friend_name;
            this.FriendEmail = email;
            this.friendsList = friendsList;
        }
    }

    @NonNull
    @Override
    public Friend_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_friend_list, parent, false);
        return new FeedFriendList.Friend_View_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Friend_View_Holder holder, int position) {
        holder.Friend_name.setText(Friend_names.get(position));
        holder.FriendEmail.setText(Friend_emails.get(position));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        storageReference.child(Friend_emails.get(position)+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.Friend_profileImg);
            }
        });
        holder.friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("RestrictedApi") Intent intent = new Intent(getApplicationContext(), ProfileViewer.class);
                intent.putExtra("Email", (holder.FriendEmail.getText().toString()));
                intent.putExtra("Name", holder.Friend_name.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return Friend_names.size();
    }


}
