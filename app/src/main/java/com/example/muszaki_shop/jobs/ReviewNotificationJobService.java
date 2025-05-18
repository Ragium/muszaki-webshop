package com.example.muszaki_shop.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.muszaki_shop.activities.MainActivity;
import com.example.muszaki_shop.notifications.ReviewNotificationHelper;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ReviewNotificationJobService extends JobService {
    private static final String EXTRA_PRODUCT_NAME = "product_name";

    @Override
    public boolean onStartJob(JobParameters params) {
        String productName = params.getExtras().getString(EXTRA_PRODUCT_NAME, "termék");
        
        ReviewNotificationHelper notificationHelper = new ReviewNotificationHelper(this);
        notificationHelper.showReviewSubmittedNotification(productName);
        
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
} 