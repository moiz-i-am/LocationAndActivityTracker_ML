package com.example.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service implements SensorEventListener {

    private static final String NOTIFICATION_CHANNEL_ID = "227";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "Notification channel name";
    private static final int NOTIFICATION_ID = 499;
    private static final String NOTIFICATION_CHANNEL_DESC = "channel dec";
    Context context;
    TimerTask timerTask;
    Timer timer = new Timer();
    StringBuilder stringBuilder;
    BufferedWriter bufferedWriter;
    float[] accData, megData, gyrData;
    String loc;
    SensorManager sensorManager;
    Sensor megSensor, accSensor, gyrSensor;
    String message, storageState;
    Boolean externalStorageAvailable, externalStorageWriteable;
    File extfilepath, extfile;
    NotificationManager notificationManager;
    FileWriter fileWriter;
    String deviceId;

    LocationManager locationManager;
    LocationListener locationListener;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {

            startInForeground();
        }


        // gps start

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS_enable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPS_enable){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    try{

                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);

                        loc = addressList.get(0).getAddressLine(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        }

        // gps end




            stringBuilder = new StringBuilder();
        this.sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        accSensor = sensorManager.getDefaultSensor(1);
        megSensor = sensorManager.getDefaultSensor(2);
        gyrSensor = sensorManager.getDefaultSensor(4);
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        timerTask = new TimerTask() {
            public void run() {
                stringBuilder.setLength(0);
                try {
                    bufferedWriter.write(System.currentTimeMillis() + ";" + accData[0] + ";" + accData[1] + ";" + accData[2] + ";" + gyrData[0] + ";" + gyrData[1] + ";" + gyrData[2] + ";" + megData[0] + ";" + megData[1] + ";" + megData[2] + ";" +  loc.toString() + ";" + "\n");
                    return;
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        };

        message = RecordFragment.activityName + " - " + deviceId + " - " + System.currentTimeMillis();
        storageState = Environment.getExternalStorageState();
        if (storageState.equals("mounted")) {
            externalStorageAvailable = true;
            externalStorageWriteable = true;
        }
        try {
            while ((externalStorageAvailable) && (externalStorageWriteable)) {
//                extfilepath = Environment.getExternalStorageDirectory() ;

                File root = Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/SensorData");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                extfile = new File(dir, message + ".csv");

                fileWriter = new FileWriter(extfile);
                bufferedWriter = new BufferedWriter(fileWriter);
                sensorregisteration();
                timer.scheduleAtFixedRate(timerTask, 0L, 20);
                return;
            }
            Log.e("Log_Tag", "There is something wrong with the SD card");
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        PreferenceConnector.writeBoolean(context, PreferenceConnector.IS_SERVICE_RUNNING, false);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == 1) {
            try {
                accData = ((float[]) sensorEvent.values.clone());
                return;
            } finally {
            }
        }
        if (sensorEvent.sensor.getType() == 4) {
            try {
                gyrData = ((float[]) sensorEvent.values.clone());
                return;
            } finally {
            }
        }
        if (sensorEvent.sensor.getType() == 2) {
            try {
                megData = ((float[]) sensorEvent.values.clone());
                return;
            } finally {
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void sensorregisteration() {
        this.sensorManager.registerListener(this, this.accSensor, 0);
        this.sensorManager.registerListener(this, this.megSensor, 0);
        this.sensorManager.registerListener(this, this.gyrSensor, 0);
    }

    private void startInForeground() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, "sensorDataChannel")
                .setContentTitle("Sensors data")
                .setContentText("Sensors data collection service")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }



}
