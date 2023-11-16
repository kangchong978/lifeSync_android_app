package com.example.lifesync;

public class DataPoint {
    final private double value;
    final private String name;

    public DataPoint(double value, String name) {
        this.value = value;
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
