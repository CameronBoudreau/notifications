package com.inbalance.notifications;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.inbalance.database.InBalanceDatabaseHelper;
import com.inbalance.database.NotificationsDatabaseHelper;
import com.inbalance.database.SchedulerDatabaseHelper;
import com.inbalance.inbalance.R;
import com.inbalance.notifications.jobs.NotificationSchedulerJob;
import com.inbalance.scheduler.Scheduler;
import com.inbalance.scheduler.SchedulerListFragment;
import com.inbalance.utils.NotificationJobCreator;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NotificationsActivity extends AppCompatActivity implements
        NotificationsListFragment.itemListener,
        View.OnClickListener
//        SchedulerListFragment.SchedulerListFragInterface
{
    private Toolbar toolbar;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private NotificationsDatabaseHelper ndbh;
    private SchedulerDatabaseHelper sdbh;

    public ArrayList<Scheduler> oldSchedulerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        JobManager.create(this).addJobCreator(new NotificationJobCreator());
        //Set toolbar as activity app bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeDB();

        ndbh = new NotificationsDatabaseHelper(getDB());
        sdbh = new SchedulerDatabaseHelper(getDB());

        if (findViewById(R.id.notification_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            NotificationsListFragment listFragment = new NotificationsListFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            listFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.notification_fragment_container, listFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.menu_notification_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_notification:
                NotificationCreateFragment newCreateFragment = new NotificationCreateFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.notification_fragment_container, newCreateFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                return true;
            case R.id.action_save_notification:
                Notification notification;
                long newID;

                FragmentManager fm = getSupportFragmentManager();
                Fragment frag =  fm.findFragmentById(R.id.notification_fragment_container);

                SchedulerListFragment schedulerFrag;
                if (frag instanceof NotificationCreateFragment) {
                    schedulerFrag = (SchedulerListFragment) frag.getChildFragmentManager().findFragmentById(R.id.fragmentSchedulerList);
                } else {
                    schedulerFrag = (SchedulerListFragment) getSupportFragmentManager().findFragmentById(R.id.notification_edit_scheduler_fragment_container);
                }

                if (frag instanceof NotificationCreateFragment) {
                    notification = new Notification(
                            -1,
                            ((NotificationCreateFragment) frag).name,
                            ((NotificationCreateFragment) frag).category,
                            ((NotificationCreateFragment) frag).message,
                            1,
                            Notification.calcNextRun(schedulerFrag.schedulerList)
                    );

                    //Save notification
                    notification.setID((int) ndbh.insertNotification(notification));
                    newID = notification.getID();
                    if (newID == -1) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Could not insert notification: Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        NotificationSchedulerJob.scheduleJob(notification.getID(), notification.getNextRun());
                    }
                } else {
                    //Updating existing notification
                    notification = ((NotificationEditFragment) frag).notification;
                    int result = ndbh.updateNotification(notification);
                    if (result != 1) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Could not update notification: Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    newID = notification.getID();
                }

                if (newID != -1) {
                    if (schedulerFrag != null) {
                        //Use notification ID to save scheduler items
                        Log.d("SaveNotificationEdit", "Saving Schedules for ID: " + newID);
                        sdbh.updateSchedulesForNotification((int) newID, schedulerFrag.schedulerList);
                    } else {
                        Log.d("SaveNotificationEdit", "Failed to get scheduler frag: " + schedulerFrag);
                        Toast toast = Toast.makeText(getApplicationContext(), "Could not update Schedules for Notification: Unable to find scheduler list fragment", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            case R.id.action_cancel_notification:

                NotificationsListFragment newListFragment = new NotificationsListFragment();

                transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.notification_fragment_container, newListFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                this.oldSchedulerList = null;

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Close the cursor and db when fragment is destroyed
        if (this.cursor != null) {
            this.cursor.close();
            Log.d("NotificationActivity", "Cursor closed.");
        }
        this.DB.close();
        Log.d("NotificationActivity", "DB closed.");
    }

    public SQLiteDatabase getDB() {
        return this.DB;
    }

    public void setDB(SQLiteDatabase DB) {
        this.DB = DB;
    }

    private void initializeDB() {
        setDB(new InBalanceDatabaseHelper(getApplicationContext()).getReadableDatabase());
    }


    @Override
    public void editItem(Notification notification) {
        NotificationEditFragment newEditFragment = new NotificationEditFragment().newInstance(notification);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.notification_fragment_container, newEditFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        Log.d("OnClickNotifAct", "Click Listener Invoked for view: " + v.getTag());
        switch (v.getId()) {
            case R.id.button_edit_notification_delete:
                Log.d("OnClickEditNotif", "Deleting notification : " + v.getTag());
                int done = ndbh.deleteNotification((int) v.getTag());

                if (done == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Could not delete notification: Database unavailable", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    sdbh.deleteSchedulesForNotification((int) v.getTag());

                    NotificationsListFragment newListFragment = new NotificationsListFragment();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.notification_fragment_container, newListFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public void setSchedulerList(ArrayList<Scheduler> schedulerList) {
//        Log.d("SetSchedulerList", "Setting scheduler list to: " + schedulerList);
//        this.oldSchedulerList = schedulerList;
//    }

//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        if (fragment instanceof SchedulerListFragment) {
//            SchedulerListFragment schedulerListFragment = (SchedulerListFragment) fragment;
//            schedulerListFragment.setSchedulerListFragInterfaceCallback(this);
//        }
//    }

}
