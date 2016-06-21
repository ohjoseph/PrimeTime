package com.practice.android.primetime.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.practice.android.primetime.Day;
import com.practice.android.primetime.TimeSlot;
import com.practice.android.primetime.database.TimeDbSchema.DayTable;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

import java.util.UUID;

/**
 * Created by Joseph on 6/20/16.
 */

public class TimeCursorWrapper extends CursorWrapper {
    public TimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TimeSlot getTimeSlot() {
        // Get info from cursor
        String dayId = getString(getColumnIndex(TimeTable.Cols.DAY_ID));
        String time = getString(getColumnIndex(TimeTable.Cols.TIME));
        String activity = getString(getColumnIndex(TimeTable.Cols.ACTIVITY));
        String energy = getString(getColumnIndex(TimeTable.Cols.ENERGY));
        String procrastination = getString(getColumnIndex(TimeTable.Cols.PROCRASTINATION));

        // Create TimeSlot from info
        TimeSlot timeSlot = new TimeSlot(UUID.fromString(dayId), new Integer(time), activity);
        timeSlot.setEnergy(new Integer(energy));
        timeSlot.setProcrastinationTime(new Integer(procrastination));
        return timeSlot;
    }

    public Day getDay() {
        // Get info from cursor
        String dayId = getString(getColumnIndex(DayTable.Cols.DAY_ID));
        UUID dayUUID = UUID.fromString(dayId);
        String dateString = getString(getColumnIndex(DayTable.Cols.DATE_STRING));

        // Create Day
        return new Day(dayUUID, dateString);
    }

    public UUID getDayUUID() {
        // Get info from cursor
        String dayUUID = getString(getColumnIndex(DayTable.Cols.DAY_ID));
        return UUID.fromString(dayUUID);
    }
}
