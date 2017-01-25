package com.example.benwi.assignment1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class RunActivity extends AppCompatActivity {
    Location startLocation, endLocation;

    Handler customHandler = new Handler();

    TextView timerValue;
    TextView distanceValue;
    TextView calorieValue;

    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    float distanceMeters, distanceMiles;
    float rate, calories;

    public static ArrayList<String> runs = new ArrayList<>();
    public ArrayAdapter<String> adapter;
    public ListView runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        runs.clear();
        try {
            runs = (ArrayList<String>) ObjectSerializer.deserialize(MainActivity.sharedPreferences.getString(MainActivity.profile.name + "History", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runList == null){
            runList = (ListView) findViewById(R.id.runList);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, runs);
        setListAdapter(adapter);

        runList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                String runSelection = (String) adapter.getItemAtPosition(position);
                long runTime = MainActivity.sharedPreferences.getLong(MainActivity.profile.name + runSelection + "time", 0);
                float runDistance = MainActivity.sharedPreferences.getFloat(MainActivity.profile.name + runSelection + "distance", 0);
                float runCalories = MainActivity.sharedPreferences.getFloat(MainActivity.profile.name + runSelection + "calories", 0);

                Log.i("Selection", runSelection);
                Log.i("Time", Long.toString(runTime));
                Log.i("Distance", Float.toString(runDistance));
                Log.i("Calories", Float.toString(runCalories));
            }
        });

        timerValue = (TextView) findViewById(R.id.timerValue);
        distanceValue = (TextView) findViewById(R.id.distanceValue);
        calorieValue = (TextView) findViewById(R.id.calorieValue);
    }

    public void startRun(View view){
        startTime = SystemClock.uptimeMillis();
        startLocation = MainActivity.currentLocation;
        endLocation = MainActivity.currentLocation;
        customHandler.postDelayed(updateTimerThread, 0);
        customHandler.postDelayed(updateDistanceThread, 0);
        customHandler.postDelayed(updateCaloriesThread, 0);
    }

    public void endRun(View view){
        timeSwapBuff += timeInMilliseconds;
        endLocation = MainActivity.currentLocation;
        customHandler.removeCallbacks(updateTimerThread);
        customHandler.removeCallbacks(updateDistanceThread);
        customHandler.removeCallbacks(updateCaloriesThread);
        //Toast.makeText(RunActivity.this, "Distance travelled: " + String.format("%.2f", distanceMiles), Toast.LENGTH_SHORT).show();
    }

    public void saveRun(View view){
        int currentRun = runs.size()+ 1;
        String runName = "Run " + currentRun;
        runs.add(runName);
        try {
            MainActivity.sharedPreferences.edit().putString(MainActivity.profile.name + "History", ObjectSerializer.serialize(runs)).apply();
            MainActivity.sharedPreferences.edit().putLong(MainActivity.profile.name + runName + "time", updatedTime).apply();
            MainActivity.sharedPreferences.edit().putFloat(MainActivity.profile.name + runName + "distance", distanceMiles).apply();
            MainActivity.sharedPreferences.edit().putFloat(MainActivity.profile.name + runName + "calories", calories).apply();
        } catch (IOException e){
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private Runnable updateTimerThread = new Runnable(){
        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            if (mins < 10) {
                timerValue.setText("0" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            else {
                timerValue.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            customHandler.postDelayed(this, 0);
        }
    };

    private Runnable updateDistanceThread = new Runnable(){
        public void run(){
            endLocation = MainActivity.currentLocation;
            distanceMeters = startLocation.distanceTo(endLocation);
            distanceMiles = distanceMeters * (float) 0.000621371;

            distanceValue.setText("" + String.format("%.2f", distanceMiles) + " miles");
            customHandler.postDelayed(this, 0);
        }
    };

    private Runnable updateCaloriesThread = new Runnable() {
        @Override
        public void run() {
            rate = (float) 0.72 * MainActivity.profile.weight;
            calories = rate * distanceMiles;

            if (calories > 1) {
                calorieValue.setText("" + String.format("%.2f", calories));
            }
            customHandler.postDelayed(this, 0);
        }
    };

    protected ListView getListView(){
        if (runList == null){
            runList = (ListView) findViewById(R.id.usersList);
        }
        return runList;
    }

    protected void setListAdapter(ListAdapter adapter){
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter(){
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }
}
