package com.inbalance.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.inbalance.scheduler.Scheduler;

public class InBalanceDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "inbalance"; // the name of our database
    public static final int DB_VERSION = 1; // the version of the database

    protected SQLiteDatabase DB;
    protected Context context;

    public InBalanceDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        setDB(db);
        Log.d("DATABASEHELPER", "Creating Notifications table...");
        try {
            DB.execSQL("CREATE TABLE "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE
                    + " (" + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_NAME + " TEXT, "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_CATEGORY + " TEXT, "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_MESSAGE + " TEXT, "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_ACTIVE + " INTEGER, "
                    + NotificationsDatabaseHelper.NOTIFICATIONS_TABLE_NEXT_RUN + " TEXT);");
            Log.d("DATABASEHELPER", "Notifications table created.");
        } catch (SQLiteException e) {
            Log.e("DATABASEHELPER", "Error creating Notifications table: " + e.getMessage());
        }

        Log.d("DATABASEHELPER", "Creating Scheduler table...");

        try {
            DB.execSQL("CREATE TABLE "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE
                    + " (" + SchedulerDatabaseHelper.SCHEDULER_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_NOTIFICATION_ID + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_TYPE + " TEXT, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_MESSAGE + " TEXT, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_1 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_2 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_3 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_4 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_5 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_6 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_DAY_7 + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_HOUR + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_MINUTE + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_ACTIVE + " INTEGER, "
                    + SchedulerDatabaseHelper.SCHEDULER_TABLE_NEXT_RUN + " TEXT);");
            Log.d("DATABASEHELPER", "Scheduler table created.");
        } catch (SQLiteException e){
            Log.e("DATABASEHELPER", "Error creating Scheduler table: " + e.getMessage());
        }

        NotificationsDatabaseHelper ndbh = new NotificationsDatabaseHelper(getDB());
        SchedulerDatabaseHelper sdbh = new SchedulerDatabaseHelper(getDB());

        Log.d("DATABASEHELPER", "Helpers initialized.");

        long new_id = ndbh.insertNotification("Read", "","Have you read for at least 30 minutes today?");
        int[] insertDays = new int[]{1, 1, 1, 1, 1, 1, 1};
        int[] insertDays2 = new int[]{0, 0, 0, 0, 0, 1, 1};
        int[] insertDays3 = new int[]{0, 1, 0, 1, 0, 1, 0};
        if (new_id == -1) {
            Log.e("InBalanceDatabaseHelper", "Failed to insert Notification when initalizing DB.");
        } else {
            Scheduler scheduler = new Scheduler(
                    -1,
                    (int) new_id,
                    Scheduler.SINGLE_TYPE,
                    null,
                    insertDays,
                    new int[]{19, 30},
                    1
            );
            sdbh.insertSimpleSchedule(scheduler);
        }

        long new_id2 = ndbh.insertNotification("Work Out", "body","Exercise for at least 40 minutes today!");

        if (new_id2 == -1) {
            Log.e("InBalanceDatabaseHelper", "Failed to insert Notification when initalizing DB.");
        } else {
            sdbh.insertSimpleSchedule(new Scheduler(
                    -1,
                    (int) new_id2,
                    Scheduler.SINGLE_TYPE,
                    null,
                    insertDays,
                    new int[]{7, 15},
                    1
            ));
            sdbh.insertSimpleSchedule(new Scheduler(
                    -1,
                    (int) new_id2,
                    Scheduler.SINGLE_TYPE,
                    null,
                    insertDays2,
                    new int[]{7, 0},
                    1
            ));
            sdbh.insertSimpleSchedule(new Scheduler(
                    -1,
                    (int) new_id2,
                    Scheduler.SINGLE_TYPE,
                    null,
                    insertDays3,
                    new int[]{9, 45},
                    1
            ));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase getDB() {
        return DB;
    }

    public void setDB(SQLiteDatabase DB) {
        this.DB = DB;
    }
}
