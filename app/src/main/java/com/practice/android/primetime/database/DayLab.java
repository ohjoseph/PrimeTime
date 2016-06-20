package com.practice.android.primetime.database;

import android.content.Context;

/**
 * Created by Joseph on 6/20/16.
 */

public class DayLab {
    private static DayLab sDayLab;

    private Context mContext;

    public static DayLab get(Context context) {
        if (sDayLab == null) {
            sDayLab = new DayLab(context);
        }

        return sDayLab;
    }

    private DayLab(Context context) {
        mContext = context.getApplicationContext();
    }
}
