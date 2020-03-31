package com.example.javaapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class feeds extends AppCompatActivity {

    FirebaseUser user;
    private RecyclerView Feed_Friends, Feed_list;
    FeedFriendList friend_list;
    FeedAdapter feedAdapter;
    FirebaseAuth fAuth;
    DatabaseReference database, friendsDB, feedsDB;
    ImageView profile_img, upload;
    TextView User_name, eml;
    ProgressBar p1;


    ArrayList<String>Friend_names, Friend_emails, feedsName, feedsTime, feedData, feedDescription;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        database = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getUid();
        profile_img = findViewById(R.id.profil_image_feed);
        upload = findViewById(R.id.upload_your_story);
        eml = findViewById(R.id.profile_eml);
        p1 = (ProgressBar) findViewById(R.id.feed_progress);
        User_name = findViewById(R.id.profile_name);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");
        friendsDB = FirebaseDatabase.getInstance().getReference("Friends_list");;
        feedsDB = FirebaseDatabase.getInstance().getReference("Feeds");
        //Setting Recycler View
        Feed_Friends = findViewById(R.id.friends_list_horizontal);
        Feed_Friends.setHasFixedSize(true);
        Feed_Friends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Feed_Friends.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

        Feed_list = findViewById(R.id.feed_list);
        Feed_list.setHasFixedSize(true);
        Feed_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //Feed_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        Friend_names = new ArrayList<>();
        Friend_emails = new ArrayList<>();
        feedsName = new ArrayList<>();
        feedsTime = new ArrayList<>();
        feedData = new ArrayList<>();
        final String userId = fAuth.getUid();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Upload.class));
            }
        });

        database.child(fAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uname = dataSnapshot.child("FullName").getValue(String.class);

                User_name.setText(capital_uname(uname));
                String email = dataSnapshot.child("Email").getValue(String.class);
                eml.setText(email);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
                storageReference.child(email+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(feeds.this).load(uri).placeholder(R.mipmap.ic_launcher_round).into(profile_img);
                    }
                });
                p1.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setFriends(userId);
        setFeeds(userId);
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Main1.class));
            }
        });
    }
    private void setFriends(final String uID) {
        friendsDB.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Friend_names.clear();
                Friend_emails.clear();
                Feed_Friends.removeAllViews();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String full_name = snapshot.child("FullName").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    Friend_names.add(full_name);
                    Friend_emails.add(email);
                }
                friend_list = new FeedFriendList(feeds.this, Friend_names, Friend_emails);
                Feed_Friends.setAdapter(friend_list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setFeeds(final String uid){
        final Set<String> Emails = new HashSet<String>();
        final HashMap<Long, LocalDateTime> map1 = new HashMap<>();
        final HashMap<LocalDateTime, String> Description = new HashMap<>();
        final HashMap<LocalDateTime, String> Name = new HashMap<>();
        final ArrayList<String> RecyclerDescription = new ArrayList<>();
        final ArrayList<String> RecyclerTime = new ArrayList<>();
        final ArrayList<String> RecyclerName = new ArrayList<>();
        final ArrayList<String> RecyclerEmail = new ArrayList<>();
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        final LocalDateTime currentDate = LocalDateTime.now();
        friendsDB.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DS: dataSnapshot.getChildren()) {
                    String eml = DS.child("Email").getValue(String.class);
                    Emails.add(eml);
                }
                for(final String eml:Emails){
                    feedsDB.child(String.valueOf(eml.hashCode())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot DS : dataSnapshot.getChildren()) {
                                LocalDateTime postDate;
                                RecyclerEmail.add(eml);
                                String date = DS.child("Time").getValue(String.class);
                                postDate = LocalDateTime.parse(date, format);
                                long diffInMillies = postDate.toEpochSecond(ZoneOffset.UTC) - currentDate.toEpochSecond(ZoneOffset.UTC);
                                map1.put(diffInMillies, postDate);
                                Description.put(postDate, DS.child("Description").getValue(String.class));
                                Name.put(postDate, DS.child("Username").getValue(String.class));
                            }
                            ArrayList<Long> sortedKeys = new ArrayList<Long>(map1.keySet());

                            Collections.sort(sortedKeys);
                            System.out.println(sortedKeys.size());
                            for (Long l : sortedKeys) {
                                RecyclerDescription.add(Description.get(map1.get(l)));
                                RecyclerTime.add((map1.get(l)).format(format));
                                RecyclerName.add(Name.get(map1.get(l)));
                            }
                            FeedAdapter fx = new FeedAdapter(feeds.this, RecyclerDescription, RecyclerTime, RecyclerEmail, RecyclerName);
                            Feed_list.setAdapter(fx);
                            //System.out.println("Post Count: "+RecyclerDescription.size()+ "Post Count: "+RecyclerEmail.size()+"Post Count: "+RecyclerName.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for(int i = 0; i < Friend_emails.size(); i++) {
            final int finalI = i;
            feedsDB.child(Friend_emails.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot DS: dataSnapshot.getChildren()) {
                        feedData.add(DS.child("URL").getValue(String.class));
                        feedsTime.add(DS.child("TimeStamp").getValue(String.class));
                        feedDescription.add(DS.child("Description").getValue(String.class));
                        feedsName.add(Friend_names.get(finalI));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            feedAdapter = new FeedAdapter(feeds.this, feedDescription, feedsTime, feedData, feedsName);
            Feed_list.setAdapter(feedAdapter);
        }
    }

    public String capital_uname(String uname){
        String arr[] = uname.split(" ");
        uname = arr[0].substring(0,1).toUpperCase() + arr[0].substring(1,arr[0].length());
        if(arr.length > 1) {
            uname = uname + " " + arr[1].substring(0, 1).toUpperCase() + ".";
        }
        return uname;
    }
}