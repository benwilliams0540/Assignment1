package com.example.benwi.assignment1;

/**
 * Created by benwi on 1/24/2017.
 */
public class Profile {
    public String name;
    public MainActivity.Gender gender;
    public int age;
    public int weight;

    public Profile(String n, String g, int a, int w){
        name = n;
        if (g.equalsIgnoreCase("male")){
            gender = MainActivity.Gender.male;
        }
        else {
            gender = MainActivity.Gender.female;
        }
        age = a;
        weight = w;
    }
}
