package com.example.projectlab;

import static android.content.ContentValues.TAG;

import static com.example.projectlab.User_Home.DEFAULT_UPDATE_INTERVAL;
import static com.example.projectlab.User_Home.FAST_UPDATE_INTERVAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Accident extends AppCompatActivity {
    TextView txt_name, txt_blood, txt_phone, txt_address, txt_email, txt_hospital;
    String str_name, str_blood, str_phone, str_address, str_email, str_hospital;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String userId;
    LocationManager locationManager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(User_Home.notificationId);

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

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            double Clat = new Double(location.getLatitude()) ;
                            double Clong = new Double(location.getLongitude());

                            ArrayList<Double> arrayList = new ArrayList<>();
                            fstore.collection("HOSPITALS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){

                                        for (QueryDocumentSnapshot document : task.getResult()){

                                            double Hlatitude = document.getDouble("latitude");
                                            double Hlongitude = document.getDouble("longitude");
                                            arrayList.add( Hlatitude);
                                            arrayList.add( Hlongitude);
                                        }
                                        Log.d(TAG, "LATITUDE AND LONGITUDE : " +arrayList);

                                        double aLat = 0,aLong = 0,dLat=0,dLong=0,blat =0, blong=0;
                                        ArrayList<Double> DistList = new ArrayList<>();

                                        for (int i =0 ; i<arrayList.size() ; i= i+2) {

                                            double R = 6371; // Radius of the earth
                                            aLat = arrayList.get(i);
                                            aLong = arrayList.get(i+1);

                                            Log.d(TAG, "arraylist 1  : " +aLat);
                                            Log.d(TAG, "arraylist 2  : " +aLong);

                                            dLat = Math.toRadians(Clat - aLat);
                                            dLong = Math.toRadians(Clong - aLong);

                                            Log.d(TAG, "Difference lat : " +dLat);
                                            Log.d(TAG, "Difference long  : " +dLong);

                                            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                                    Math.cos(Math.toRadians(aLat)) * Math.cos(Math.toRadians(Clat)) *
                                                            Math.sin(dLong / 2) * Math.sin(dLong / 2);

                                            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                            double d = R * c; // Distance in km

                                            DistList.add(d);

                                            Log.d(TAG, "a : " +a);
                                            Log.d(TAG, "c  : " +c);
                                            Log.d(TAG, "Distance in km  : " +d);

                                        }
                                        Log.d(TAG,"Final distances in kms :" +DistList);
                                    }else {
                                        Log.d(TAG,"Error getting documents:",task.getException());
                                    }
                                }
                            });
                        }
                    }
                });



//retrieving and storing in arraylist

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

                                /*
                                //hospital phone number
                                fstore.collection("HOSPITALS")
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
                                                        String message = "ALERT !! AN accident has occurred on http://maps.google.com/?q=" + lat + "," + lon + " , persons details are -"+ " Name: " +str_name ;
                                                        String message2 =  "Blood type : " +str_blood +" Address : "+str_address;
                                                        smsManager.sendTextMessage(str_hospital,null,message,null,null);
                                                        smsManager.sendTextMessage(str_hospital,null,message2,null,null);
                                                    }
                                                }
                                            }
                                        }); */
                            }
                        }
                    }
                });

    }

}