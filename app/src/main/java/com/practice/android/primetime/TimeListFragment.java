package com.practice.android.primetime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TimeListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TimeAdapter mAdapter;

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

    /*******
     * RecyclerView Classes
     ********/
    private class TimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

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
            mTimeTextView.setText(timeSlot.getTimeString());
            mActivityTextView.setText(timeSlot.getActivity());
            mEnergyTextView.setText("" + timeSlot.getEnergy());
            mProcrastinationTextView.setText("" + timeSlot.getProcrastinationTime());
        }

        @Override
        public void onClick(View view) {
            // Do nothing yet
            Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
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
     * Helper Classes
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
