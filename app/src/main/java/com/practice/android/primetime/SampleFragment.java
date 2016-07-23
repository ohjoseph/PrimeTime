package com.practice.android.primetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 6/21/16.
 */

public class SampleFragment extends Fragment implements UpdateFragmentUI {

    private TextView mTextView;
    private String mJsonString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle outState) {
        View v = inflater.inflate(R.layout.sample_fragment, parent, false);

        mTextView = (TextView) v.findViewById(R.id.textView);

        updateUI();
        return v;
    }

    @Override
    public void updateUI() {

    }
}
