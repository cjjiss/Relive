package com.example.projectlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User_Login extends AppCompatActivity {
    EditText e1, e2;
    Button b1;
    TextView t1;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        e1 = findViewById(R.id.ed_emaillogu);
        e2 = findViewById(R.id.ed_passlogu);
        b1 = findViewById(R.id.b3_logu);

        mAuth = FirebaseAuth.getInstance();

        t1 =findViewById(R.id.textView);
        db = FirebaseFirestore.getInstance();

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(User_Login.this, User_Registration.class);
                startActivity(i);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = e1.getText().toString().trim();
                String pass = e2.getText().toString().trim();

                if (email.isEmpty())
                {
                    e1.setError("Email Required");
                }
                else if (pass.isEmpty())
                {
                    e2.setError("Password Required");
                }
                else {
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //IF EMAIL & PASSWORD MATCHES
                                db.collection("USERS")
                                        .whereEqualTo("email", email)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Intent intentStudent = new Intent(User_Login.this, User_Home.class);
                                                        startActivity(intentStudent);
                                                        finish();
                                                        Toast.makeText(User_Login.this, "SIGNED IN SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(User_Login.this, "FAILED!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                            else{
                                Toast.makeText(User_Login.this,"Log in Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
