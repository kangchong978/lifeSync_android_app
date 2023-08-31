package com.example.lifesync;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.lifesync.databinding.ActivityMainBinding;

import android.hardware.SensorManager;
import android.util.Log;
import android.hardware.Sensor;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private StepDetector stepDetector = new StepDetector();
    private int steps = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
         sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         List<Sensor> accelerometerSensor = Collections.singletonList(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
         TextView textView =(TextView)  findViewById(R.id.textview_first);

//         Log.d( "Debugger", accelerometerSensor.toString());

         if(!accelerometerSensor.isEmpty()){
             Sensor aSensor =  accelerometerSensor.get(0);
             Log.d( "Debugger", aSensor.toString());
             sensorEventListener = new SensorEventListener(){

                 @Override
                 public void onSensorChanged(SensorEvent event) {
                     float x = event.values[0];
                     float y = event.values[1];
                     float z = event.values[2];


                     stepDetector.processAccelerometerData(x, y ,z);
                     int newSteps = stepDetector.getStepCount();
                     if(steps < newSteps){
                         steps = newSteps;
                         Log.d("Debugger", String.format("Steps： %d", steps));
                         textView.setText(String.format("Steps： %d", steps));
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

}