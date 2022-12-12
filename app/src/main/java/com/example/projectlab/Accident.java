package com.example.projectlab;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Accident extends AppCompatActivity {
    TextView txt_name,txt_blood,txt_phone,txt_address,txt_email,txt_hospital;
    String str_name,str_blood,str_phone,str_address,str_email,str_hospital;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;

    String userId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);

        txt_name = findViewById(R.id.tv_name);
        txt_blood = findViewById(R.id.tv_blood);
        txt_phone = findViewById(R.id.tv_phone);
        txt_address = findViewById(R.id.tv_address);
        txt_email = findViewById(R.id.tv_email);
        txt_hospital = findViewById(R.id.tv_5);


        Auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        //retrieving
        fstore.collection("USERS")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot != null && documentSnapshot.exists()){
                                str_name = documentSnapshot.getString("name");
                                str_blood = documentSnapshot.getString("blood");
                                str_phone = documentSnapshot.getString("phone");
                                str_address = documentSnapshot.getString("address");
                                str_email = documentSnapshot.getString("email");


                                txt_name.setText(str_name);
                                txt_blood.setText(str_blood);
                                txt_phone.setText(str_phone);
                                txt_address.setText(str_address);
                                txt_email.setText(str_email);

                                //hospital phone number
                                fstore.collection("USERS")
                                        .whereEqualTo("confirm","confirm")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                        //send sms on accident
                                                        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
                                                        String lat = sharedPreferences.getString("value1","");
                                                        String lon = sharedPreferences.getString("value2","");
                                                        str_hospital = documentSnapshot.getString("phone");
                                                        txt_hospital.setText(str_hospital);

                                                        SmsManager smsManager = SmsManager.getDefault();
                                                        String message = "ALERT !! AN accident has occured on http://maps.google.com/?q=" + lat + "," + lon + " , persons details are -"+ " Name: " +str_name ;
                                                        String message2 =  "Blood type : " +str_blood +" Address : "+str_address;
                                                        smsManager.sendTextMessage(str_hospital,null,message,null,null);
                                                        smsManager.sendTextMessage(str_hospital,null,message2,null,null);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

    }
}