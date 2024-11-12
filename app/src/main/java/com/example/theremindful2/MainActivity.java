package com.example.theremindful2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

//    private Button u_button;
//    Uri Image;
private ActivityResultLauncher<Intent> FilePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to the parent ViewPager2 for horizontal swiping between themes
        ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);

        // Set the adapter for the parent ViewPager2
        parentViewPager.setAdapter(new ParentAdapter(this));

        // Set the orientation for horizontal swiping
        parentViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Reference to the Floating Action Button to open Caregiver Settings
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);

        // Handle click on the Floating Action Button to open Caregiver Settings Activity
        fabSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.example.theremindful2.CaregiverSettingsActivity.class);
            startActivity(intent);
        });

        // getting the upload button
        FloatingActionButton upload_button = findViewById(R.id.upload_button);

        // setting the register for FilePicker
        FilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        System.out.println(data.getStringExtra("filePATH"));
                    }
                });


        // setting listener
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create and start the intent
//                    Intent i = new Intent(MainActivity.this, com.example.theremindful2.FilePicker.class);
//
//                    setContentView(R.layout.activity_main);
//
//                    startActivity(i);  // Use startActivityForResult instead of startActivity

                    Intent i = new Intent(MainActivity.this, FilePicker.class);
                    FilePickerLauncher.launch(i);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
                }

            }

        });

        // Daily Task Feature
        FloatingActionButton fabTaskBook = findViewById(R.id.fabTaskBook);
        fabTaskBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the TaskDialogFragment
                DialogFragment taskDialog = new TaskDialogFragment();
                taskDialog.show(getSupportFragmentManager(), "TaskDialog");
            }
        });




    }
}
