package com.example.javaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    Context context;
    DatabaseReference mreference;
    ArrayList<String> FriendsNames;
    ArrayList<String>FriendsEmail;
    FirebaseAuth fAuth;



    class FriendViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        RelativeLayout rl;
        TextView fullname, email;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            fullname = itemView.findViewById(R.id.name_text);
            rl = itemView.findViewById(R.id.friend_adder);
            email = itemView.findViewById(R.id.e_mail);
        }
    }
    public FriendAdapter(Context context, ArrayList<String> name, ArrayList<String> email_f) {
        this.context = context;
        this.FriendsNames = name;
        this.FriendsEmail = email_f;
    }

    public FriendAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new FriendAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, int position) {
        holder.fullname.setText(FriendsNames.get(position));
        holder.email.setText(FriendsEmail.get(position));
        mreference = FirebaseDatabase.getInstance().getReference("Friends_list");
        fAuth= FirebaseAuth.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        storageReference.child(FriendsEmail.get(position)+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.profileImg);
            }
        });
    }



    @Override
    public int getItemCount() {
        return FriendsNames.size();
    }

}
