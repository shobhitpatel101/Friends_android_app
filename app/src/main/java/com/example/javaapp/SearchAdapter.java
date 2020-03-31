package com.example.javaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    Context context;
    DatabaseReference mreference;
    ArrayList<String> fulllist;
    ArrayList<String>Emaillist;
    ArrayList<String>prof_ilist;
    FirebaseAuth fAuth;
    String SelfEmail;

    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        RelativeLayout rl;
        TextView fullname, email;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            fullname = itemView.findViewById(R.id.name_text);
            rl = itemView.findViewById(R.id.friend_adder);
            email = itemView.findViewById(R.id.e_mail);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> fulllist, ArrayList<String> emaillist, ArrayList<String> prof_ilist, String email) {
        this.context = context;
        this.fulllist = fulllist;
        this.Emaillist = emaillist;
        this.prof_ilist = prof_ilist;
        this.SelfEmail = email;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {
        holder.fullname.setText(fulllist.get(position));
        holder.email.setText(Emaillist.get(position));
        final String DOB = prof_ilist.get(position);
        final String fullname = holder.fullname.getText().toString().trim();
        final String email = holder.email.getText().toString().trim();
        mreference = FirebaseDatabase.getInstance().getReference("Friends_list");
        fAuth= FirebaseAuth.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        storageReference.child(email+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.profileImg);
            }
        });
        final String uid = fAuth.getCurrentUser().getUid();

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                @SuppressLint("RestrictedApi") Intent intent = new Intent(getApplicationContext(), SearchProfileViewer.class);
                intent.putExtra("Email",SelfEmail);
                intent.putExtra("FriendsEmail", (holder.email.getText().toString()));
                intent.putExtra("Name", holder.fullname.getText().toString());
                intent.putExtra("DOB", DOB);
                getApplicationContext().startActivity(intent);


            }
        });

    }


    @Override
    public int getItemCount() {
        return fulllist.size();
    }
}
