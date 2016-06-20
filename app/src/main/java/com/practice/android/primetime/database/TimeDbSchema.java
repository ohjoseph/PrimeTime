package com.practice.android.primetime.database;

/**
 * Created by Joseph on 6/20/16.
 */

public class TimeDbSchema {
    public static final class TimeTable {
        public static final String NAME = "TimeSlots";

        public static final class Cols {
            public static final String DAY_ID = "day_id";
            public static final String TIME = "time";
            public static final String ACTIVITY = "activity";
            public static final String ENERGY = "energy";
            public static final String PROCRASTINATION = "procrastination";
        }
    }

    public static final class DayTable {
        public static final String NAME = "Days";

        public static final class Cols {
            public static final String DAY_ID = "id";
        }
    }
}
