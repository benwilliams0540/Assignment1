package com.example.benwi.assignment1;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Run Tracker");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
