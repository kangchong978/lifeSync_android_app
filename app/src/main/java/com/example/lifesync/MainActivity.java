package com.example.lifesync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.lifesync.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    final private StepDetector stepDetector = new StepDetector();
    private int steps = 0;

    @SuppressLint("RestrictedApi")
    @Override

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        com.example.lifesync.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         List<Sensor> accelerometerSensor = Collections.singletonList(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        if (!hasBatteryOptimizationExemption()) {
            requestBatteryOptimizationExemption();
        }

//         Log.d( "Debugger", accelerometerSensor.toString());

        Sensor aSensor = accelerometerSensor.get(0);
        Log.d( "Debugger", aSensor.toString());
        //                         textView.setText(String.format("Steps： %d", steps));
        // Do nothing


        SensorEventListener sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];


                stepDetector.processAccelerometerData(x, y, z);
                int newSteps = stepDetector.getStepCount();
                if (steps < newSteps) {
                    steps = newSteps;
                    Log.d("Debugger", String.format("Steps： %d", steps));
//                         textView.setText(String.format("Steps： %d", steps));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Do nothing
            }
        };

        sensorManager.registerListener(sensorEventListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL );

//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
//        setSupportActionBar(toolbar);

//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav,
//                R.string.close_nav);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
    }


    private boolean hasBatteryOptimizationExemption() {
        String packageName = getPackageName();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return powerManager.isIgnoringBatteryOptimizations(packageName);
            }
        }
        return false;
    }

    private void requestBatteryOptimizationExemption() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }







}