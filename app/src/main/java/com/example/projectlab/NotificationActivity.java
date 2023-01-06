package com.example.projectlab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationActivity extends AppCompatActivity {

    Button alert,cancel;
    int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        alert = findViewById(R.id.button2);
        cancel = findViewById(R.id.button3);


        final Timer t = new Timer();
        t.schedule(new TimerTask() {
                       @Override
                       public void run() {

                         Intent ie = new Intent(NotificationActivity.this,Accident.class);
                         startActivity(ie);
                       }
                   },15000

        );



        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t != null)
                    t.cancel();
                Intent i = new Intent(NotificationActivity.this,Accident.class);
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t != null)
                    t.cancel();
                Intent i2 = new Intent(NotificationActivity.this,User_Home.class);
                startActivity(i2);
            }
        });


    }

}