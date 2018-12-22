package com.inbalance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.inbalance.scheduler.Scheduler;

import java.util.ArrayList;

public class SchedulerDatabaseHelper {

    //Scheduler Table and Columns
    public static final String SCHEDULER_TABLE = "SCHEDULER";
    public static final String SCHEDULER_TABLE_ID = "_id";
    public static final String SCHEDULER_TABLE_NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String SCHEDULER_TABLE_TYPE = "TYPE";
    public static final String SCHEDULER_TABLE_MESSAGE = "MESSAGE";
    public static final String SCHEDULER_TABLE_DAY_1 = "DAY_1";
    public static final String SCHEDULER_TABLE_DAY_2 = "DAY_2";
    public static final String SCHEDULER_TABLE_DAY_3 = "DAY_3";
    public static final String SCHEDULER_TABLE_DAY_4 = "DAY_4";
    public static final String SCHEDULER_TABLE_DAY_5 = "DAY_5";
    public static final String SCHEDULER_TABLE_DAY_6 = "DAY_6";
    public static final String SCHEDULER_TABLE_DAY_7 = "DAY_7";
    public static final String SCHEDULER_TABLE_HOUR = "HOUR";
    public static final String SCHEDULER_TABLE_MINUTE = "MINUTE";
    public static final String SCHEDULER_TABLE_ACTIVE = "ACTIVE";
    public static final String SCHEDULER_TABLE_NEXT_RUN = "NEXT_RUN";

    public static SQLiteDatabase DB;

    private int[] days;
    private Scheduler[] schedulerList;

    public SchedulerDatabaseHelper(SQLiteDatabase db) {
        this.DB = db;
    }

    public long insertSimpleSchedule(Scheduler scheduler) {
        ContentValues scheduleValues = new ContentValues();

        //TODO Check for 7 values in int array

        scheduleValues.put(SCHEDULER_TABLE_NOTIFICATION_ID, scheduler.getNotificationID());
        scheduleValues.put(SCHEDULER_TABLE_TYPE, Scheduler.SINGLE_TYPE);
        scheduleValues.put(SCHEDULER_TABLE_MESSAGE, scheduler.getMessage());
        scheduleValues.put(SCHEDULER_TABLE_DAY_1, scheduler.getDay(0));
        scheduleValues.put(SCHEDULER_TABLE_DAY_2, scheduler.getDay(1));
        scheduleValues.put(SCHEDULER_TABLE_DAY_3, scheduler.getDay(2));
        scheduleValues.put(SCHEDULER_TABLE_DAY_4, scheduler.getDay(3));
        scheduleValues.put(SCHEDULER_TABLE_DAY_5, scheduler.getDay(4));
        scheduleValues.put(SCHEDULER_TABLE_DAY_6, scheduler.getDay(5));
        scheduleValues.put(SCHEDULER_TABLE_DAY_7, scheduler.getDay(6));
        scheduleValues.put(SCHEDULER_TABLE_HOUR,  scheduler.getHour());
        scheduleValues.put(SCHEDULER_TABLE_MINUTE, scheduler.getMinute());
        scheduleValues.put(SCHEDULER_TABLE_ACTIVE, scheduler.getActive());
        scheduleValues.put(SCHEDULER_TABLE_NEXT_RUN, scheduler.getNextRun());

        return this.DB.insert(SCHEDULER_TABLE, null, scheduleValues);
    }

    public long updateSimpleSchedule(Scheduler scheduler) {
        ContentValues scheduleValues = new ContentValues();
        int[] days = scheduler.getDays();
        int[] time = scheduler.getTime();

        scheduleValues.put(SCHEDULER_TABLE_NOTIFICATION_ID, scheduler.getNotificationID());
        scheduleValues.put(SCHEDULER_TABLE_TYPE, Scheduler.SINGLE_TYPE);
        scheduleValues.put(SCHEDULER_TABLE_MESSAGE, scheduler.getMessage());
        scheduleValues.put(SCHEDULER_TABLE_DAY_1, days[0]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_2, days[1]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_3, days[2]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_4, days[3]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_5, days[4]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_6, days[5]);
        scheduleValues.put(SCHEDULER_TABLE_DAY_7, days[6]);
        scheduleValues.put(SCHEDULER_TABLE_HOUR, time[0]);
        scheduleValues.put(SCHEDULER_TABLE_MINUTE, time[1]);
        scheduleValues.put(SCHEDULER_TABLE_ACTIVE, scheduler.getActive());

        return this.DB.update(SCHEDULER_TABLE, scheduleValues, SCHEDULER_TABLE_ID + "=?", new String[] {Integer.toString(scheduler.getID())});
    }

    public int deleteSchedule(Scheduler scheduler) {
        int done = 0;
        String stringID = Integer.toString(scheduler.getID());
        String where = "_id=";

        //Check so as not to delete all rows
        if (stringID != null || stringID.toLowerCase() != "null") {
            done = this.DB.delete(
                    SCHEDULER_TABLE,
                    SCHEDULER_TABLE_ID + "=?",
                    new String[]{stringID}
            );
        }
        return done;
    }

    public Cursor getSchedulesForNotification(int notificationID) {
       return this.DB.query(
                this.SCHEDULER_TABLE,
                null,
                this.SCHEDULER_TABLE_NOTIFICATION_ID + "=?",
                new String[] {Integer.toString(notificationID)},
                null,
                null,
                null
        );
    }

    public int updateSchedulesForNotification(int notificationID, ArrayList<Scheduler> newSchedules){
        int done = -1;

        Cursor cursor = getSchedulesForNotification(notificationID);

        ArrayList<Scheduler> currentSchedules = new ArrayList<Scheduler>(cursor.getCount());

        //if TABLE has rows
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                int pos = cursor.getPosition();
                int[] days = Scheduler.getDaysArrayFromCursor(cursor, pos);
                int[] time = Scheduler.getTimeArrayFromCursor(cursor, pos);

                Scheduler schedulerItem = new Scheduler(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        days,
                        time,
                        cursor.getInt(6)
                );

                currentSchedules.add(schedulerItem);
            } while (cursor.moveToNext());
        }

        //Check for items updated or removed in old list
        Log.d("UpdateSchedules", "Current Schedules:\n" + currentSchedules + "\n New Schedules:\n" + newSchedules);
        if (currentSchedules == null) {
            currentSchedules = new ArrayList<Scheduler>();
        }
        for (Scheduler curScheduler : currentSchedules) {
            boolean found = false;
            for (Scheduler newScheduler : newSchedules) {
                if (curScheduler.getID() == newScheduler.getID()) {
                    //Found in new items, update
                    newScheduler.setNextRun();
                    Log.d("UpdateSchedules", "Updating new Schedule " + curScheduler.getID() + " for notification " + notificationID + " with data: " + curScheduler);
                    updateSimpleSchedule(newScheduler);
                    found = true;
                    break;
                }
            }

            if (!found) {
                //Not found in new items, delete
                Log.d("UpdateSchedules", "Deleting old Schedule " + curScheduler.getID() + " for notification " + notificationID + " with data: " + curScheduler);
                deleteSchedule(curScheduler);
            }
        }

        //Check for added items
        for (Scheduler newScheduler : newSchedules) {
            if (newScheduler.getID() == -1) {
                //Insert new item
                newScheduler.setNextRun();
                Log.d("UpdateSchedules", "Adding new Schedule " + newScheduler.getID() + " for notification " + notificationID + " with data: " + newScheduler);
                insertSimpleSchedule(newScheduler);
            }
        }

        return done;
    }

    public int deleteSchedulesForNotification(int notificationID) {
        int done = -1;

        String stringID = Integer.toString(notificationID);
        String where = "_id=";

        //Check so as not to delete all rows
        if (stringID != null || stringID.toLowerCase() != "null") {
            done = DB.delete(
                    SCHEDULER_TABLE,
                    SCHEDULER_TABLE_NOTIFICATION_ID + "=?",
                    new String[] {stringID}
            );
        }
        return done;
    }

    public Scheduler[] getSimple(Boolean unique, String query, String[] columns, String[] orderBy) {
        //TODO: create method
        return schedulerList;
    }
}