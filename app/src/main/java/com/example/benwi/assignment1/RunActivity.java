package com.example.benwi.assignment1;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class RunActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private Location startLocation, endLocation;
    private Handler customHandler = new Handler();

    private TextView timerValue;
    private TextView distanceValue;
    private TextView calorieValue;

    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private long startTime = 0L;

    private float distanceMiles;
    private float calories;

    private static ArrayList<String> runHistory = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        initializeDrawer();
        initializeRuns();

        timerValue = (TextView) findViewById(R.id.timerValue);
        distanceValue = (TextView) findViewById(R.id.distanceValue);
        calorieValue = (TextView) findViewById(R.id.calorieValue);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void initializeRuns(){
        runHistory.clear();
        try {
            runHistory = (ArrayList<String>) ObjectSerializer.deserialize(MainActivity.sharedPreferences.getString(MainActivity.profile.name + "History",
                    ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runList == null){
            runList = (ListView) findViewById(R.id.runList);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, runHistory);
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
    }

    private  void initializeDrawer(){
        mDrawerList = (ListView)findViewById(R.id.navList2);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                switch (position){
                    case 0:
                        startActivity(new Intent(RunActivity.this, InfoActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(RunActivity.this, InfoActivity.class));
                        break;
                }
            }
        });
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout2);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Run Tracker");
    }

    private void addDrawerItems() {
        String[] menuItems = {"Information"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Run Tracker");
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    private ListView getListView(){
        if (runList == null){
            runList = (ListView) findViewById(R.id.usersList);
        }
        return runList;
    }

    private void setListAdapter(ListAdapter adapter){
        getListView().setAdapter(adapter);
    }

    public void startRun(View view){
        startTime = SystemClock.uptimeMillis();
        startLocation = MainActivity.currentLocation;
        endLocation = MainActivity.currentLocation;

        timeSwapBuff += timeInMilliseconds;
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
    }

    public void saveRun(View view){
        int currentRun = runHistory.size()+ 1;
        String runName = "Run " + currentRun;
        runHistory.add(runName);
        try {
            MainActivity.sharedPreferences.edit().putString(MainActivity.profile.name + "History", ObjectSerializer.serialize(runHistory)).apply();
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
            distanceMiles = startLocation.distanceTo(endLocation) * (float) 0.000621371;

            distanceValue.setText("" + String.format("%.2f", distanceMiles) + " miles");
            customHandler.postDelayed(this, 0);
        }
    };

    private Runnable updateCaloriesThread = new Runnable() {
        @Override
        public void run() {
            calories = (float) 0.72 * MainActivity.profile.weight * distanceMiles;

            if (calories > 1) {
                calorieValue.setText("" + String.format("%.2f", calories));
            }
            customHandler.postDelayed(this, 0);
        }
    };
}
