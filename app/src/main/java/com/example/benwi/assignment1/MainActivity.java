package com.example.benwi.assignment1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.benwi.assignment1.R.id.nameText;

public class MainActivity extends AppCompatActivity {

    public LocationManager locationManager;
    public LocationListener locationListener;
    public enum Gender {
        male, female
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText nameText = (EditText) findViewById(R.id.nameText);
        nameText.requestFocus();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
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

        Log.i("Info", "Begin");

        if (name.equals("")){
            Log.i("Info", "name failed");
            nameText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if ((!gender.equalsIgnoreCase("male")) && (!gender.equalsIgnoreCase("female"))){
            Log.i("Info", "gender failed");
            genderText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter male or female for gender", Toast.LENGTH_SHORT);
        }
        else if (ageString.length() < 1){
            Log.i("Info", "age failed");
            ageText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your age", Toast.LENGTH_SHORT);
        }
        else if (weightString.length() < 1){
            Log.i("Info", "weight failed");
            weightText.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter your weight", Toast.LENGTH_SHORT);
        }
        else {
            age = Integer.parseInt(ageString);
            weight = Integer.parseInt(weightString);
            Profile profile = new Profile(name, gender, age, weight);
            Toast.makeText(MainActivity.this, "Profile created", Toast.LENGTH_SHORT);
            startActivity(new Intent(MainActivity.this, RunActivity.class));
        }
        Log.i("Info", "End");
    }
}
