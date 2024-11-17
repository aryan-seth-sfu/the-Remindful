package com.example.theremindful2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> FilePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load themes from storage
        List<Theme> themes = MetadataUtils.loadThemesFromStorage(this);

        // Reference to the parent ViewPager2 for horizontal swiping between themes
        ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);

        // Set the adapter for the parent ViewPager2 with loaded themes
        parentViewPager.setAdapter(new ParentAdapter(this, themes));

        // Set the orientation for horizontal swiping
        parentViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Reference to the Floating Action Button to open Caregiver Settings
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);

        // Handle click on the Floating Action Button to open Caregiver Settings Activity
        fabSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.example.theremindful2.CaregiverSettingsActivity.class);
            startActivity(intent);
        });

        // Getting the upload button
        FloatingActionButton upload_button = findViewById(R.id.upload_button);

        // Setting the register for FilePicker
        FilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        String filePath = data.getStringExtra("filePATH");
                        if (filePath != null) {
                            // Handle the picked file
                            Toast.makeText(MainActivity.this, "File picked: " + filePath, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Setting listener for the upload button
        upload_button.setOnClickListener(v -> {
            try {
                Intent i = new Intent(MainActivity.this, FilePicker.class);
                FilePickerLauncher.launch(i);
            } catch (Exception e) {
                Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
