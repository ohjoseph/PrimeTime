package com.practice.android.primetime;

import android.content.ContentValues;
import android.text.format.DateFormat;

import com.practice.android.primetime.database.TimeDbSchema;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Joseph on 6/19/16.
 */

public class TimeSlot {
    public static UUID TODAY_ID = UUID.randomUUID();

    private UUID mDayId;
    private int mTime;
    private String mActivity;
    private int mEnergy;
    private int mProcrastinationTime;

    public TimeSlot() {
        mDayId = TODAY_ID;
        mActivity = "Sample Activity";
    }

    public TimeSlot(UUID dayId, int time) {
        mDayId = dayId;
        mTime = time;
    }

    public TimeSlot(UUID dayId, int time, String activity) {
        mDayId = dayId;
        mTime = time;
        mActivity = activity;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    public String getTimeString() {
        String timeString;
        if (mTime > 12) {
            timeString = (mTime % 12) + " PM";
        } else if (mTime == 12) {
            timeString = "12 PM";
        } else if (mTime == 0) {
            timeString = "12 AM";
        } else {
            timeString = mTime + " AM";
        }

        return timeString;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TimeTable.Cols.DAY_ID, mDayId.toString());
        values.put(TimeTable.Cols.TIME, Integer.toString(mTime));
        values.put(TimeTable.Cols.ACTIVITY, mActivity);
        values.put(TimeTable.Cols.ENERGY, Integer.toString(mEnergy));
        values.put(TimeTable.Cols.PROCRASTINATION, Integer.toString(mProcrastinationTime));
        return values;
    }

    public UUID getDayId() {
        return mDayId;
    }

    public String getActivity() {
        return mActivity;
    }

    public void setActivity(String activity) {
        mActivity = activity;
    }

    public int getEnergy() {
        return mEnergy;
    }

    public void setEnergy(int energy) {
        mEnergy = energy;
    }

    public int getProcrastinationTime() {
        return mProcrastinationTime;
    }

    public void setProcrastinationTime(int procrastinationTime) {
        mProcrastinationTime = procrastinationTime;
    }

}
