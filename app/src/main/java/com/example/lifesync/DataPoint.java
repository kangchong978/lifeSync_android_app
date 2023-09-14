package com.example.lifesync;

public class DataPoint {
    final private int value;
    final private String name;

    public DataPoint(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
