package com.example.projectlab;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class User_Home extends AppCompatActivity implements SensorEventListener  {
    private static final String CHANNEL_ID="CHANNELID";
    public static final int notificationId = 100;
    private int SMS_PERMISSION_CODE = 1;
    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaRecorder mediaRecorder;
    int amp ;
    double db;
    Button b1;
    Switch aSwitch,secsw;
    SensorManager sensorManager;
    Sensor sensor;
    double accelerationX, accelerationY, accelerationZ, a, G, speeddouble;
    int threshold = 4;
    TextView tv1, tv2;
    Location location;


    //gps
    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 2;
    public static final int PERMISSION_FINE_LOCATION = 99;
    Boolean updateOn = false;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    FirebaseFirestore fstore;

    String speed, address,lat,lon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        aSwitch = findViewById(R.id.switch1);
        tv1 = findViewById(R.id.textView5);
        tv2 = findViewById(R.id.textView6);
        fstore = FirebaseFirestore.getInstance();


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
                 location = locationResult.getLastLocation();
                updateUIValue(location);
            }
        };


        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    //turn on
                    if (isMicrophonePresent()) {
                        getMicrophonePresent();
                    }
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
        /*
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
            */
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new RecorderTask(mediaRecorder), 0, 1000);

            Toast.makeText(this, "Recording is started", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            e.printStackTrace();
        }

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

    private void LatandLong(Location location) {
        /*
        String str_lat = String.valueOf(location.getLatitude());
        String str_lon = String.valueOf(location.getLongitude());
        double lat = Double.parseDouble(str_lat);
        double lon = Double.parseDouble(str_lon);
        */
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

        Log.e(TAG, "Gforce: " +G );
        Log.d("Db value :", String.valueOf(+db));


        /*** Detect Accident ***/
        if (G > threshold && speeddouble > 0.01 && db > 75) {
            Toast.makeText(this, "Accident occured", Toast.LENGTH_SHORT).show();

            LatandLong(location);

            SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("value1", lat);
            editor.putString("value2",lon);
            editor.apply();

            sensorManager.unregisterListener(this);

            notification();

            Intent i = new Intent(User_Home.this,NotificationActivity.class);
            startActivity(i);

        }
    }

    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);


        sound();


        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("System Detected an ACCIDENT !!")
                .setContentText("Alert will be sent if not disabled withing 15 secs")

                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        builder.setVibrate(new long[] { });



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());

    }

    private void stop() {
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_ALARM);
        MediaPlayer mp = MediaPlayer. create (getApplicationContext(), alarmSound);
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
                       @Override
                       public void run() {
                           mp.stop();
                           t.cancel();
                       }
                   },5000

        );

    }

    private void sound() {
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_ALARM);
        MediaPlayer mp = MediaPlayer. create (getApplicationContext(), alarmSound);
        mp.start();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.stop();
                t.cancel();
            }
        },15000

        );

    }
    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicrophonePresent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }
    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile" + ".mp3");
        return file.getPath();
    }
    private class RecorderTask extends TimerTask {
        private MediaRecorder recorder;

        public RecorderTask(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            amp = recorder.getMaxAmplitude();

            db = 20 * Math.log10(amp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}