package com.practice.android.primetime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.practice.android.primetime.database.TimeBaseHelper;
import com.practice.android.primetime.database.TimeCursorWrapper;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Joseph on 6/19/16.
 */

public class TimeLab {
    private static TimeLab sTimeLab;
    private static final String PREF_NEW_DAY = "newDay";

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /*******
     * Static methods
     *******/
    private TimeLab(Context context) {
        // Create Database
        mContext = context.getApplicationContext();
        mDatabase = new TimeBaseHelper(mContext).getWritableDatabase();
        makeHours();
    }

    public static TimeLab get(Context context) {
        if (sTimeLab == null) {
            sTimeLab = new TimeLab(context);
        }

        return sTimeLab;
    }

    /*****
     * Database methods
     ******/
    public void addTimeSlot(TimeSlot slot) {
        ContentValues values = slot.toContentValues();

        // Add to database
        mDatabase.insertOrThrow(TimeTable.NAME, null, values);
    }

    public void updateTimeSlot(TimeSlot slot) {
        String uuidString = slot.getDayId().toString();
        String timeString = slot.getTime() + "";
        ContentValues values = slot.toContentValues();

        // Update the database
        mDatabase.update(TimeTable.NAME,
                values,
                TimeTable.Cols.DAY_ID + " = ? AND " + TimeTable.Cols.TIME + " = ?",
                new String[]{uuidString, timeString});
    }

    public TimeSlot getTimeSlot(UUID dayId, int time) {
        // Get the timeslot for day and time
        String whereClause = TimeTable.Cols.DAY_ID + " = ? AND " + TimeTable.Cols.TIME + " = ?";
        String[] whereArgs = new String[] {dayId.toString(), Integer.toString(time)};
        TimeCursorWrapper cursor = queryTimeSlots(whereClause, whereArgs);

        // Get the timeSlot from the cursor
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTimeSlot();
        } finally {
            cursor.close();
        }
    }

    public List<TimeSlot> getTimeSlots(UUID dayId) {
        List<TimeSlot> slots = new ArrayList<>();

        // Format the query
        String whereClause = TimeTable.Cols.DAY_ID + " = ?";
        String[] whereArgs = new String[] {dayId.toString()};
        TimeCursorWrapper cursor = queryTimeSlots(whereClause, whereArgs);

        // Return a list of all TimeSlots for that day
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                slots.add(cursor.getTimeSlot());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return slots;
    }

    /*******
     * Helper Methods
     *******/
    private TimeCursorWrapper queryTimeSlots(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null, null, null
        );

        return new TimeCursorWrapper(cursor);
    }

    private void makeHours() {
        // Check if time slots already made for that day
        boolean newDay = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(PREF_NEW_DAY, true);
        if (newDay) {
            // Create time slots
            UUID dayId = TimeSlot.TODAY_ID;
            for (int i = 7; i <= 23; i++) {
                TimeSlot ts = new TimeSlot(dayId, i, "Activity " + (i - 6));
                addTimeSlot(ts);
            }
            for (int i = 0; i < 7; i++) {
                TimeSlot ts = new TimeSlot(dayId, i, "Activity " + (i + 18));
                addTimeSlot(ts);
            }
            // Set newDay false
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putBoolean(PREF_NEW_DAY, false)
                    .apply();
        }
    }

}
