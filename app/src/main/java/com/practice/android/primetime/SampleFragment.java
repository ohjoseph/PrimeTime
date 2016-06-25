package com.practice.android.primetime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joseph on 6/21/16.
 */

public class SampleFragment extends Fragment implements UpdateFragmentUI {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle outState) {
        View v = inflater.inflate(R.layout.sample_fragment, parent, false);

        return v;
    }

    @Override
    public void updateUI() {

    }
}
