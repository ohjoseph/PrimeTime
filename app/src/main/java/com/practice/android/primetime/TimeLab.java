package com.practice.android.primetime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.practice.android.primetime.database.TimeBaseHelper;
import com.practice.android.primetime.database.TimeCursorWrapper;
import com.practice.android.primetime.database.TimeDbSchema.DayTable;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Joseph on 6/19/16.
 */

public class TimeLab {
    private static TimeLab sTimeLab;
    public static final String PREF_NEW_DAY = "newDay";
    public static final String PREF_LAST_DAY = "last Day";

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /*******
     * Static methods
     *******/
    private TimeLab(Context context) {
        // Create Database
        mContext = context.getApplicationContext();
        mDatabase = new TimeBaseHelper(mContext).getWritableDatabase();
        makeNewDay();
    }

    public static TimeLab get(Context context) {
        if (sTimeLab == null) {
            sTimeLab = new TimeLab(context);
        }

        return sTimeLab;
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

    public List<TimeSlot> getTimeSlots(String dateString) {
        UUID dayId = getDayId(dateString);
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
            return cursor.getDayUUID();
        } finally {
            cursor.close();
        }
    }

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

    /*******
     * Helper Methods
     *******/
    public void makeNewDay() {
        // Check if time slots already made for that day
        String lastDay = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_LAST_DAY, "");
        String today = formatDate(new Date());

        if (!today.equals(lastDay)) {
            // Create a new Day
            UUID uuid = UUID.randomUUID();
            Day day = new Day(uuid, today);
            addDay(day);
            // Set last day
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit().putString(PREF_LAST_DAY, today)
                    .apply();

            // Create time slots
            UUID dayId = day.getDayId();
            for (int i = 7; i <= 23; i++) {
                TimeSlot ts = new TimeSlot(dayId, i, "Activity " + (i - 6));
                addTimeSlot(ts);
            }
            for (int i = 0; i < 7; i++) {
                TimeSlot ts = new TimeSlot(dayId, i, "Activity " + (i + 18));
                addTimeSlot(ts);
            }
        }
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

    private TimeCursorWrapper queryDay(String dateString) {
        Cursor cursor = mDatabase.query(
                DayTable.NAME,
                new String[] {DayTable.Cols.DAY_ID},
                DayTable.Cols.DATE_STRING + " = ?",
                new String[] {dateString},
                null, null, null
        );

        return new TimeCursorWrapper(cursor);
    }

    public String formatDate(Date date) {
         return DateFormat.getLongDateFormat(mContext).format(date);
    }

    public String getLastDay() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(TimeLab.PREF_LAST_DAY, formatDate(new Date()));
    }

}
