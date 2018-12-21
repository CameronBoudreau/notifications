package com.inbalance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.inbalance.database.InBalanceDatabaseHelper;
import com.inbalance.notifications.Notification;

import java.util.ArrayList;

public class NotificationsDatabaseHelper {

    //Notifications Table and Columns
    public static final String NOTIFICATIONS_TABLE = "NOTIFICATIONS";
    public static final String NOTIFICATIONS_TABLE_ID = "_id";
    public static final String NOTIFICATIONS_TABLE_NAME = "NAME";
    public static final String NOTIFICATIONS_TABLE_CATEGORY = "CATEGORY";
    public static final String NOTIFICATIONS_TABLE_MESSAGE = "MESSAGE";
    public static final String NOTIFICATIONS_TABLE_ACTIVE = "ACTIVE";
    public static final String NOTIFICATIONS_TABLE_NEXT_RUN = "NEXT_RUN";

    public static SQLiteDatabase DB;

    public NotificationsDatabaseHelper(SQLiteDatabase db) {
        this.DB = db;
    }

    public long insertNotification(String name, String category, String message) {
        ContentValues notificationValues = new ContentValues();

        if (category == null | category == "") {
            category = "default";
        }

        notificationValues.put(NOTIFICATIONS_TABLE_NAME, name);
        notificationValues.put(NOTIFICATIONS_TABLE_CATEGORY, category);
        notificationValues.put(NOTIFICATIONS_TABLE_MESSAGE, message);
        notificationValues.put(NOTIFICATIONS_TABLE_ACTIVE, 1);

        return DB.insert(NOTIFICATIONS_TABLE, null, notificationValues);
    }

    //Returns # of rows updated
    public int updateNotification(Notification notification) {
        ContentValues notificationValues = new ContentValues();

        notificationValues.put(NOTIFICATIONS_TABLE_NAME, notification.getName());
        notificationValues.put(NOTIFICATIONS_TABLE_CATEGORY, notification.getCategory());
        notificationValues.put(NOTIFICATIONS_TABLE_MESSAGE, notification.getMessage());
        notificationValues.put(NOTIFICATIONS_TABLE_ACTIVE, notification.getActiveInt());

        return DB.update(
                NOTIFICATIONS_TABLE,
                notificationValues,
                "_id=?",
                new String[] {notification.getIDString()});
    }

    public int deleteNotification(int id) {
        int done = 0;
        String stringID = Integer.toString(id);
        String where = "_id=";

        //Check so as not to delete all rows
        if (stringID != null || stringID.toLowerCase() != "null") {
            done = DB.delete(
                    NOTIFICATIONS_TABLE,
                    NOTIFICATIONS_TABLE_ID + "=?",
                    new String[] {stringID});
        }

        return done;
    }

    public ArrayList<Notification> getActiveNotifications() {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        Cursor cursor = this.DB.query(
                this.NOTIFICATIONS_TABLE,
                null,
                this.NOTIFICATIONS_TABLE_ACTIVE + "=?",
                new String[] {"1"},
                null,
                null,
                null
        );

        //if TABLE has rows
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                Notification notificationItem = new Notification(
                        cursor.getInt(cursor.getColumnIndex(NOTIFICATIONS_TABLE_ID)),
                        cursor.getString(cursor.getColumnIndex(NOTIFICATIONS_TABLE_NAME)),
                        cursor.getString(cursor.getColumnIndex(NOTIFICATIONS_TABLE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(NOTIFICATIONS_TABLE_MESSAGE)),
                        cursor.getInt(cursor.getColumnIndex(NOTIFICATIONS_TABLE_ACTIVE)),
                        cursor.getString(cursor.getColumnIndex(NOTIFICATIONS_TABLE_NEXT_RUN))
                );

                notifications.add(notificationItem);
            } while (cursor.moveToNext());
        }
        return notifications;
    }
}
