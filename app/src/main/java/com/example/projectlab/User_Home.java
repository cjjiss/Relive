package com.example.projectlab;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class User_Home extends AppCompatActivity implements SensorEventListener {
    private int SMS_PERMISSION_CODE = 1;
    Button b1;
    Switch aSwitch,secsw;
    SensorManager sensorManager;
    Sensor sensor;
    double accelerationX, accelerationY, accelerationZ, a, G, speeddouble;
    int threshold = 4;
    TextView tv1, tv2;


    //gps
    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 2;
    public static final int PERMISSION_FINE_LOCATION = 99;
    Boolean updateOn = false;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    String speed, address,lat,lon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        b1 = findViewById(R.id.button);
        aSwitch = findViewById(R.id.switch1);
        tv1 = findViewById(R.id.textView5);
        tv2 = findViewById(R.id.textView6);


        //accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(User_Home.this, sensor, sensorManager.SENSOR_DELAY_NORMAL);

        //gps

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateUIValue(location);
            }
        };

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    //turn on
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }

        });

        updateGPS();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                   // aSwitch.setText("enabled");

                    //permission already granted
                    if (ContextCompat.checkSelfPermission(User_Home.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(User_Home.this, "Permssion already granted!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requestSmsPermission();
                    }

                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(User_Home.this,Accident.class);
                            startActivity(i);
                        }
                    });
                }
                else {
                    //aSwitch.setText("not enabled");

                }
            }
        });
    }

    private void stopLocationUpdates() {
        tv1.setText("stopped");
        tv2.setText("stopped");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();

    }
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(User_Home.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValue(location);

                }
            });
        }else{
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }

    private void updateUIValue(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        if (location.hasSpeed()) {
            speed = String.valueOf(location.getSpeed());
            speeddouble = Double.valueOf(location.getSpeed());
            tv1.setText(speed);

        }else{
            speed="not available";
        }
        Geocoder geocoder=new Geocoder(User_Home.this);
        try {
            List<Address>addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            address=(addresses.get(0).getAddressLine(0));
            tv2.setText(address);
        }catch (Exception e){
            address="unable to get the street address";

        }
    }

    private void requestSmsPermission(){
        //explains why we need this permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to enable the emergency alert in case of any occurrence of accident")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(User_Home.this,new String[]{Manifest.permission.SEND_SMS},SMS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            //dont have to show
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                    if (requestCode == SMS_PERMISSION_CODE) {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "this app requires permission to be granted", Toast.LENGTH_SHORT).show();
                    finish();
                }

        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG,"X " +sensorEvent.values[0] +"Y "+ sensorEvent.values[1] + "Z "+ sensorEvent.values[2] );

        //calculating gforce

        accelerationX =   sensorEvent.values[0];
        accelerationY =   sensorEvent.values[1];
        accelerationZ =   sensorEvent.values[2];


        a = (Math.sqrt(accelerationX*accelerationX + accelerationY*accelerationY + accelerationZ*accelerationZ));

        G = a/9.81;

        Log.e(TAG, "onSensorChanged: " +G );


        /*** Detect Accident ***/
        if (G > threshold && speeddouble > 0.0001 ) {
            Toast.makeText(this, "Accident occured", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("value1", lat);
            editor.putString("value2",lon);
            editor.apply();

            sensorManager.unregisterListener(this);
            Intent i = new Intent(User_Home.this,Accident.class);
            startActivity(i);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}