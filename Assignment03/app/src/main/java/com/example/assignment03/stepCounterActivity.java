package com.example.assignment03;

//----------------------------------------------------
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationManager;
import android.hardware.SensorManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
//-----------------------------------------------------

//Our step counter will implement our sensor filter and use the Step Listener interface for its step
//function
public class stepCounterActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private TextView tv;
    private Button start,stop;
    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private LocationManager lm;
    private DBOpenHelper odb;
    private SQLiteDatabase sdb;
    private LocationProvider lp;
    private Location location;
    private ArrayList<Double> altValues;


    @Override
    public void onCreate(Bundle savedInstanceState){

        //We need to get permission from android to access the GPS provider and fine location
        //This is required to access the accelerometer
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lp = lm.getProvider(LocationManager.GPS_PROVIDER);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //required to enable altitude capture
        lp.supportsAltitude();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_counter);

        final String currentUser = getIntent().getStringExtra("CURRENT_USER");
        setTitle(currentUser);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
        start = (Button)findViewById(R.id.startBtn);
        stop = (Button) findViewById(R.id.stopBtn);
        tv = (TextView) findViewById(R.id.Counter);
        odb = new DBOpenHelper(this, "users.db", null, 1);
        sdb = odb.getWritableDatabase();
        altValues = new ArrayList<>();

        //listener on START button to begin our pedometer sensor
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numSteps = 0;
                //register our accelerometer and start listening for changes
                sensorManager.registerListener(stepCounterActivity.this, accel,
                        SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        //listener on STOP button to finish our pedometer sensor
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(stepCounterActivity.this);
                if(numSteps == 0){}
                else {
                    ContentValues cv = new ContentValues();
                    cv.put("STEPS", numSteps);
                    String where = "NAME=?";
                    String[] whereArgs = new String[]{currentUser};
                    sdb.update("users", cv, where, whereArgs);

                    //We need to turn our captured doubles into a single string in order to store in the
                    //DB. Unfortunately MySQLi does not support arraylist storage so this is required
                    //We then store the string in the ALT column in the DB
                    StringBuilder altitudeToInsert = new StringBuilder();
                    for (int i = 0; i < altValues.size(); i++) {
                        altitudeToInsert.append(altValues.get(i) + ",");
                    }
                    ContentValues cv2 = new ContentValues();
                    cv2.put("ALT", altitudeToInsert.toString());
                    sdb.update("users", cv2, where, whereArgs);
                    Toast.makeText(getApplicationContext(), "Steps updated for user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //required method if our sensor accuracy changes. This is hardcoded, so it will never change
    //on its own
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }

    //push the Accelerometer change through our sensor filter to see if the change is worth counting
    //as a step. The updateAccel function will take the x,y,z values
    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1],
                    event.values[2]);
        }
    }

    //Using our Step Detector interface to increase a step pending sensorChange has successfully
    //passed the sensor filter check
    //increase the number of steps and then add our captured altitude
    //PLEASE NOTE: due to the way android captures altitude (WGS84 reference ellipsoid) simply
    //capturing the users actual altitude will result in a horizontal graph as small elevation changes
    //will not be reflected. Therefore, to provide a visual result, I have passed sudo altitudes
    @Override
    public void step(long timeNs){
        numSteps++;
        //make the altitude vary by 1000 meters. I also divide the altitude by 10 in order to avoid
        //very long numbers
        altValues.add(location.getAltitude() /10 + (Math.random() * 1000.0));
        tv.setText(Integer.toString(numSteps));
    }
}
