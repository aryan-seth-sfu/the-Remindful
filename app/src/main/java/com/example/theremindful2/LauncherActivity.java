package com.example.theremindful2;
import android.view.MenuItem;
import android.widget.PopupMenu; // For PopupMenu functionality
import java.util.Calendar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import java.util.concurrent.TimeUnit;

public class LauncherActivity extends AppCompatActivity {

    // Create notification channel
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "DailyTaskChannel";
            String description = "Channel for daily task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("DAILY_TASK_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            } else {
                // Permission already granted, schedule the notification
                scheduleDailyNotification();
            }
        } else {
            // Permissions not required for earlier versions
            scheduleDailyNotification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                scheduleDailyNotification();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleDailyNotification() {
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

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyNotificationWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }

    private void openDailyTaskDialog() {
        DialogFragment taskDialog = new TaskDialogFragment();
        taskDialog.show(getSupportFragmentManager(), getString(R.string.taskDialogTag));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if the activity was opened via the notification
        if (intent != null && intent.getBooleanExtra("openDailyTaskDialog", false)) {
            openDailyTaskDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Create notification channel
        createNotificationChannel();

        // Request notification permission
        requestNotificationPermission();

        // Check if the activity was opened via the notification
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("openDailyTaskDialog", false)) {
            openDailyTaskDialog();
        }


        // Check if the periodic work is already scheduled
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("DailyNotificationWork")
                .observe(this, workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) {
                        scheduleDailyNotification();
                    }
                });


        Button musicButton = findViewById(R.id.musicStart);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, audioactivity.class);
                startActivity(intent);
            }
        });

        Button dailyTaskButton = findViewById(R.id.dailyTaskButton);
        dailyTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment taskDialog = new TaskDialogFragment();
                taskDialog.show(getSupportFragmentManager(), getString(R.string.taskDialogTag));
            }
        });

        Button instructionButton = findViewById(R.id.instructionButton);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, instructionPage.class);
                startActivity(intent);
            }
        });


        Button buttonOpenMain = findViewById(R.id.startButton);
        buttonOpenMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                PopupMenu popup = new PopupMenu(LauncherActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.dropdown_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId()== R.id.imageBrowsing) {
                            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        else if (menuItem.getItemId()== R.id.imageUpload){
                            Intent intent = new Intent(LauncherActivity.this, CareGiverImagesSettingsActivity.class);
                            startActivity(intent);

                        }
                        return false;
                    }

                });
            }
        });
    }
}



