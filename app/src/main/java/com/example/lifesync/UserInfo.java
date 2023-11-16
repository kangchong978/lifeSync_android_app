package com.example.lifesync;

public class UserInfo {
    int id;
    String name;
    double weight; // in kg
    double height; // in cm
    int age;
    String gender;

    public UserInfo(int id, String name, double weight, double height, int age, String gender) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
    }

    // Add getters and setters for your fields as needed
}
