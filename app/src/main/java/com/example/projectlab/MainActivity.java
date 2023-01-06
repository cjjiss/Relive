package com.example.projectlab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//This is first page
public class MainActivity extends AppCompatActivity {
    ImageView v1,v2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        v1= findViewById(R.id.imageView);
        v2 = findViewById(R.id.imageView3);

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i = new Intent(MainActivity.this,User_Login.class);
                    startActivity(i);
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(MainActivity.this,Hospital_login.class);
                startActivity(i2);
            }
        });
    }
}