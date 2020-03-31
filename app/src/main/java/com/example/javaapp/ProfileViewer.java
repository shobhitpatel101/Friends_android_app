package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProfileViewer extends AppCompatActivity {
    ImageView FriendProfilePic;
    TextView FriendFullName,Followers, Followings, Postcount, EmailT;
    RecyclerView PersonalFeeds;
    StorageReference fStorage;
    DatabaseReference fDatabase;
    FirebaseAuth fAuth;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        Intent intent = getIntent();
        String Email = intent.getStringExtra("Email");
        String Name = intent.getStringExtra("Name");


        PersonalFeeds = findViewById(R.id.profile_viewer_feeds);
        LinearLayoutManager llm = new LinearLayoutManager((this));
        PersonalFeeds.setLayoutManager(llm);
        PersonalFeeds.setHasFixedSize(true);

        EmailT = findViewById(R.id.profile_viewer_email);
        FriendProfilePic = findViewById(R.id.feed_prof_pic);
        FriendFullName = findViewById(R.id.profile_viewer_name);
        Postcount = findViewById(R.id.friend_post_count);
        Followers = findViewById(R.id.friend_follower_count);
        Followings = findViewById(R.id.friend_following_count);
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance().getReference("profileImages");

        FriendFullName.setText(Name);
        EmailT.setText(String.valueOf(Email));
        FriendFullName.setText(String.valueOf(Name));
        fStorage.child(Email + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileViewer.this).load(uri).placeholder(R.mipmap.ic_launcher_round).into(FriendProfilePic);
            }
        });
        setPFeeds(Email);
    }
    private void setPFeeds(final String Email){
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        final LocalDateTime currentDate = LocalDateTime.now();
        final HashMap<Long, LocalDateTime> map1 = new HashMap<>();
        final HashMap<LocalDateTime, String> Description = new HashMap<>();
        final HashMap<LocalDateTime, String> Name = new HashMap<>();
        final ArrayList<String> RecyclerDescription = new ArrayList<>();
        final ArrayList<String> RecyclerTime = new ArrayList<>();
        final ArrayList<String> RecyclerName = new ArrayList<>();

        if(fAuth.getCurrentUser().getUid()!= null) {
            fDatabase = FirebaseDatabase.getInstance().getReference("Feeds");
            DatabaseReference fDB = FirebaseDatabase.getInstance().getReference("Friends_list");
            fDB.child(fAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Followings.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            fDatabase.child(String.valueOf(Email.hashCode())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Postcount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        LocalDateTime postDate;
                        System.out.println("Loops goes: ");
                        String date = DS.child("Time").getValue(String.class);
                        postDate = LocalDateTime.parse(date, format);
                        long diffInMillies = postDate.toEpochSecond(ZoneOffset.UTC) - currentDate.toEpochSecond(ZoneOffset.UTC);
                        map1.put(diffInMillies, postDate);
                        Description.put(postDate, DS.child("Description").getValue(String.class));
                        Name.put(postDate, DS.child("Username").getValue(String.class));
                    }
                    ArrayList<Long> sortedKeys = new ArrayList<Long>(map1.keySet());
                    Collections.sort(sortedKeys);
                    for (Long l : sortedKeys) {
                        RecyclerDescription.add(Description.get(map1.get(l)));
                        RecyclerTime.add((map1.get(l)).format(format));
                        RecyclerName.add(Name.get(map1.get(l)));
                    }
                    PersonalFeedViewer PersonalFeedsList = new PersonalFeedViewer(RecyclerDescription, RecyclerTime, RecyclerName, Email, ProfileViewer.this);
                    PersonalFeeds.setAdapter(PersonalFeedsList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
