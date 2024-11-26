package com.example.theremindful2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

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
                taskDialog.show(getSupportFragmentManager(), "TaskDialog");
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



