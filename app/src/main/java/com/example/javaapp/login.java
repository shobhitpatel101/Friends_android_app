package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private static String TAG;
    private EditText email, password;
    private FirebaseAuth Auth1;
    private TextView frgt;
    private ProgressBar progressBar;
    private Button btn, rgstr;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.eml_login);
        password = findViewById(R.id.pass_login);
        btn = findViewById(R.id.button);
        Auth1 = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);
        rgstr = findViewById(R.id.Register_btn);
        frgt = findViewById(R.id.Fgt_psd);
        if (Auth1.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Main1.class));
            finish();
        }
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String pass1 = password.getText().toString().trim();
                if (TextUtils.isEmpty(mail)) {
                    email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(pass1)) {
                    password.setError("Password is Required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                Auth1.signInWithEmailAndPassword(mail, pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), feeds.class));
                        } else {
                            Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        rgstr.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), signup.class));
            }
        });
        frgt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText rgstr_ml = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password !.");
                passwordResetDialog.setMessage("Enter Your Registed Email to get Reset Password Link");
                passwordResetDialog.setView(rgstr_ml);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = rgstr_ml.getText().toString();
                        Auth1.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this, "Reset Link sent to Registered Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, "Email entered Does not exist" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(login.this, "Email entered Does not exist" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }
}
