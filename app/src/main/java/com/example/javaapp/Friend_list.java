package com.example.javaapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Friend_list extends RecyclerView.Adapter<Friend_list.FriendViewHolder> {
    Context context;
    ArrayList<String> name;
    ArrayList<String>Email_f;

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImg;
        private TextView fullname, email;
        
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            fullname = itemView.findViewById(R.id.name_text);
            email = itemView.findViewById(R.id.e_mail);
        }

        public FriendViewHolder(@NonNull View itemView, ImageView profileImg, TextView fullname, TextView email) {
            super(itemView);
            this.profileImg = profileImg;
            this.fullname = fullname;
            this.email = email;
        }
    }

    public Friend_list(Context context, ArrayList<String> name, ArrayList<String> email_f) {
        this.context = context;
        this.name = name;
        this.Email_f = email_f;
    }

    @NonNull
    @Override
    public Friend_list.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new Friend_list.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, int position) {
        holder.fullname.setText(name.get(position));
        holder.email.setText(Email_f.get(position));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        storageReference.child(Email_f.get(position)+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.profileImg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }
   
}