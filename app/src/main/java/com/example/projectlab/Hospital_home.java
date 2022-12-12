package com.example.projectlab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Hospital_home extends Accident {
    TextView name,blood,phone,address,email;
    Button b;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;

    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        name = findViewById(R.id.tv_name);
        blood = findViewById(R.id.tv_blood);
        phone = findViewById(R.id.tv_phone);
        address = findViewById(R.id.tv_address);
        email = findViewById(R.id.tv_email);
        b = findViewById(R.id.b_check);


        Auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userId = user.getUid();


        DocumentReference documentReference = fstore.collection("USERS").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("name"));
                blood.setText(value.getString("blood"));
                phone.setText(value.getString("phone"));
                address.setText(value.getString("address"));
                email.setText(value.getString("email"));

            }
        });



    }
}