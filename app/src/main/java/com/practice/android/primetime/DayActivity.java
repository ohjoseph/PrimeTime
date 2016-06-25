package com.practice.android.primetime;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class DayActivity extends AppCompatActivity {
    public static final String EXTRA_DAY_ID = "extra_day_id";

    private String mDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        // Get the passed in Date
        Intent i = getIntent();
        mDateString = i.getStringExtra(EXTRA_DAY_ID);

        // Make fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = TimeListFragment.newInstance(mDateString);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
