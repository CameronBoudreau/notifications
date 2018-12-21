package com.inbalance.notifications;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.inbalance.database.InBalanceDatabaseHelper;
import com.inbalance.database.NotificationsDatabaseHelper;
import com.inbalance.inbalance.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class NotificationsListFragment extends Fragment implements View.OnClickListener {

    private SQLiteDatabase DB;
    private Cursor cursor;
    public ArrayList<Notification> notificationList;
    public NotificationsListAdapter adapter;
    private Context context;
    private NotificationsDatabaseHelper ndbh;

    public NotificationsListFragment() {
        // Required empty public constructor
    }

    public interface itemListener {
        void editItem(Notification notification);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.DB = new InBalanceDatabaseHelper(getContext()).getReadableDatabase();
        this.ndbh = new NotificationsDatabaseHelper(this.DB);
        View view = getView();

        ListView listNotifications = (ListView) view.findViewById(R.id.list_notifications);

        setNotificationList();

        NotificationsListAdapter listAdapter = new NotificationsListAdapter(
                getActivity().getApplicationContext(),
                R.layout.notification_item_layout,
                notificationList,
                this,
                (NotificationsActivity) getActivity()
        );

        listNotifications.setAdapter(listAdapter);
        this.adapter = listAdapter;
        Log.d("NotificationActivity", "Adapter set");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Close the cursor and db when fragment is destroyed
        if (this.cursor != null) {
            this.cursor.close();
            Log.d("NotificationListFrag", "Cursor closed.");
        }
        this.DB.close();
        Log.d("NotificationListFrag", "DB closed.");
    }

    private void setNotificationsCursor() {
        try {
            this.cursor = DB.rawQuery(String.format("SELECT * FROM %s", NotificationsDatabaseHelper.NOTIFICATIONS_TABLE), null);

        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this.context, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setNotificationList() {
        setNotificationsCursor();
        Log.i("SetNotificationList", "Cursor: " + DatabaseUtils.dumpCursorToString(this.cursor));

        this.notificationList = new ArrayList<Notification>();

        //if TABLE has rows
        if (this.cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                Notification notificationItem = new Notification(
                        this.cursor.getInt(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_ID)),
                        this.cursor.getString(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_NAME)),
                        this.cursor.getString(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_CATEGORY)),
                        this.cursor.getString(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_MESSAGE)),
                        this.cursor.getInt(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_ACTIVE)),
                        this.cursor.getString(this.cursor.getColumnIndex(ndbh.NOTIFICATIONS_TABLE_NEXT_RUN))
                );

                this.notificationList.add(notificationItem);
            } while (this.cursor.moveToNext());
        }
        Log.d("NotificationListFrag", "NotificationList length: " + this.notificationList.size());
    }

    public SQLiteDatabase getDB() {
        return this.DB;
    }

    public void setDB(SQLiteDatabase DB) {
        this.DB = DB;
    }

    public void deleteItem(View view) {
        Notification notification = notificationList.get((int) view.getTag());
        int success = ndbh.deleteNotification(notification.getID());

        if (success != 0) {
            notificationList.remove((int) view.getTag());
            Log.d("DeleteItem", String.format("SchedulerList after deleting position %s : %s", view.getTag(), notificationList));
            this.adapter.notifyDataSetChanged();
        } else {
            Toast toast = Toast.makeText(this.context, "Unable to contact database to delete item", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void updateList(Notification notification, int position) {
        Log.d("UpdateList", String.format("Updating list at position %s with new scheduler: %s", position, notification.toString()));
        notificationList.set(position, notification);
        this.adapter.notifyDataSetChanged();
        Log.d("UpdateList", String.format("notificationList after updating position %s : %s", position, notificationList));
    }

    public void toggleItemActive(Switch view) {
        int position = (int) view.getTag();
        Notification notification = this.notificationList.get(position);
        notification.toggleActive();
        ndbh.updateNotification(notification);
        Log.d("toggleSwitch", String.format("Scheduler at %s after switch toggle: %s", position, notification.toString()));
        updateList(notification, position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.switch_active):
                toggleItemActive((Switch) v);
                break;
            case (R.id.button_delete_notification):
                deleteItem(v);
                break;
            case (R.id.notification_item):
                NotificationsActivity activity = (NotificationsActivity) getActivity();
                Notification notification = this.notificationList.get((int) v.getTag());
                activity.editItem(notification);
                break;
            default:
                return;
        }
    }
}
