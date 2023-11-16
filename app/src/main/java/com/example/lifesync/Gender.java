package com.example.lifesync;
public enum Gender {
    Unknown(-1, "Unknown"),Male(0, "Male"), Female(1,"Female" );
    final int id;
    final String name;

    Gender(int id, String name) {
        this.id = id;
        this.name = name;
    }

    static public Gender getClassFromInt(int code) {
        switch (code) {
            case 0:
                return Male;
            case 1:
                return Female;
            default:
                return Unknown;
        }
    }

    public String toString() {
         return this.name;
    }
}