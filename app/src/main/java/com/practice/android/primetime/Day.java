package com.practice.android.primetime;

import java.util.UUID;

/**
 * Created by Joseph on 6/20/16.
 */

public class Day {
    private UUID mDayId;
    private String mDateString;

    public Day(UUID dayId, String longDate) {
        mDayId = dayId;
        mDateString = longDate;
    }

    public UUID getDayId() {
        return mDayId;
    }

    public void setDayId(UUID dayId) {
        mDayId = dayId;
    }

    public String getDateString() {
        return mDateString;
    }

    public void setDateString(String dateString) {
        mDateString = dateString;
    }
}
