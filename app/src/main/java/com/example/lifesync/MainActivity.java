package com.example.lifesync;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lifesync.databinding.ActivityMainBinding;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private StepDetector stepDetector = new StepDetector();
    private int steps = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        com.example.lifesync.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         List<Sensor> accelerometerSensor = Collections.singletonList(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));


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

    }

}