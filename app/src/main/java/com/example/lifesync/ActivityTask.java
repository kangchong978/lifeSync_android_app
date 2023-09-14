package com.example.lifesync;

public class ActivityTask  implements  Comparable<ActivityTask>{
    private final ActivityClass activityClass;
    private int value;
    private int previousValue = 0;

    public int getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(int previousValue) {
        this.previousValue = previousValue;
    }

    final private int targetValue;

    private boolean done;

    public int getTargetValue() {
        return targetValue;
    }

    public ActivityTask(ActivityClass activityClass, int value, int targetValue) {
        this.activityClass = activityClass;
        this.value = value;
        this.done = false;
        this.targetValue = targetValue;
    }

    public int getValue() {
        return this.value;
    }

    public ActivityClass getActivityClass() {
        return this.activityClass;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int compareTo(ActivityTask compareTask) {

        int compareValue = ((ActivityTask) compareTask).getValue();

        //ascending order
//        return this.value - compareValue;

        //descending order
        return compareValue - this.value;

    }
}
