package com.inbalance.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.inbalance.database.SchedulerDatabaseHelper;

import java.time.DayOfWeek;
import java.util.ArrayList;

public class Scheduler {

    private int id;
    private int notificationID;
    private String type;
    private String message;
    private int[] days;
    private int[] time;
    private boolean active;
    private Context context;

    public SchedulerDatabaseHelper sdbh;

    public static final String SINGLE_TYPE = "single";
    public static final String REPEAT_TYPE = "repeat";

    private static final String[] daysOfWeekShort = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public static final String[] daysOfWeekLong = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public Scheduler(int id, int notificationID, String type, String message, int[] days, int[] time, int active) {
        this.id = id;
        this.notificationID = notificationID;
        this.type = type;
        this.message = message;
        this.days = days;
        this.time = time;
        this.active = active == 1;
        this.context = context;
    }

    //Getters
    public int getID() {
        return this.id;
    }

    public int getNotificationID() {
        return this.notificationID;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public int[] getDays() {
        return this.days;
    }

    public int[] getTime() {
        return this.time;
    }

    public String getTimeString() {
        String timeString = "";
        int hour = this.time[0];
        String hourString;
        String minuteString = Integer.toString(this.time[1]);
        Log.d("getTimeString", "minute: " + minuteString);
        if (minuteString.length() == 1) {
            if (minuteString == "0") {
                minuteString += "0";
            } else {
                minuteString = "0" + minuteString;
            }
        }

        if (hour - 12 < 0) {
            timeString += Integer.toString(hour) + ":" + minuteString + " AM";
        } else {
            if (hour == 12) {
                hourString = Integer.toString(hour);
            } else {
                hourString = Integer.toString(hour - 12);
            }

            timeString += hourString + ":" + minuteString+ " PM";
        }

        return timeString;
    }

    public static String getTimeString(int[] time) {
        String timeString = "";
        int hour = time[0];
        String hourString;
        String minuteString = Integer.toString(time[1]);
        Log.d("getTimeString", "minute: " + minuteString);
        if (minuteString.length() == 1) {
            if (minuteString == "0") {
                minuteString += "0";
            } else {
                minuteString = "0" + minuteString;
            }
        }

        if (hour - 12 < 0) {
            timeString += Integer.toString(hour) + ":" + minuteString + " AM";
        } else {
            if (hour == 12) {
                hourString = Integer.toString(hour);
            } else {
                hourString = Integer.toString(hour - 12);
            }

            timeString += hourString + ":" + minuteString+ " PM";
        }

        return timeString;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean to) {
        this.active = to;
    }

    public boolean toggleActive() {
        return this.active = this.active ? false : true;
    }

    public String setMessage(String message) {
        //TODO: add validation
        this.message = message;
        return this.message;
    }

    public int[] setDays(int[] days) {
        this.days = days;
        return this.days;
    }

    public static int[] getDaysArrayFromCursor(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        int[] dayList = new int[7];

        for (int i = 0; i < 7; i++) {
            int idx = cursor.getColumnIndex("DAY_" + Integer.toString(i +1));
            dayList[i] = cursor.getInt(idx);
        }
        return dayList;
    }

    public static int[] getTimeArrayFromCursor(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        int[] timeList = new int[2];
        int idx = cursor.getColumnIndex(SchedulerDatabaseHelper.SCHEDULER_TABLE_HOUR);
        timeList[0] = cursor.getInt(idx);
        idx = cursor.getColumnIndex(SchedulerDatabaseHelper.SCHEDULER_TABLE_MINUTE);
        timeList[1] = cursor.getInt(idx);
        return timeList;
    }

    public int[] setTime(int[] time) {
        this.time = time;
        return this.time;
    }

    public String toString() {
        return  "ID: " + this.id + "\nNotificationID: " + this.notificationID + "\nType: " + this.type +"\nMessage: " + this.message + "\nDays: " + this.getDaysAsString() + "\nTime: " + this.getTimeString() + "\nActive: " + this.active;
    }

    public String getDaysAsString() {
        String daysString = "";
        if (days != null) {
            for (int i=0; i < 7; i++) {
                if (days[i] == 1) {
                    if (!daysString.equals("")) {
                        daysString += ", ";
                    }
                    daysString += daysOfWeekShort[i];
                }
            }
        }
        return daysString;
    }

    public String getNextRunForNotification(int notificationID) {
        String time = "";

        //Get all active schedulers

        //Find any with day matching today
            //If found, which have a time > now
                //If multiple, schedule for first
            //If not found continue to next day
        //If not found, go to next day (or first if it was the last day)


        return time;
    }
}
