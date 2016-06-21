package com.practice.android.primetime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practice.android.primetime.database.TimeDbSchema.DayTable;
import com.practice.android.primetime.database.TimeDbSchema.TimeTable;

/**
 * Created by Joseph on 6/20/16.
 */

public class TimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "timeBase.db";

    public TimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the database for timeslots
        sqLiteDatabase.execSQL("create table " + TimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TimeTable.Cols.DAY_ID + ", " +
                TimeTable.Cols.TIME + ", " +
                TimeTable.Cols.ACTIVITY + ", " +
                TimeTable.Cols.ENERGY + ", " +
                TimeTable.Cols.PROCRASTINATION + ")"
        );

        // Create the database for days
        sqLiteDatabase.execSQL("create table " + DayTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
                DayTable.Cols.DAY_ID + ", " +
                DayTable.Cols.DATE_STRING + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Do nothing here
    }
}
