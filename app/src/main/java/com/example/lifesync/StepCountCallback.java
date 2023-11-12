package com.example.lifesync;

public interface StepCountCallback {
    void onStepCountChanged(SensorData sensorData);
}


class SensorData {
    final int steps;
    final int bmi;
    final double distance;
    final double calories;

    SensorData(int steps, int bmi, double distance, double calories) {
        this.steps = steps;
        this.bmi = bmi;
        this.distance = distance;
        this.calories = calories;
    }
}
