package com.inbalance.utils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.inbalance.notifications.jobs.NotificationManagerJob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotificationJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case NotificationManagerJob.TAG:
                return new NotificationManagerJob();
            default:
                return null;
        }
    }
}
