package com.example.benwi.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HistoryActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private TextView timerValue;
    private TextView distanceValue;
    private TextView calorieValue;
    private TextView runTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeDrawer();

        runTitle = (TextView) findViewById(R.id.runTitle);
        timerValue = (TextView) findViewById(R.id.timerValueH);
        distanceValue = (TextView) findViewById(R.id.distanceValueH);
        calorieValue = (TextView) findViewById(R.id.calorieValueH);

        runTitle.setText(RunActivity.runSelection);
        timerValue.setText("" + RunActivity.runTime);
        distanceValue.setText("" + RunActivity.runDistance);
        calorieValue.setText("" + RunActivity.runTime);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private  void initializeDrawer(){
        mDrawerList = (ListView)findViewById(R.id.navList3);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                switch (position){
                    case 0:
                        startActivity(new Intent(HistoryActivity.this, InfoActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(HistoryActivity.this, InfoActivity.class));
                        break;
                }
            }
        });
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout3);

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

    private void deleteRun(View view){
        RunActivity.runHistory.remove(RunActivity.runPosition);
        finish();
    }

}
