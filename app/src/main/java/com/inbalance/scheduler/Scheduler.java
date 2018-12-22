package com.inbalance.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.inbalance.database.SchedulerDatabaseHelper;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;

public class Scheduler {

    private int id;
    private int notificationID;
    private String type;
    private String message;
    private int[] days;
    private int[] time;
    private boolean active;
    private String nextRun;
    private Context context;

    public SchedulerDatabaseHelper sdbh;

    public static final String SINGLE_TYPE = "single";
    public static final String REPEAT_TYPE = "repeat";

    private static final String[] daysOfWeekShort = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public static final String[] daysOfWeekLong = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    SimpleDateFormat iso8601Format = new SimpleDateFormat(
            "YYYY-MM-DDTHH:MM:SS.SSS");

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

    public int getDay(int idx) {
        return this.days[idx];
    }

    public int[] getTime() {
        return this.time;
    }

    public int getHour() {
        return this.time[0];
    }

    public int getMinute() {
        return this.time[1];
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

    public void setNextRun() {
        this.nextRun = calcNextRun();
    }

    public String getNextRun() {
        return (this.nextRun != null) ? this.nextRun : this.calcNextRun();
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

    private ArrayList<Integer> getUniqueDaysArraySundayFirst() {
        ArrayList<Integer> days = new ArrayList<Integer>();
        //Start list with sunday
        if (this.days[6] == 1) {
            days.add(1);
        }
        for (int i = 0; i < 6; i++) {
            if (this.days[i] == 1) {
                days.add(i + 2);
            }
        }
        return days;
    }

    public String calcNextRun() {
        String nextRun = "";

        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_WEEK);
        int currentTime = Integer.parseInt(Integer.toString(currentDate.get(Calendar.HOUR_OF_DAY)) + Integer.toString(currentDate.get(Calendar.MINUTE)));

        int schedulerTime = Integer.parseInt(Integer.toString(this.time[0]) + Integer.toString(this.time[1]));
        ArrayList<Integer> activeDays = getUniqueDaysArraySundayFirst();

        //Find next date
        if (activeDays.contains(currentDay) && schedulerTime > currentTime) {
            //Found day matching current and time is after now, schedule for today
            currentDate.set(Calendar.HOUR_OF_DAY, this.time[0]);
            currentDate.set(Calendar.MINUTE, this.time[1]);
            currentDate.set(Calendar.SECOND, 0);

            nextRun = iso8601Format.format(currentDate);
        } else if (activeDays.size() < 2) {
            //Only one day to run, schedule for next occurrence of that day
            int day = activeDays.get(0);

            nextRun = setRunToDateOfNextDay(day);
        } else {
            //Find next day
            int day = -1;

            if (currentDay != 7) {
                for (int i = currentDay + 1; i < 8; i++) {
                    if (activeDays.contains(i)) {
                        day = i;
                        break;
                    }
                }
            }
            //not found until end of week, get first instance in list
            day = day == -1 ? activeDays.get(0) : day;

            nextRun = setRunToDateOfNextDay(day);
        }

        return nextRun;
    }

    private String setRunToDateOfNextDay(int day) {
        LocalDate nextDay = LocalDate.now(ZoneId.of("UTC")).with(TemporalAdjusters.next(DayOfWeek.of(day)));
        Calendar date = Calendar.getInstance();

        //Set Date Fields
        date.set(Calendar.YEAR, nextDay.getYear());
        date.set(Calendar.MONTH, nextDay.getMonthValue());
        date.set(Calendar.DAY_OF_YEAR, nextDay.getDayOfYear());

        //Set Time Fields
        date.set(Calendar.HOUR_OF_DAY, this.time[0]);
        date.set(Calendar.MINUTE, this.time[1]);
        date.set(Calendar.SECOND, 0);

        return iso8601Format.format(date);
    }
}
