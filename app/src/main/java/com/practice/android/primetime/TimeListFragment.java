package com.practice.android.primetime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TimeListFragment extends Fragment {
    public static final int REQUEST_TIME_SLOT = 0;
    private static final String DIALOG_TIME_SLOT = "dialog_time_slot";

    private RecyclerView mRecyclerView;
    private TimeAdapter mAdapter;
    private TimeSlot mLastTimeSlotClicked;

    public static TimeListFragment newInstance(String param1, String param2) {
        TimeListFragment fragment = new TimeListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_time_list, container, false);

        // Initialize RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.time_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);
        updateUI();

        return v;
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        }
    }

    /********
     * Helper Methods
     *********/
    private void updateUI() {
        List<TimeSlot> slots = TimeLab.get(getActivity()).getTimeSlots(TimeSlot.TODAY_ID);

        if (mAdapter == null) {
            mAdapter = new TimeAdapter(slots);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTimeSlots(slots);
            mAdapter.notifyDataSetChanged();
        }

    }
}
