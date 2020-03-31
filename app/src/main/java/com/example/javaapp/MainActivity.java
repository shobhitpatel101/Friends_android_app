package com.example.javaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    public TextView t1;
    public String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        str = "Friends ";
        t1 = (TextView) findViewById(R.id.text_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                t1.setVisibility(View.VISIBLE);
                for(int i=0;i<str.length()-1;i++){
                    t1.setText(t1.getText() + str.substring(i, i + 1) + " ");
                }
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(MainActivity.this, login.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}
