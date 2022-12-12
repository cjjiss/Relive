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

public class Hospital_Registration extends AppCompatActivity {
    EditText e1,e2,e3,e4,e5,e6;
    Button b1;
    TextView t1;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        e1 = (EditText) findViewById(R.id.ed_nameregh);
        e2 = (EditText) findViewById(R.id.ed_emailregh);
        e3 = (EditText) findViewById(R.id.ed_addregh);
        e4 = (EditText) findViewById(R.id.ed_phoneregh);
        e5 = (EditText) findViewById(R.id.ed_passregh);
        e6 = (EditText) findViewById(R.id.ed_cpassregh);

        b1 = (Button) findViewById(R.id.b5_regh);
        t1 = (TextView) findViewById(R.id.textView4);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Hospital_Registration.this,Hospital_login.class);
                startActivity(i);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameh = e1.getText().toString().trim();
                String emailh = e2.getText().toString().trim();
                String addressh = e3.getText().toString().trim();
                String phoneh = e4.getText().toString().trim();
                String passh = e5.getText().toString().trim();
                String cpassh =  e6.getText().toString().trim();

                //validation

                if (nameh.isEmpty())
                {
                    e1.setError("Name Required");
                }
                if(emailh.isEmpty())
                {
                    e2.setError("Email Required");
                }
                if(addressh.isEmpty())
                {
                    e3.setError("Address Required");
                }
                if(phoneh.isEmpty())
                {
                    e4.setError("Phone no. Required");
                }

                if (!passh.equals(cpassh))
                {
                    e6.setError("Password does not match");
                    return;
                }

                auth.createUserWithEmailAndPassword(emailh,passh)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    HospitalData hospitalData = new HospitalData(nameh,emailh,addressh,phoneh);
                                    firestore.collection("USERS").document(phoneh).set(hospitalData);
                                    Toast.makeText(Hospital_Registration.this,"Hospital successfully Registered",Toast.LENGTH_SHORT).show();
                                    Intent i2 = new Intent(Hospital_Registration.this,Hospital_login.class);
                                    startActivity(i2);
                                }
                                else
                                {
                                    Toast.makeText(Hospital_Registration.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Hospital_Registration.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}