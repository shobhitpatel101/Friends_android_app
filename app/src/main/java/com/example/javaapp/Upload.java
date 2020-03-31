package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Upload extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1889;
    EditText Description;
    StorageReference storageReference;
    ImageView userProfile, UploadImage;
    TextView username;
    Button UploadButton;
    private Uri filePath;
    FirebaseAuth fAuth;
    DatabaseReference fReference;
    DatabaseReference feedReference;
    String Email;
    String UName;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        UploadImage = findViewById(R.id.story_image);
        Description = findViewById(R.id.description);
        UploadButton = findViewById(R.id.UploadButton);
        userProfile = findViewById(R.id.profile_image_feed_uploader);
        username = findViewById(R.id.profile_name);
        fAuth = FirebaseAuth.getInstance();
        final String userId = fAuth.getUid();
        fReference = FirebaseDatabase.getInstance().getReference("users");
        feedReference = FirebaseDatabase.getInstance().getReference("Feeds");
        storageReference = FirebaseStorage.getInstance().getReference("Feeds");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        final LocalDateTime currentDate = LocalDateTime.now();
        final String formatDateTime = currentDate.format(format);
        fReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Email = dataSnapshot.child("Email").getValue(String.class);
                UName = dataSnapshot.child("FullName").getValue(String.class);
                username.setText(UName);
                StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("profileImages");
                storageReference1.child(Email+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(Upload.this).load(uri).placeholder(R.mipmap.ic_launcher_round).into(userProfile);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HashMap<String, String> feedMAP;
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    String Des = Description.getText().toString().trim();
                    String Name = username.getText().toString().trim();
                    String fileName = formatDateTime;

                    FeedHelper information = new FeedHelper(Des, Email, Name, fileName);
                    feedReference.child(String.valueOf(Email.hashCode())).push().setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                    final ProgressDialog progressDialog = new ProgressDialog(Upload.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    StorageReference ref = storageReference.child(Email).child(((formatDateTime.hashCode())+".jpeg").toString());
                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Upload.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Upload.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");

                        }
                    });
                    Intent i = new Intent(Upload.this, feeds.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public void SelectImage(View view) {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                UploadImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
