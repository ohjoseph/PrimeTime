package com.practice.android.primetime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Joseph on 6/21/16.
 */

public class DayListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DayAdapter mDayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle outState) {
        View v = inflater.inflate(R.layout.fragment_day_list, parent, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.day_list_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        updateUI();

        return v;
    }

    /*******
     * RecyclerView Classes
     *******/
    private class DayHolder extends RecyclerView.ViewHolder {
        public DayHolder(View itemView) {
            super(itemView);
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {
        private List<Day> mDays;

        private DayAdapter(List<Day> days) {
            mDays = days;
        }

        @Override
        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_day, parent, false);
            return new DayHolder(v);
        }

        @Override
        public void onBindViewHolder(DayHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }

        public void setDays(List<Day> days) {
            mDays = days;
            notifyDataSetChanged();
        }
    }

    /**********
     * Helper Methods
     **********/
    private void updateUI() {
        List<Day> dayList = TimeLab.get(getActivity()).getDays();

        // Update information from database
        if (mDayAdapter == null) {
            mDayAdapter = new DayAdapter(dayList);
        } else {
            mDayAdapter.setDays(dayList);
        }
        mRecyclerView.setAdapter(mDayAdapter);
        mRecyclerView.setFocusable(false);
    }
}
