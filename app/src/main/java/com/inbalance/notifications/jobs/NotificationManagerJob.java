package com.inbalance.notifications.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.inbalance.database.InBalanceDatabaseHelper;
import com.inbalance.database.NotificationsDatabaseHelper;

import java.util.Set;

import androidx.annotation.NonNull;

public class NotificationManagerJob extends Job {

    public static final String TAG = "notification_manager_job";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        NotificationsDatabaseHelper ndbh = new NotificationsDatabaseHelper(new InBalanceDatabaseHelper(getContext()).getReadableDatabase());
        //Get Notifications
        ndbh.getActiveNotifications();

        //Calc next run for each in scheduler class

        //Save date to notification

        //Schedule new NotificationSchedulerJob to run notification

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(NotificationManagerJob.TAG);
        if (!jobRequests.isEmpty()) {
            return;
        }
        new JobRequest.Builder(NotificationManagerJob.TAG)
                .startNow()
                .setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                .build()
                .schedule();
    }
}
