package com.example.theremindful2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Re-schedule the notification
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20); // Set to 8 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= currentTimeMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long initialDelay = calendar.getTimeInMillis() - currentTimeMillis;

        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
                DailyNotificationWorker.class,
                1, TimeUnit.DAYS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyNotificationWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }
}
