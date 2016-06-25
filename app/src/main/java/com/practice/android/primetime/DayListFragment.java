package com.practice.android.primetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Joseph on 6/21/16.
 */

public class DayListFragment extends Fragment implements UpdateFragmentUI {
    // Inflated Views
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private DayAdapter mDayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle outState) {
        View v = inflater.inflate(R.layout.fragment_day_list, parent, false);
        mEmptyView = (TextView) v.findViewById(R.id.day_list_empty_view);

        // Initialize RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.day_list_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        updateUI();
        return v;
    }

    /*******
     * RecyclerView Classes
     *******/
    private class DayHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mDateTextView;
        private Day mDay;

        public DayHolder(View itemView) {
            super(itemView);

            // Initialize list item
            mDateTextView = (TextView) itemView.findViewById(R.id.day_list_item_date_textView);
            mDateTextView.setOnClickListener(this);
        }

        public void bindHolder(Day day) {
            mDateTextView.setText(day.getDateString());
            mDay = day;
        }

        @Override
        public void onClick(View view) {
            // Make a new DayActivity
            Intent i = new Intent(getActivity(), DayActivity.class);
            i.putExtra(DayActivity.EXTRA_DAY_ID, mDay.getDateString());
            startActivity(i);
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
            holder.bindHolder(mDays.get(position));
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
    @Override
    public void updateUI() {
        // Get list of days
        List<Day> dayList = TimeLab.get(getActivity()).getDays();
        Collections.reverse(dayList);

        // Show or hide the empty view
        if (dayList.isEmpty()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
        }

        // Update information from database
        if (mDayAdapter == null) {
            mDayAdapter = new DayAdapter(dayList);
        } else {
            mDayAdapter.setDays(dayList);
        }
        mRecyclerView.setAdapter(mDayAdapter);

        // Scroll ScrollView up to top
        mRecyclerView.setFocusable(false);
    }
}
