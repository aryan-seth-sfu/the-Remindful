package com.example.theremindful2;

import android.app.NotificationChannel; // For creating notification channels
import android.app.NotificationManager; // For managing notifications
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull; // For NonNull annotation
import androidx.core.app.ActivityCompat; // For ActivityCompat
import androidx.core.content.ContextCompat; // For ContextCompat
import android.Manifest; // For permissions
import android.content.pm.PackageManager; // For checking permissions
import android.widget.Toast; // For Toast messages
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

public class LauncherActivity extends AppCompatActivity {

    // Method to create a notification channel
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

    // Method to request notification permission
    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Request notification permission
        requestNotificationPermission();

        // Schedule daily notification
        NotificationScheduler.scheduleDailyNotification(this);

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



