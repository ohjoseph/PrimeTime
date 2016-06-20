package com.practice.android.primetime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.UUID;

public class TimeSlotDialog extends DialogFragment {
    private static final String ARG_DAY_ID = "arg_day_id";
    private static final String ARG_TIME = "arg_time";
    private static final String ARG_ACTIVITY = "arg_activity";
    private static final String ARG_ENERGY = "arg_energy";
    private static final String ARG_PROCRASTINATION = "arg_procrastination";

    private EditText mActivityEditText;
    private Spinner mEnergySpinner;
    private Spinner mProcrastinationSpinner;

    public static TimeSlotDialog newInstance(TimeSlot slot) {
        // Create and return fragment with arguments
        TimeSlotDialog fragment = new TimeSlotDialog();
        Bundle args = createArgs(slot);
        fragment.setArguments(args);
        return fragment;
    }

    private static Bundle createArgs(TimeSlot slot) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY_ID, slot.getDayId());
        args.putInt(ARG_TIME, slot.getTime());
        args.putString(ARG_ACTIVITY, slot.getActivity());
        args.putInt(ARG_ENERGY, slot.getEnergy());
        args.putInt(ARG_PROCRASTINATION, slot.getProcrastinationTime());

        return args;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get argument data
        Bundle args = getArguments();
        UUID dayId = (UUID) args.getSerializable(ARG_DAY_ID);
        int time = args.getInt(ARG_TIME);
        String activity = args.getString(ARG_ACTIVITY);
        int energy = args.getInt(ARG_ENERGY);
        int procrastination = args.getInt(ARG_PROCRASTINATION);

        // Inflate view and bind widgets
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time_slot, null);
        mActivityEditText = (EditText) v.findViewById(R.id.dialog_activity_editText);
        mEnergySpinner = (Spinner) v.findViewById(R.id.energy_spinner);
        mProcrastinationSpinner = (Spinner) v.findViewById(R.id.procrastination_spinner);

        // Initialize energy spinner
        ArrayAdapter<CharSequence> energyAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.energy_level_array,
                android.R.layout.simple_spinner_item);
        energyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEnergySpinner.setAdapter(energyAdapter);

        // Initalize procrastination spinner
        ArrayAdapter<CharSequence> procrastAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.procrastination_time_array,
                android.R.layout.simple_spinner_item);
        procrastAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProcrastinationSpinner.setAdapter(procrastAdapter);

        // Return created dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
