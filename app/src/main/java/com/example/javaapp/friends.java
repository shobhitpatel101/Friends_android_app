package com.example.javaapp;
//if request.auth != null
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class friends extends AppCompatActivity {

    private EditText mSearchField;
    private FirebaseUser fuser;
    private RecyclerView mResultList,Friends;
    ArrayList<String>fulllist, name, Email_f;
    ArrayList<String>Emaillist;
    ArrayList<String>prof_ilist;
    SearchAdapter searchAdapter;
    FriendAdapter friend_list;
    final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private String pro_url;
    StorageReference storageReference;
    private DatabaseReference mUserDatabase, fDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Intent intent = getIntent();
        final String Email = intent.getStringExtra("Email");
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");
        fDB = FirebaseDatabase.getInstance().getReference("Friends_list");
        storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mSearchField = findViewById(R.id.search_field);

        mResultList = findViewById(R.id.result_list);
        LinearLayoutManager llm = new LinearLayoutManager((this));
        mResultList.setLayoutManager(llm);
        mResultList.setHasFixedSize(true);
        //mResultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Friends = findViewById(R.id.friends_list);
        Friends.setHasFixedSize(true);
        Friends.setLayoutManager(new LinearLayoutManager(this));
        Friends.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));




        fulllist = new ArrayList<>();
        Emaillist = new ArrayList<>();
        prof_ilist = new ArrayList<>();
        name = new ArrayList<>();
        Email_f = new ArrayList<>();
        final String uID = fuser.getUid();
        setAdapter2(uID);

        mSearchField.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    fulllist.clear();
                    Emaillist.clear();
                    prof_ilist.clear();
                    //setAdapter1(s.toString(), Email);
                    setAdapter2(uID);
                }
                else{
                    name.clear();
                    Email_f.clear();
                    setAdapter1(s.toString(), Email);
                }
           }
       });

    }

    private void setAdapter2(final String uID) {
        fDB.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.clear();
                Email_f.clear();
                Friends.removeAllViews();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String full_name = snapshot.child("FullName").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    name.add(full_name);
                    Email_f.add(email);
                }

                friend_list = new FriendAdapter(friends.this, name, Email_f);
                Friends.setAdapter(friend_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter1(final String toSearch, final String Email) {

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fulllist.clear();
                Emaillist.clear();
                prof_ilist.clear();
                mResultList.removeAllViews();
                int count = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String uid = snapshot.getKey();
                    String full_name = snapshot.child("FullName").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String date = snapshot.child("DOB").getValue(String.class);
                    if(full_name.toLowerCase().contains(toSearch)){
                        fulllist.add(full_name);
                        Emaillist.add(email);
                        prof_ilist.add(date);
                        count+=1;
                    }
                    else if(email.toLowerCase().contains(toSearch)){
                        fulllist.add(full_name);
                        Emaillist.add(email);
                        prof_ilist.add(date);
                        count+=1;
                    }
                    if(count == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(friends.this, fulllist, Emaillist, prof_ilist, Email);
                mResultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
