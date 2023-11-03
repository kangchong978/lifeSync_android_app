package com.example.lifesync;

import androidx.annotation.NonNull;

public enum ActivityClass {
    Unknown(-1, ""), Steps(0,"Steps"), Distance(1,"Distance"), BMI(2,"BMI"), CaloriesBurned(3, "CaloriesBurned");
    private final int code;
    private final String activityName;

    ActivityClass(int code, String activityName) {
        this.code = code;this.activityName = activityName;
    }

    public int toInt() {
        return code;
    }

    @NonNull
    public String toString() {
        //only override toString, if the returned value has a meaning for the
        //human viewing this value
        return this.activityName;
    }

//    public int getInt() {
//        //only override toString, if the returned value has a meaning for the
//        //human viewing this value
//        return code;
//    }

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
    }static public ActivityClass getClassFromString(String stringCode) {
        switch (stringCode) {
            case "Steps":
                return Steps;
            case "Distance":
                return Distance;
            case "BMI":
                return BMI;
            case "CaloriesBurned":
                return CaloriesBurned;
            default:
                return Unknown;
        }
    }



}