package com.example.lifesync;

public interface StepCountCallback {
    void onStepCountChanged(SensorData sensorData);
}


class SensorData {
    final int steps;
    final double bmi;
    final double distance;
    final double calories;

    SensorData(int steps, double bmi, double distance, double calories) {
        this.steps = steps;
        this.bmi = bmi;
        this.distance = distance;
        this.calories = calories;
    }
}
