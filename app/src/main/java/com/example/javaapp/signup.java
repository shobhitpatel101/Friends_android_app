package com.example.javaapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class signup extends AppCompatActivity {
    private EditText Fullname, email, password, password2, phone;
    private ImageView dob;
    private String gend;
    private TextView dob_t;
    private RadioGroup rg;
    DatabaseReference mreference;
    private ProgressBar progressBar;
    private Button btn,gnddr;
    FirebaseAuth fAuth;
    String userid;
    private static final String TAG = "signup";
    private DatePickerDialog.OnDateSetListener mdl;
    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Fullname = (EditText) findViewById(R.id.Fname);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.pass1);
        password2 = (EditText)findViewById(R.id.pass2);
        phone = (EditText)findViewById(R.id.phn);
        dob = (ImageView)findViewById(R.id.dob);
        btn = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
        dob_t = (TextView) findViewById(R.id.dob_t);

        mreference = FirebaseDatabase.getInstance().getReference("users");
        fAuth = FirebaseAuth.getInstance();
        rg = findViewById(R.id.gndr);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup Group, int checkedid) {
                RadioButton gender = findViewById(checkedid);
                gend = gender.getText().toString();

            }
        });
        dob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(signup.this, "Select DOB", Toast.LENGTH_SHORT).show();
                Calendar c = Calendar.getInstance();
                int day = c.get(DAY_OF_MONTH);
                int month = c.get(MONTH);
                int year = c.get(YEAR);
                DatePickerDialog date = new DatePickerDialog(signup.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth, mdl, year, month, day);
                date.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                date.show();
            }
        });

        mdl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                Log.d(TAG, "onDateSet: mm/dd/yy: " + month + "/" + day + "/" + year);
                String dat = month + "/" + day + "/" + year;
                dob_t.setText(dat);
                }
            };

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String lemail = email.getText().toString().trim();
                final String password1 = password.getText().toString().trim();
                final String fullname = Fullname.getText().toString().trim();
                final String password21 = password2.getText().toString().trim();
                final String phn = phone.getText().toString().trim();
                final String date = dob_t.getText().toString().trim();
                if (fAuth.getCurrentUser() != null) {
                        startActivity(new Intent(getApplicationContext(), Main1.class));
                        finish();
                }

                if (TextUtils.isEmpty(lemail)) {
                    email.setError("Email is Mandatory");
                    return;
                }
                if (TextUtils.isEmpty(password1)) {
                    password.setError("Password is Required");
                    return;
                }
                if(TextUtils.isEmpty(phn)){
                    phone.setError("Mobile Number is Required");
                }

                if(TextUtils.isEmpty(date)){
                    dob_t.setError("Enter Valid DOB");
                }

                if (password1.length() < 6) {
                    password.setError("Password Length Must be greater than 6");
                    return;
                }
                if (!(password1.equals(password21))) {
                    password2.setError("Password Does not Match");
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(lemail, password1).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Signup_helper information = new Signup_helper(fullname, lemail, date, gend, phn);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(signup.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), login.class));
                                    }
                                });
                            } else {
                                Toast.makeText(signup.this, "Error !.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
    }

}
