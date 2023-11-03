package com.example.lifesync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class StepCountService extends Service {
    private static final String CHANNEL_ID = "0";
    private static final int NOTIFICATION_ID = 1;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private StepDetector stepDetector;
    private int steps = 0;

    private StepCountCallback stepCountCallback;


    private final IBinder mBinder = new UiBinder();

    public void setStepCountCallback(StepCountCallback callback) {
        this.stepCountCallback = callback;
    }

    public class UiBinder extends Binder {
        StepCountService getService() {
            // Return this instance of MyService so clients can call public methods
            return StepCountService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification to run the service in the foreground
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        // Register sensor listener and perform other necessary operations
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometerListener);
    }

    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            stepDetector.processAccelerometerData(x, y, z);
            int newSteps = stepDetector.getStepCount();
            if (steps < newSteps) {
                steps = newSteps;
                double stepLengthMeters = 0.7; // Example average step length in meters
                double distance = steps * stepLengthMeters;
                Log.d("Debugger", String.format("Steps： %d", steps));
                if (stepCountCallback != null) {
                    stepCountCallback.onStepCountChanged(new SensorData(steps, 0, distance, 0));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed.
        }
    };

    public SensorData getSensorData() {
        double stepLengthMeters = 0.7; // Example average step length in meters
        double distance = steps * stepLengthMeters;
        return  new SensorData(steps, 0, distance, 0);
    }

    private Notification createNotification() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Create a notification for the service
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Synchronizing").setContentText("Your activity is being recorded").setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();
    }
}

