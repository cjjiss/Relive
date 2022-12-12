package com.example.projectlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class User_Registration extends AppCompatActivity {
    EditText e1,e2,e3,e4,e5,e6,e7;
    Button b1;
    TextView t1;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String UserID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        e1 = (EditText) findViewById(R.id.ed_nameregu);
        e2 = (EditText) findViewById(R.id.ed_bloodregu);
        e3 = (EditText) findViewById(R.id.ed_phoneregu);
        e4 = (EditText) findViewById(R.id.ed_addressregu);
        e5 = (EditText) findViewById(R.id.ed_emailregu);
        e6 = (EditText) findViewById(R.id.ed_passregu);
        e7 = (EditText) findViewById(R.id.ed_cpassregu);

        b1 = (Button) findViewById(R.id.b4_regu);
        t1 = (TextView) findViewById(R.id.textView2);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(User_Registration.this,User_Login.class);
                startActivity(i);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = e1.getText().toString().trim();
                String blood = e2.getText().toString().trim();
                String phone = e3.getText().toString().trim();
                String address = e4.getText().toString().trim();
                String email = e5.getText().toString().trim();
                String pass =  e6.getText().toString().trim();
                String cpass = e7.getText().toString().trim();

                //validation

                if (name.isEmpty())
                {
                    e1.setError("Name Required");
                }
                if(blood.isEmpty())
                {
                    e2.setError("Blood Group Required");
                }
                if(phone.isEmpty())
                {
                    e3.setError("Phone no. Required");
                }
                if(address.isEmpty())
                {
                    e4.setError("Address Required");
                }
                if(email.isEmpty())
                {
                    e5.setError("Email Required");
                }
                if (!pass.equals(cpass))
                {
                    e7.setError("Password does not match");
                    return;
                }

                auth.createUserWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {

                                    UserData obj = new UserData(name,blood,phone,address,email);
                                    UserID = auth.getCurrentUser().getUid();
                                    Task<Void> documentReference = firestore.collection("USERS").document(UserID).set(obj);

                                    Toast.makeText(User_Registration.this,"User successfully Registered",Toast.LENGTH_SHORT).show();
                                    Intent i2 = new Intent(User_Registration.this,User_Login.class);
                                    startActivity(i2);
                                }
                                else
                                {
                                    Toast.makeText(User_Registration.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(User_Registration.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}