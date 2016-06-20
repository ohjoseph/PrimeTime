package com.practice.android.primetime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /*******
     * Static methods
     *******/
    private TimeLab(Context context) {
        // Create Database
        mContext = context;
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
        mDatabase.insert(TimeTable.NAME, null, values);
    }

    public void updateTimeSlot(TimeSlot slot) {
        String uuidString = slot.getDayId().toString();
        String timeString = slot.getTime() + "";
        ContentValues values = slot.toContentValues();

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

        String whereClause = TimeTable.Cols.DAY_ID + " = ?";
        String[] whereArgs = new String[] {dayId.toString()};
        TimeCursorWrapper cursor = queryTimeSlots(whereClause, whereArgs);

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
        UUID dayId = TimeSlot.TODAY_ID;
        for (int i = 7; i <= 23; i++) {
            TimeSlot ts = new TimeSlot(dayId, i, "Activity " + i);
            addTimeSlot(ts);
        }
        for (int i = 0; i < 7; i++) {
            TimeSlot ts = new TimeSlot(dayId, i, "Activity " + i);
            addTimeSlot(ts);
        }
    }

}