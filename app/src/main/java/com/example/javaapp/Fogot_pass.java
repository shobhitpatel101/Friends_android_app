package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Fogot_pass extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText rgstr_ml = findViewById(R.id.rgst_ml);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_pass);
        String mail = rgstr_ml.getText().toString();
        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Fogot_pass.this, "Reset Link sent to Registered Email", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Fogot_pass.this, "Email entered Does not exist"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
