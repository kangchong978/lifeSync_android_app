package com.example.lifesync;

public class StepDetector {
    private static final float GRAVITY_THRESHOLD = 13.5f; // Threshold for detecting gravity (stationary)
    private static final int STEP_DELAY_MS = 500; // Minimum time between steps in milliseconds
    private static final int STEP_THRESHOLD = 8;   // Threshold for step detection

    private long lastStepTime = 0;
    private int stepCount = 0;

    private boolean isStationary = true;

    public void processAccelerometerData(float x, float y, float z) {
        long currentTime = System.currentTimeMillis();

        // Calculate the magnitude of the acceleration vector
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        // Check if the magnitude is above the threshold
        if (magnitude > GRAVITY_THRESHOLD) {
            isStationary = false;
        } else {
            isStationary = true;
        }

        // Check if the magnitude is above the step threshold
        if (!isStationary && magnitude > STEP_THRESHOLD) {
            // Check if enough time has passed since the last step
            if (currentTime - lastStepTime > STEP_DELAY_MS) {
                lastStepTime = currentTime;
                stepCount++;
                // Perform any desired action on step detection
                // For example, update the UI to display step count
            }
        }
    }

    public int getStepCount() {
        return stepCount;
    }
}