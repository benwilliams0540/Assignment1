package com.example.benwi.assignment1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    public static Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public enum Gender {
        male, female
    }
    private static ArrayList<String> users = new ArrayList<>();
    public static Profile profile;
    private ArrayAdapter<String> adapter;
    private ListView usersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDrawer();
        initializeUsers();
        initializeGPS();

        RelativeLayout focusLayout = (RelativeLayout) findViewById(R.id.activity_main);
        focusLayout.requestFocus();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void initializeGPS(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location){
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){}

            @Override
            public void onProviderEnabled(String s){}

            @Override
            public void onProviderDisabled(String s){}

        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void initializeUsers(){
        users.clear();
        sharedPreferences = this.getSharedPreferences("com.example.benwi.assignent1", Context.MODE_PRIVATE);
        try {
            users = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("users", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (usersListView == null){
            usersListView = (ListView) findViewById(R.id.usersList);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        setListAdapter(adapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                String name = (String) adapter.getItemAtPosition(position);
                String gender = sharedPreferences.getString(name + "Gender", "");
                int age = sharedPreferences.getInt(name + "Age", 0);
                int weight = sharedPreferences.getInt(name + "Weight", 0);

                profile = new Profile(name, gender, age, weight);
                startActivity(new Intent(MainActivity.this, RunActivity.class));
            }
        });

    }

    private void initializeDrawer(){
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                }
            }
        });
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

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
        if (usersListView == null){
            usersListView = (ListView) findViewById(R.id.usersList);
        }
        return usersListView;
    }

    private void setListAdapter(ListAdapter adapter){
        getListView().setAdapter(adapter);
    }

    public void newProfile(View view){
        EditText nameText = (EditText) findViewById(R.id.nameText);
        EditText genderText = (EditText) findViewById(R.id.genderText);
        EditText ageText = (EditText) findViewById(R.id.ageText);
        EditText weightText = (EditText) findViewById(R.id.weightText);

        String name = nameText.getText().toString();
        String gender = genderText.getText().toString();
        String ageString = ageText.getText().toString();
        String weightString = weightText.getText().toString();

        int age = 0;
        int weight = 0;

        if (name.equals("")){
            Log.i("Info", "name failed");
            nameText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if ((!gender.equalsIgnoreCase("male")) && (!gender.equalsIgnoreCase("female"))){
            Log.i("Info", "gender failed");
            genderText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter male or female for gender", Toast.LENGTH_SHORT).show();
        }
        else if (ageString.length() < 1){
            Log.i("Info", "age failed");
            ageText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your age", Toast.LENGTH_SHORT).show();
        }
        else if (weightString.length() < 1){
            Log.i("Info", "weight failed");
            weightText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your weight", Toast.LENGTH_SHORT).show();
        }
        else {
            age = Integer.parseInt(ageString);
            weight = Integer.parseInt(weightString);

            users.add(name);
            try {
                sharedPreferences.edit().putString("users", ObjectSerializer.serialize(users)).apply();
                sharedPreferences.edit().putString(name + "Gender", gender).apply();
                sharedPreferences.edit().putInt(name + "Age", age).apply();
                sharedPreferences.edit().putInt(name + "Weight", weight).apply();
            } catch (IOException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();

            profile = new Profile(name, gender, age, weight);
            Toast.makeText(MainActivity.this, "Profile created", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, RunActivity.class));
        }
    }
}
