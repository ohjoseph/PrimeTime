package com.practice.android.primetime;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by Joseph on 6/21/16.
 */

public class SharedPreferences {
    private static final String PREF_LAST_DAY = "last Day";

    // Get Last Day
    public static String getMostRecentDay(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_DAY, null);
    }

    // Set last day
    public static void setMostRecentDay(Context context, String today) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(PREF_LAST_DAY, today)
                .apply();
    }

    // Get Date String format
    public static String formatDate(Context context, Date date) {
        return DateFormat.getLongDateFormat(context).format(date);
    }
}
