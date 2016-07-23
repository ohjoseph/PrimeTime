package com.practice.android.primetime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.practice.android.primetime.database.TimeBaseHelper;
import com.practice.android.primetime.database.TimeCursorWrapper;
import com.practice.android.primetime.database.TimeDbSchema.DayTable;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Joseph on 6/19/16.
 */

public class TimeLab {
    private static final String TAG = "TimeLab";
    private static TimeLab sTimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /*******
     * Static methods
     *******/
    private TimeLab(Context context) {
        // Create Database
        mContext = context.getApplicationContext();
        mDatabase = new TimeBaseHelper(mContext).getWritableDatabase();
        initializeNewDay();
    }

    public static TimeLab get(Context context) {
        if (sTimeLab == null) {
            sTimeLab = new TimeLab(context);
        }

        return sTimeLab;
    }

    public static void nullifyInstance() {
        sTimeLab = null;
    }

    /*****
     * TimeSlot Database methods
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

    public List<TimeSlot> getTimeSlots(String dateString) {
        UUID dayId = getDayId(dateString);
        List<TimeSlot> slots = new ArrayList<>();

        // Check if day exists
        if (dayId != null) {
            // Format the query
            String whereClause = TimeTable.Cols.DAY_ID + " = ?";
            String[] whereArgs = new String[]{dayId.toString()};
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
        } else {
            // Make the TimeSlots for that day
            Log.e(TAG, "Error: DayID was null.");
        }

        return slots;
    }

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

    /*******
     * Day Database Methods
     *******/
    public void addDay(Day day) {
        ContentValues values = new ContentValues();
        values.put(DayTable.Cols.DAY_ID, day.getDayId().toString());
        values.put(DayTable.Cols.DATE_STRING, day.getDateString());

        mDatabase.insertOrThrow(DayTable.NAME, null, values);
    }

    public UUID getDayId(String dateString) {
        // Get the UUID of corresponding date
        TimeCursorWrapper cursor = queryDay(dateString);
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                return cursor.getDayUUID();
            }
        } finally {
            cursor.close();
        }
        // Return null if no day found
        return null;
    }

    // Get a list of all the days
    public List<Day> getDays() {
        // Get the list of all Days
        List<Day> dayList = new ArrayList<>();
        TimeCursorWrapper cursor = queryDays();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Day day = cursor.getDay();
                dayList.add(day);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return dayList;
    }

    // Remove a day's timeslots from database
    public void deleteDay(String dateString) {
        // Delete the TimeSlots of that day
        String dayId = "";
        TimeCursorWrapper cursor = queryDay(dateString);
        try {
            // Check cursor for null value
            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                Log.e(TAG, "Error: Could not find DayID for deletion.");
                return;
            }
            dayId = cursor.getDayUUID().toString();
        } finally {
            cursor.close();
        }
        mDatabase.delete(TimeTable.NAME,
                TimeTable.Cols.DAY_ID + " = ?",
                new String[]{dayId});

        // Delete the Day
        mDatabase.delete(DayTable.NAME,
                DayTable.Cols.DATE_STRING + " = ?",
                new String[]{dateString});
    }

    // Gets the list of all days
    private TimeCursorWrapper queryDays() {
        Cursor cursor = mDatabase.query(
                DayTable.NAME,
                null,
                null,
                null,
                null, null, null
        );

        return new TimeCursorWrapper(cursor);
    }

    // Return a cursor with single day's timeslots
    private TimeCursorWrapper queryDay(String dateString) {
        Cursor cursor = mDatabase.query(
                DayTable.NAME,
                new String[]{DayTable.Cols.DAY_ID},
                DayTable.Cols.DATE_STRING + " = ?",
                new String[] {dateString},
                null, null, null
        );

        return new TimeCursorWrapper(cursor);
    }

    /*******
     * Helper Methods
     *******/
    private void initializeNewDay() {
        // Check if time slots already made for that day
        String lastDay = SharedPreferences.getMostRecentDay(mContext);
        String today = SharedPreferences.formatDate(mContext, new Date());

        // Make a new day only if not already existing
        if (!today.equals(lastDay)) {
            createToday();
        }
    }

    public void createToday() {
        // Make today's TimeSlots
        String today = SharedPreferences.formatDate(mContext, new Date());
        createNewDay(today);

        // Update to current day
        SharedPreferences.setMostRecentDay(mContext, today);
    }

    private void createNewDay(String dateString) {
        // Delete old time slots, if existing
        deleteDay(dateString);

        // Create a new Day
        UUID uuid = UUID.randomUUID();
        Day day = new Day(uuid, dateString);
        addDay(day);

        // Create time slots
        UUID dayId = day.getDayId();
        for (int i = 7; i <= 23; i++) {
            TimeSlot ts = new TimeSlot(dayId, i, "Sleep");
            addTimeSlot(ts);
        }
        for (int i = 0; i < 7; i++) {
            TimeSlot ts = new TimeSlot(dayId, i, "Sleep");
            addTimeSlot(ts);
        }
    }

    public List<TimeSlot> getAllTimeSlots() {
        List<Day> dayList = getDays();
        List<TimeSlot> allSlots = new ArrayList<>();
        for (Day d : dayList) {
            List<TimeSlot> slots = getTimeSlots(d.getDateString());
            for (TimeSlot slot : slots) {
                allSlots.add(slot);
            }
        }

        return allSlots;
    }
}
