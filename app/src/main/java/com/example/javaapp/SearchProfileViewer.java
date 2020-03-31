package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SearchProfileViewer extends AppCompatActivity {
    ImageView FriendProfilePic;
    TextView FriendFullName,Followers, Followings, Postcount, EmailT, Foll;
    RecyclerView PersonalFeeds;
    StorageReference fStorage;
    DatabaseReference fDatabase;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile_viewer);
        Intent intent = getIntent();
        final String UEmail = intent.getStringExtra("SelfEmail");
        final String DOB = intent.getStringExtra("DOB");
        final String Email = intent.getStringExtra("Email");
        final String Name = intent.getStringExtra("Name");
        final String Status1 = "Follow";
        final String Status2 = "Following";

        EmailT = findViewById(R.id.profile_viewer_email);
        FriendProfilePic = findViewById(R.id.feed_prof_pic);
        FriendFullName = findViewById(R.id.profile_viewer_name);
        Postcount = findViewById(R.id.friend_post_count);
        Followers = findViewById(R.id.friend_follower_count);
        Followings = findViewById(R.id.friend_following_count);
        Foll = findViewById(R.id.follow_status);
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance().getReference("profileImages");
        final DatabaseReference friendexist = FirebaseDatabase.getInstance().getReference("Friends_list");

        EmailT.setText(String.valueOf(Email));
        FriendFullName.setText(String.valueOf(Name));

        friendexist.child(String.valueOf(UEmail.hashCode())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int Umax = 0;
                for(DataSnapshot DS: dataSnapshot.getChildren()){
                    if(Email == DS.child("Email").getValue(String.class)){
                        Umax = 1;
                        break;
                    }
                }
                if(Umax == 1){
                    Foll.setText(Status2);
                }
                else{
                    Foll.setText(Status1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Foll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Foll.getText().toString() == Status2){
                    final Query Dx = friendexist.child(String.valueOf(UEmail.hashCode())).orderByChild("Email").equalTo(Email);
                    Dx.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot DS:dataSnapshot.getChildren()){
                                Dx.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Foll.setText(Status1);
                }
                else{
                    final HashMap<String, String> friend = new HashMap<>();
                    friend.put("FullName", Name);
                    friend.put("Email", Email);
                    friend.put("DOB", DOB);
                    FirebaseDatabase.getInstance().getReference("Friends_list").child(String.valueOf(Email.hashCode())).push().setValue(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("Popup was Clicked", "I clicked here");
                        }
                    });
                    Foll.setText(Status2);
                }
            }
        });
    }
}
