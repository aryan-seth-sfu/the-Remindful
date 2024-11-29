package com.example.theremindful2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DailyNotificationWorker extends Worker {

    public DailyNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Create the notification channel (if needed)
        createNotificationChannel();

        // Trigger the notification
        showNotification();

        return Result.success();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "DAILY_TASK_CHANNEL",
                    "Daily Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for daily task reminders");
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification() {
        // Retrieve the daily task from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get today's date as a string (e.g., "2024-11-30")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        // Retrieve the last assigned date and task
        String lastAssignedDate = sharedPreferences.getString("lastAssignedDate", "");
        String taskDescription = sharedPreferences.getString("selectedTask", "");

        // If the date has changed, assign a new task
        if (!todayDate.equals(lastAssignedDate) || taskDescription.isEmpty()) {
            String[] tasks = {
                    "Take a picture of something red",
                    "Capture the reflection of yourself in a mirror or water",
                    "Find and photograph your favorite book or magazine",
                    "Take a picture of a cozy corner in your home",
                    "Capture a moment of sunlight streaming through a window",
                    "Photograph something that smells nice, like a flower or candle",
                    "Take a picture of something furry, like a pet or a stuffed animal",
                    "Capture an item that reminds you of a happy memory",
                    "Photograph your favorite piece of clothing",
                    "Find something that makes a soothing sound and take a picture of it",
                    "Take a picture of something you use every day",
                    "Capture an object that has your favorite color",
                    "Take a picture of a pair of shoes or slippers",
                    "Find and photograph something old and meaningful to you",
                    "Capture the cover of your favorite music album or movie",
                    "Take a picture of your favorite mug or glass",
                    "Photograph a piece of art or decoration in your home",
                    "Capture a sunrise or sunset if possible",
                    "Take a picture of something you’ve recently cleaned or organized",
                    "Find and photograph something with an interesting texture",
                    "Capture a memory by taking a picture of your favorite room",
                    "Take a picture of something you’ve written or drawn recently",
                    "Photograph a tool or item you use for cooking or baking",
                    "Take a picture of something you’d give as a gift",
                    "Capture a moment with a friend or family member (with their permission)",
                    "Take a picture of something you’d like to share with someone else",
                    "Find and photograph a clock or watch showing the current time",
                    "Take a picture of something you’ve recently enjoyed eating",
                    "Capture a flower or leaf you find outside",
                    "Photograph an object that you’d like to keep forever"
            };

            // Assign a random task
            Random random = new Random();
            taskDescription = tasks[random.nextInt(tasks.length)];

            // Save the new task and today's date
            editor.putString("selectedTask", taskDescription);
            editor.putString("lastAssignedDate", todayDate);
            editor.apply();
        }

        // Create an intent to open LauncherActivity and show the task dialog
        Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
        intent.putExtra("openDailyTaskDialog", true); // Pass a flag to indicate the dialog should open
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification with the task and pending intent
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "DAILY_TASK_CHANNEL")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Task Reminder")
                .setContentText("Please complete today's task!\n" + taskDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Please complete today's task!\n" + taskDescription))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Attach the PendingIntent

        NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }


}
