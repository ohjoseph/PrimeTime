package com.practice.android.primetime;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the fragment to activity
        FragmentManager fm = getSupportFragmentManager();
        TimeListFragment fragment = (TimeListFragment) fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = TimeListFragment.newInstance("", "");
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
