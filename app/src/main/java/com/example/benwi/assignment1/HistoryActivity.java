package com.example.benwi.assignment1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    private TextView timerValue;
    private TextView distanceValue;
    private TextView calorieValue;
    private TextView runTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Run Tracker");

        runTitle = (TextView) findViewById(R.id.runTitle);
        timerValue = (TextView) findViewById(R.id.timerValueH);
        distanceValue = (TextView) findViewById(R.id.distanceValueH);
        calorieValue = (TextView) findViewById(R.id.calorieValueH);

        runTitle.setText(RunActivity.runSelection);
        int secs = (int) (RunActivity.runTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (RunActivity.runTime % 1000);
        if (mins < 10) {
            timerValue.setText("0" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%02d", milliseconds));
        }
        else {
            timerValue.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%02d", milliseconds));
        }

        distanceValue.setText("" + String.format("%.2f", RunActivity.runDistance) + " miles");

        calorieValue.setText("" + String.format("%.2f", RunActivity.runCalories));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appInfo:
                startActivity(new Intent(HistoryActivity.this, InfoActivity.class));
        }
        return false;
    }

    public void deleteRun(View view){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Are you sure you want to delete this run?")
                .setMessage("This action cannot be undone")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RunActivity.runHistory.remove(RunActivity.runPosition);
                        RunActivity.adapter.notifyDataSetChanged();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
