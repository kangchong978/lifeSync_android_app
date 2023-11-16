package com.example.lifesync;

public class ActivityTask  implements  Comparable<ActivityTask>{
    private final ActivityClass activityClass;
    private double value;
    private double previousValue = 0;

    public double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(double previousValue) {
        this.previousValue = previousValue;
    }

    final private int targetValue;

    private boolean done;

    private  int id;

    public int getTargetValue() {
        return targetValue;
    }

    public int getId() {
        return id;
    }

    public ActivityTask(int id, ActivityClass activityClass, int value, int targetValue) {
        this.id = id;
        this.activityClass = activityClass;
        this.value = value;
        this.done = false;
        this.targetValue = targetValue;
    }

    public double getValue() {
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

    public void setValue(double value) {
        this.value = value;
    }

    public int compareTo(ActivityTask compareTask) {

        double compareValue = ((ActivityTask) compareTask).getValue();

        //ascending order
//        return this.value - compareValue;

        //descending order
        Double result =  compareValue - this.value;
        return result.intValue();

    }
}
