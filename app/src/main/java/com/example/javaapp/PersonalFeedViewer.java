package com.example.javaapp;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

public class PersonalFeedViewer extends RecyclerView.Adapter<PersonalFeedViewer.PersonalFeedHolder> {
    ArrayList<String> feedsDes;
    ArrayList<String>feedTime;
    ArrayList<String>feedName;
    String Email;
    Context context;

    public PersonalFeedViewer(ArrayList<String> feedsDes, ArrayList<String> feedTime, ArrayList<String> feedName, String email, Context context) {
        this.feedsDes = feedsDes;
        this.feedTime = feedTime;
        this.feedName = feedName;
        this.Email = email;
        this.context = context;
    }

    class PersonalFeedHolder extends RecyclerView.ViewHolder{
        TextView Description, PostTime, Likes;
        ImageView Image, Prof;

        public PersonalFeedHolder(@NonNull View itemView) {
            super(itemView);
            Description = itemView.findViewById(R.id.post_description);
            PostTime = itemView.findViewById(R.id.post_at_time);
            Likes = itemView.findViewById(R.id.post_like_count);
            Image = itemView.findViewById(R.id.post_image);
            Prof = itemView.findViewById(R.id.PostersProfilePic);
        }
    }

    @NonNull
    @Override
    public PersonalFeedViewer.PersonalFeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendsfeeds, parent, false);
        return new PersonalFeedViewer.PersonalFeedHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PersonalFeedHolder holder, int position) {
        holder.Description.setText(feedsDes.get(position));
        holder.PostTime.setText(" at "+feedTime.get(position));
        //holder.Uploader.setText(feedName.get(position));
        holder.Likes.setText("0");
        StorageReference storageReference2 = FirebaseStorage.getInstance().getReference();
        storageReference2.child("profileImages").child(this.Email + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.Prof);
            }
        });



        storageReference2.child("Feeds").child(this.Email).child((feedTime.get(position).hashCode()+".jpeg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri.toString());
                Glide.with(context).load(uri).into(holder.Image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.feedsDes.size();
    }



}
