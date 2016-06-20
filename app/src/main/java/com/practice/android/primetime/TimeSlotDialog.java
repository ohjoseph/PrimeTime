package com.practice.android.primetime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.UUID;

public class TimeSlotDialog extends DialogFragment {
    private static final String ARG_ACTIVITY = "arg_activity";
    private static final String ARG_ENERGY = "arg_energy";
    private static final String ARG_PROCRASTINATION = "arg_procrastination";

    public static final String EXTRA_ACTIVITY = "arg_activity";
    public static final String EXTRA_ENERGY = "arg_energy";
    public static final String EXTRA_PROCRASTINATION = "arg_procrastination";

    private EditText mActivityEditText;
    private Spinner mEnergySpinner;
    private Spinner mProcrastinationSpinner;

    private int mEnergyInt;
    private int mProcrastInt;

    public static TimeSlotDialog newInstance(TimeSlot slot) {
        // Create and return fragment with arguments
        TimeSlotDialog fragment = new TimeSlotDialog();
        Bundle args = createArgs(slot);
        fragment.setArguments(args);
        return fragment;
    }

    private static Bundle createArgs(TimeSlot slot) {
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY, slot.getActivity());
        args.putInt(ARG_ENERGY, slot.getEnergy());
        args.putInt(ARG_PROCRASTINATION, slot.getProcrastinationTime());

        return args;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get argument data
        Bundle args = getArguments();
        String activity = args.getString(ARG_ACTIVITY);
        int energy = args.getInt(ARG_ENERGY);
        int procrastination = args.getInt(ARG_PROCRASTINATION);

        // Inflate view and bind widgets
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time_slot, null);
        mActivityEditText = (EditText) v.findViewById(R.id.dialog_activity_editText);
        mEnergySpinner = (Spinner) v.findViewById(R.id.energy_spinner);
        mProcrastinationSpinner = (Spinner) v.findViewById(R.id.procrastination_spinner);

        // Initialize EditText
        if (!activity.equals(null)) {
            mActivityEditText.setText(activity);
        }
        mActivityEditText.selectAll();
        mActivityEditText.requestFocus();

        // Initialize energy spinner
        ArrayAdapter<CharSequence> energyAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.energy_level_array,
                android.R.layout.simple_spinner_item);
        energyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEnergySpinner.setAdapter(energyAdapter);
        mEnergySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Saves selected item
                String selected = (String) adapterView.getItemAtPosition(i);
                mEnergyInt = Integer.parseInt(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
        // Set the saved energy level
        String energyString = Integer.toString(energy);
        if (!energyString.equals(null)) {
            int position = energyAdapter.getPosition(energyString);
            mEnergySpinner.setSelection(position);
        }

        // Initalize procrastination spinner
        ArrayAdapter<CharSequence> procrastAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.procrastination_time_array,
                android.R.layout.simple_spinner_item);
        procrastAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProcrastinationSpinner.setAdapter(procrastAdapter);
        mProcrastinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Save out procrastination time
                String selected = (String) adapterView.getItemAtPosition(i);
                mProcrastInt = Integer.parseInt(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
        // Set the saved procrastination time
        String procrastString = Integer.toString(procrastination);
        if (!procrastString.equals(null)) {
            int position = procrastAdapter.getPosition(procrastString);
            mProcrastinationSpinner.setSelection(position);
        }

        // Return created dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String activity = mActivityEditText.getText().toString();
                        setResult(activity, mEnergyInt, mProcrastInt);
                        hideKeyboard();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideKeyboard();
                    }
                })
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show the soft keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setResult(String activity, int energy, int procrastination) {
        // Check for target fragment
        if (getTargetFragment() == null) {
            return;
        }

        // Send updated data back
        Intent i = new Intent();
        i.putExtra(EXTRA_ACTIVITY, activity);
        i.putExtra(EXTRA_ENERGY, energy);
        i.putExtra(EXTRA_PROCRASTINATION, procrastination);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityEditText.getWindowToken(), 0);
    }
}
