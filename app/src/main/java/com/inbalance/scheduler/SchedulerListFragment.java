package com.inbalance.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.inbalance.database.InBalanceDatabaseHelper;
import com.inbalance.database.SchedulerDatabaseHelper;
import com.inbalance.inbalance.R;
import com.inbalance.notifications.NotificationsActivity;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SchedulerListFragment extends Fragment implements
        View.OnClickListener,
        View.OnFocusChangeListener,
        DayOfWeekDialogFragment.NoticeDialogListener,
        TimePickerDialogFragment.OnTimeSetListener
{

    private SQLiteDatabase DB;
    private Cursor cursor;
    private View view;
    private Context context;
    public ArrayList<Scheduler> schedulerList;
    public SchedulerListAdapter adapter;
    private  ArrayList<String> daysString;
    private SchedulerDatabaseHelper sdbh;
//    private SchedulerListFragInterface listInterfaceCallback;

    public int notificationID = -1;

    public SchedulerListFragment() {
        // Required empty public constructor
    }

    public SchedulerListFragment newInstance(int notificationID) {
        SchedulerListFragment frag = new SchedulerListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("id", notificationID);
        frag.setArguments(bundle);

        return frag;
    }

//    public interface SchedulerListFragInterface {
//        void setSchedulerList(ArrayList<Scheduler> schedulerList);
//    }

//    public void setSchedulerListFragInterfaceCallback(NotificationsActivity activity) {
//        this.listInterfaceCallback = activity;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scheduler_list, container, false);
        this.context = getActivity().getApplicationContext();
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.notificationID = bundle.getInt("id", -1);
        }
        this.DB = new InBalanceDatabaseHelper(getContext()).getReadableDatabase();
        this.sdbh = new SchedulerDatabaseHelper(this.DB);

        ListView listSchedules = (ListView) view.findViewById(R.id.list_schedulers);

        if (this.notificationID != -1) {
            Log.d("SchedulerListFrag", "Adding item to empty array");
            //Get schedulers from DB for given notification
            SQLiteOpenHelper inBalanceDatabaseHelper = new InBalanceDatabaseHelper(this.context);

            try {
                DB = inBalanceDatabaseHelper.getReadableDatabase();
                setDB(DB);

                cursor = sdbh.getSchedulesForNotification(this.notificationID);

                setSchedulerList(cursor);

//                Log.d("SchedulerListFrag", "Scheduler length: " + this.schedulerList.size());

                //TODO: make sure removing this is OK - it's in onDelete.
                cursor.close();
                DB.close();
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Log.d("SchedulerListFrag", "Adding item to empty array");
            addInitialItem();
        }

        Log.d("SchedulerListFrag", "Setting adapter");
        this.adapter = new SchedulerListAdapter(
                getActivity().getApplicationContext(),
                R.layout.notification_item_layout,
                this.schedulerList,
                this,
                this.notificationID
        );
        listSchedules.setAdapter(this.adapter);
        Log.d("SchedulerListFrag", "Adapter set");

        Button addbutton = (Button) view.findViewById(R.id.addBtn);
        addbutton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Close the cursor and DB when activity is destroyed
        if (this.cursor != null) {
            this.cursor.close();
            Log.d("NotificationActivity", "Cursor closed.");
        }
        if (this.DB != null) {
            this.DB.close();
        }
        Log.d("NotificationActivity", "DB closed.");
    }

    private void addInitialItem() {
        schedulerList = schedulerList == null ? new ArrayList<Scheduler>() : schedulerList;
        this.schedulerList.add(createDefaultSingleSchedule());
    }

    private void addItem(View v) {
        this.schedulerList.add(createDefaultSingleSchedule());
        Log.d("SchedulerListFrag", "Item added, notifying adapter of change. list: " + schedulerList.toString());
        this.adapter.notifyDataSetChanged();
    }

    private void setSchedulerList(Cursor cursor) {
        Log.i("SETSCHEDULERLIST", "Cursor: " + DatabaseUtils.dumpCursorToString(cursor));

        this.schedulerList = new ArrayList<Scheduler>(cursor.getCount());

        //if TABLE has rows
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                int pos = cursor.getPosition();
                int[] days = Scheduler.getDaysArrayFromCursor(cursor, pos);
                int[] time = Scheduler.getTimeArrayFromCursor(cursor, pos);
                Log.d("SetSchedulerList", "Position " + cursor.getPosition() + " Active From Cursor: " + cursor.getInt(6));
                Scheduler schedulerItem = new Scheduler(
                        cursor.getInt(cursor.getColumnIndex(sdbh.SCHEDULER_TABLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(sdbh.SCHEDULER_TABLE_NOTIFICATION_ID)),
                        cursor.getString(cursor.getColumnIndex(sdbh.SCHEDULER_TABLE_TYPE)),
                        cursor.getString(cursor.getColumnIndex(sdbh.SCHEDULER_TABLE_MESSAGE)),
                        days,
                        time,
                        cursor.getInt(cursor.getColumnIndex(sdbh.SCHEDULER_TABLE_ACTIVE))
                );

                this.schedulerList.add(schedulerItem);
            } while (cursor.moveToNext());
        } else addInitialItem();
    }

    public SQLiteDatabase getDB() {
        return DB;
    }

    public void setDB(SQLiteDatabase DB) {
        this.DB = DB;
    }

    private Scheduler createDefaultSingleSchedule() {
        return new Scheduler(
                -1,
                -1,
                Scheduler.SINGLE_TYPE,
                "",
                new int[]{1,1,1,1,1,1,1},
                new int[]{12, 0},
                1
        );
    }

    public void updateList(Scheduler scheduler, int position) {
        Log.d("UpdateList", String.format("Updating list at position %s with new scheduler: %s", position, scheduler.toString()));
        schedulerList.set(position, scheduler);
        this.adapter.notifyDataSetChanged();
        Log.d("UpdateList", String.format("SchedulerList after updating position %s : %s", position, schedulerList));
    }

    public void toggleSwitch(View view) {
        int position = (int) view.getTag();
        Scheduler scheduler = this.schedulerList.get(position);
        scheduler.toggleActive();
        Log.d("toggleSwitch", String.format("Scheduler at %s after switch toggle: %s", position, scheduler.toString()));
        updateList(scheduler, position);
    }

    public void deleteItem(View v) {
        schedulerList.remove((int) v.getTag());
        Log.d("DeleteItem", String.format("SchedulerList after deleting position %s : %s", v.getTag(), schedulerList));
        this.adapter.notifyDataSetChanged();
    }

    public void updateMessage(Scheduler scheduler, int position, String message) {
        scheduler.setMessage(message);
        Log.d("updateMessage", String.format("Scheduler after updating with new message '%s' : %s", message, schedulerList));
        updateList(scheduler, position);
    }

    @Override
    public void onClick ( View view ) {
        // view is the row view returned by getView
        // The position is stored as tag, so it can be retrieved using getTag ()
        int id = view.getId();
        Log.d("OnClickSchedulerList", String.format("ID: %s", id));
        switch (id) {
            case R.id.button_delete_schedule:
                Log.d("OnClickSchedulerList", "Delete clicked for view: " + view);
                deleteItem(view);
                break;
            case R.id.addBtn:
                addItem(view);
                break;
            case R.id.switch_scheduler_active:
                toggleSwitch(view);
                break;
            case R.id.button_days:
                openDaysDialog(view);
                break;
            case R.id.button_time:
                Log.d("OnClickTimeBtn","Time button clicked.");
                openTimeDialog(view);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.editText_message:
                if (!hasFocus) {
                    int idx = (int) v.getTag();
                    String message = ((EditText) v).getText().toString();
                    Scheduler scheduler = schedulerList.get(idx);
                    Log.d("MessageEdit", String.format("Message being saved after focus loss."));
                    updateMessage(scheduler, idx, message);
                }
                break;
        }

    }

    private void openDaysDialog(View view) {
        int pos = (int)view.getTag();
        Scheduler scheduler = this.schedulerList.get(pos);
        int[] days = scheduler.getDays();
        DialogFragment dialog = new DayOfWeekDialogFragment().newInstance(pos, days);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "DayOfWeekDialogFragment");
    }

    private void openTimeDialog(View view) {
        int pos = (int)view.getTag();
        Scheduler scheduler = this.schedulerList.get(pos);
        int[] time = scheduler.getTime();
        Log.d("OpenTimeDialog", String.format("scheduler in Open dialog: " + scheduler.toString()));
        DialogFragment dialog = new TimePickerDialogFragment().newInstance(pos, time);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "TimePickerDialogFragment");
    }

    @Override
    public void onDialogPositiveClick( ArrayList<Integer> checked, int position) {
        Log.d("DialogPosClick", String.format("Checked List: %s", checked));
        int[] days = new int[7];
        for (int i = 0; i <7; i++) {
            days[i] = (checked.contains(i + 1)) ? 1 : 0;
        }
        updateDays(days, position);
    }

    private void updateDays(int[] days, int position) {
        Scheduler scheduler = this.schedulerList.get(position);
        scheduler.setDays(days);
        Log.d("updateDays", String.format("Updating days on scheduler at position %s with: %s", position, scheduler));
        updateList(scheduler, position);
    }

    @Override
    public void returnTime(TimePicker view, int hourOfDay, int minute, int pos) {
        Scheduler scheduler = this.schedulerList.get(pos);
        int[] timeArray = new int[] {hourOfDay, minute};
        scheduler.setTime(timeArray);
        updateList(scheduler, pos);
    }
}
