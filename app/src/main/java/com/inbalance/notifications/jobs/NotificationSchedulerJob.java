package com.inbalance.notifications.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.Set;

import androidx.annotation.NonNull;

public class NotificationSchedulerJob extends Job {
    public static final String TAG = "notification_factory_job";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return Result.SUCCESS;
    }

    public static void scheduleJob(int notificationID, String when) {
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(NotificationSchedulerJob.TAG);

        if (!jobRequests.isEmpty()) {
            //TODO:
            //Job exists, cancel it - currently keeps old version
            return;
        }

        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("notificationID", notificationID);

        new JobRequest.Builder(NotificationSchedulerJob.TAG)
                .startNow()
                .setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                .build()
                .schedule();
    }
}
