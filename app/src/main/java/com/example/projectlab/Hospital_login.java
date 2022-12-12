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

public class Hospital_login extends AppCompatActivity {
    EditText e1, e2;
    Button b1;
    TextView t1;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_login);

        e1 = (EditText) findViewById(R.id.ed_emaillogh);
        e2 = (EditText) findViewById(R.id.ed_passlogh);

        b1 = findViewById(R.id.b5_logh);

        mAuth = FirebaseAuth.getInstance();

        t1 = (TextView) findViewById(R.id.textView3);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Hospital_login.this, Hospital_Registration.class);
                startActivity(i);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailh = e1.getText().toString().trim();
                String passh = e2.getText().toString().trim();

                if (emailh.isEmpty())
                {
                    e1.setError("Email Required");
                }
                else if (passh.isEmpty())
                {
                    e2.setError("Password Required");
                }
                else {
                    mAuth.signInWithEmailAndPassword(emailh,passh).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Hospital_login.this,"Successfully Logged in",Toast.LENGTH_SHORT).show();
                                Intent i2 = new Intent(Hospital_login.this,Hospital_home.class);
                                startActivity(i2);
                            }
                            else{
                                Toast.makeText(Hospital_login.this,"Log in Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}