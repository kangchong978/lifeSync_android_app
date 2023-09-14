package com.example.lifesync;

import androidx.annotation.NonNull;

public enum ActivityClass {
    Unknown(-1), Steps(0), Distance(1), BMI(2), CaloriesBurned(3);
    private final int code;

    ActivityClass(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }

    @NonNull
    public String toString() {
        //only override toString, if the returned value has a meaning for the
        //human viewing this value
        return String.valueOf(code);
    }

    static public ActivityClass getClassFromInt(int code) {
        switch (code) {
            case 0:
                return Steps;
            case 1:
                return Distance;
            case 2:
                return BMI;
            case 3:
                return CaloriesBurned;
            default:
                return Unknown;
        }
    }

    static public ActivityClass getClassFromInt(String stringCode) {
        int code = Integer.parseInt(stringCode);
        return getClassFromInt(code);
    }
}