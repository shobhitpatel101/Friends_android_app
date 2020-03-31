package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main1 extends AppCompatActivity {
    TextView Name, Phone, DOB, Email,  name1, date1, name2, date2;
    FirebaseAuth fAuth;
    ImageView imageView3, img1, img2;
    Button btm;
    private Button friend_invoker;
    DatabaseReference fDatabase, mDatabase;
    String userid, email;
    int TAKE_ACTIVITY_IMAGE = 10001;
    private static final String TAG = "Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        friend_invoker = (Button)findViewById(R.id.more_friends);

        Name = findViewById(R.id.Name_dis);
        Phone = findViewById(R.id.phone_dis);

        imageView3 = findViewById(R.id.imageView3);
        DOB = findViewById(R.id.DOB_dis);
        btm = findViewById(R.id.Logout);

        img1 = findViewById(R.id.det1_img);
        name1 = findViewById(R.id.det1_name);
        date1 =findViewById(R.id.det1_date);

        img2 = findViewById(R.id.det2_img);
        name2 = findViewById(R.id.det2_name);
        date2 =findViewById(R.id.det2_date);

        Email = findViewById(R.id.email_dis);
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance().getReference("users");
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        mDatabase = FirebaseDatabase.getInstance().getReference("Friends_list");
        userid = fAuth.getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(imageView3);
        }
        //
        fDatabase.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("Email").getValue(String.class);
                Phone.setText(dataSnapshot.child("Phone").getValue(String.class));
                Name.setText(dataSnapshot.child("FullName").getValue(String.class));
                DOB.setText(dataSnapshot.child("DOB").getValue(String.class));
                Email.setText(dataSnapshot.child("Email").getValue(String.class));
                Log.d(TAG, "Email: " + email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final HashMap<Date, String> map = new HashMap<>();
        final HashMap<Long, Date>map2 = new HashMap<>();
        final HashMap<Date, String>map3 = new HashMap<>();
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final Date new1 = new Date();
        df.format((new1));
        final int[] size1 = {0};
        mDatabase.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot DS: dataSnapshot.getChildren()){
                    size1[0] +=1;
                    String bDate = DS.child("DOB").getValue(String.class);
                    try {
                        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(bDate);
                        date.setYear(new1.getYear());
                        long diffInMillies = (date.getTime() - new1.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        if(diffInMillies > 0){
                            map2.put(diff, date);

                            map.put(date, DS.child("FullName").getValue(String.class));
                            map3.put(date, DS.child("Email").getValue(String.class));

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<Long> sortedKeys = new ArrayList<Long>(map2.keySet());
                Collections.sort(sortedKeys);
                int size = sortedKeys.size();
                int i = 0;
                if(size >= 1) {
                    name1.setText(map.get(map2.get(sortedKeys.get(i))).toString());
                    date1.setText("Bday: "+df.format(map2.get(sortedKeys.get(i))).toString()+", ("+sortedKeys.get(i) +") days to go");
                    storageReference.child(map3.get(map2.get(sortedKeys.get(i))).toString()+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Main1.this).load(uri).placeholder(R.mipmap.ic_launcher_round).into(img2);
                        }
                    });
                    i += 1;
                }
                if(size >= 2) {
                    name2.setText( map.get(map2.get(sortedKeys.get(i))).toString() );
                    date2.setText("Bday: "+df.format(map2.get(sortedKeys.get(i))).toString()+", ("+sortedKeys.get(i) +") days to go");
                    storageReference.child(map3.get(map2.get(sortedKeys.get(i))).toString()+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Main1.this).load(uri).placeholder(R.mipmap.ic_launcher_round).into(img1);
                        }
                    });
                    i += 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

        friend_invoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), friends.class));
            }
        });

    }

    public void imgClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, TAKE_ACTIVITY_IMAGE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_ACTIVITY_IMAGE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    imageView3.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages").child(email+".jpeg");
        ref.putBytes(bs.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println(uri.toString());
                        Log.d(TAG, "onSuccess: "+uri);
                        setUserProfileUrl(uri);
                    }
                });
            }
            private  void setUserProfileUrl(Uri uri){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                        setPhotoUri(uri).build();
                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main1.this, "Profile Picture Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main1.this, "Profile image Upload Failed..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: ", e.getCause());
            }
        });
    }

    public void more_friends(View view) {
        Intent intent = new Intent(getApplicationContext(), friends.class);
        intent.putExtra("Email", (Email.getText().toString()));
        startActivity(intent);
    }

    public void linking_friend(View view) {
        /*final String uid = fAuth.getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("temp");
        HashMap<String, Integer> hs =new HashMap<>();
        hs.put("ADD", 0);
        dr.child("uid").setValue(hs).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, uid);
                startActivity(Intent.createChooser(intent,"via Share"));

            }
        });*/
        startActivity(new Intent(getApplicationContext(), feeds.class));
    }
}
