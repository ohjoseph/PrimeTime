package com.practice.android.primetime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TimeListFragment extends Fragment implements UpdateFragmentUI {
    public static final int REQUEST_TIME_SLOT = 0;
    private static final String DIALOG_TIME_SLOT = "dialog_time_slot";
    private static final String ARG_DATE = "arg_date_string";

    private RecyclerView mRecyclerView;
    private TextView mDateTextView;
    private TextView mEmptyView;
    private TimeAdapter mAdapter;
    private TimeSlot mLastTimeSlotClicked;

    private String mDateString;

    public static TimeListFragment newInstance(String dateString) {
        TimeListFragment fragment = new TimeListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, dateString);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle outState) {
        super.onCreate(outState);
        setHasOptionsMenu(true);

        // Get arguments and data
        Bundle args = getArguments();
        if (args != null) {
            mDateString = args.getString(ARG_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_time_list, container, false);
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);
        mDateTextView = (TextView) v.findViewById(R.id.time_list_date_textView);

        // Initialize RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.time_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);

        updateUI();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_time_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_day:
                // Delete the entire day
                TimeLab.get(getActivity()).deleteDay(mDateString);
                break;
            case R.id.menu_item_reset_day:
                // Create or reset new day
                TimeLab.get(getActivity()).createToday();
                break;
            default:
                return onOptionsItemSelected(item);
        }

        // Tidy up UI
        getActivity().closeOptionsMenu();
        updateUI();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for TimeSlotDialog
        if (requestCode == REQUEST_TIME_SLOT && resultCode == Activity.RESULT_OK) {
            if (data == null || mLastTimeSlotClicked == null) {
                return;
            }

            // Update the clicked TimeSlot
            String activity = data.getStringExtra(TimeSlotDialog.EXTRA_ACTIVITY);
            int energy = data.getIntExtra(TimeSlotDialog.EXTRA_ENERGY, 1);
            int procrast = data.getIntExtra(TimeSlotDialog.EXTRA_PROCRASTINATION, 0);
            mLastTimeSlotClicked.setActivity(activity);
            mLastTimeSlotClicked.setEnergy(energy);
            mLastTimeSlotClicked.setProcrastinationTime(procrast);

            // Save changes
            TimeLab.get(getActivity()).updateTimeSlot(mLastTimeSlotClicked);
            updateUI();
        }
    }

    /*******
     * RecyclerView Classes
     ********/
    private class TimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TimeSlot mTimeSlot;
        private TextView mTimeTextView;
        private TextView mActivityTextView;
        private TextView mEnergyTextView;
        private TextView mProcrastinationTextView;

        public TimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTimeTextView =
                    (TextView) itemView.findViewById(R.id.list_item_time_textView);
            mActivityTextView =
                    (TextView) itemView.findViewById(R.id.list_item_activity_textView);
            mEnergyTextView =
                    (TextView) itemView.findViewById(R.id.list_item_energy_textView);
            mProcrastinationTextView =
                    (TextView) itemView.findViewById(R.id.list_item_procrastination_textView);
        }

        public void bindTimeHolder(TimeSlot timeSlot) {
            mTimeSlot = timeSlot;
            mTimeTextView.setText(timeSlot.getTimeString());
            mActivityTextView.setText(timeSlot.getActivity());
            mEnergyTextView.setText("" + timeSlot.getEnergy());
            mProcrastinationTextView.setText("" + timeSlot.getProcrastinationTime());
        }

        @Override
        public void onClick(View view) {
            // Save out last clicked
            mLastTimeSlotClicked = mTimeSlot;
            // Open the TimeSlot dialog
            FragmentManager fm = getFragmentManager();
            TimeSlotDialog dialogFrag = TimeSlotDialog.newInstance(mTimeSlot);
            dialogFrag.setTargetFragment(TimeListFragment.this, REQUEST_TIME_SLOT);
            dialogFrag.show(fm, DIALOG_TIME_SLOT);
        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeHolder> {
        private List<TimeSlot> mTimeSlots;

        public TimeAdapter(List<TimeSlot> timeSlots) {
            mTimeSlots = timeSlots;
        }

        @Override
        public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_time_slot, parent, false);
            return new TimeHolder(v);
        }

        @Override
        public void onBindViewHolder(TimeHolder holder, int position) {
            TimeSlot ts = mTimeSlots.get(position);
            holder.bindTimeHolder(ts);
        }

        @Override
        public int getItemCount() {
            return mTimeSlots.size();
        }

        public void setTimeSlots(List<TimeSlot> slots) {
            mTimeSlots = slots;
            notifyDataSetChanged();
        }
    }

    /********
     * Helper Methods
     *********/
    @Override
    public void updateUI() {
        // Get data from database
        List<TimeSlot> slots = TimeLab.get(getActivity())
                .getTimeSlots(mDateString);

        // Show or hide the empty view
        if (slots.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        // Keep or update existing adapter
        if (mAdapter == null) {
            mAdapter = new TimeAdapter(slots);
        } else {
            mAdapter.setTimeSlots(slots);
        }

        // Set the date on the textView
        mDateTextView.setText(mDateString);

        // Reset the adapter
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setFocusable(false);
    }
}
