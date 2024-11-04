package com.example.theremindful2;


import android.content.Intent;
import android.content.res.Resources;
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

import com.example.theremindful2.data.AppDatabase;
import com.example.theremindful2.data.Database;
import com.example.theremindful2.data.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ViewPager2 parentViewPager ;
    public ParentAdapter parentAdapter = new ParentAdapter(this);
    private final ActivityResultLauncher<Intent> upload_button_activity_result_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    result -> {
//                    Intent data = result.getData();
//                    if (data != null) {
//                        System.out.println(data.getStringExtra("filePATH"));
//                    }
        updateImages();
//        Database db = new Database(this);
//        db.getImagesByTheme("new", new Database.DatabaseCallback<List<Image>>() {
//            @Override
//            public void onSuccess(List<Image> result) {
//                System.out.println(result);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                System.out.println("got an error");
//            }
//        });

    });
//ActivityResultLauncher<Intent> upload_button_activity_result_launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialSetup();

    }
    private void initialSetup() {
        parentPagerCode();
        uploadButtonCode();
        setupDatabase();
    }
    private void parentPagerCode() {
        setContentView(R.layout.activity_main);
        parentViewPager = findViewById(R.id.parentViewPager);

        // Reference to the parent ViewPager2 for horizontal swiping between themes
//        ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);

        // Set the adapter for the parent ViewPager2
        parentViewPager.setAdapter(parentAdapter);

        // Set the orientation for horizontal swiping
        parentViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Reference to the Floating Action Button to open Caregiver Settings
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);

        // Handle click on the Floating Action Button to open Caregiver Settings Activity
        fabSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.example.theremindful2.CaregiverSettingsActivity.class);
            startActivity(intent);
        });
    }


    private void uploadButtonCode() {
        ActivityResultLauncher<Intent> FilePickerLauncher;
        // getting the upload button
        FloatingActionButton upload_button = findViewById(R.id.upload_button);

        // setting the register for FilePicker



        // setting listener
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    upload_button_activity_result_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                            result -> {
////                    Intent data = result.getData();
////                    if (data != null) {
////                        System.out.println(data.getStringExtra("filePATH"));
////                    }
//                                Database db = new Database(getApplicationContext());
//                                db.getImagesByTheme("new", new Database.DatabaseCallback<List<Image>>() {
//                                    @Override
//                                    public void onSuccess(List<Image> result) {
//                                        System.out.println(result);
//                                    }
//
//                                    @Override
//                                    public void onError(Exception e) {
//                                        System.out.println("got an error");
//                                    }
//                                });
//
//                            });
                    Intent i = new Intent(MainActivity.this, FilePicker.class);
                    upload_button_activity_result_launcher.launch(i);
//                    System.out.println("this should be after saving to database");
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void setupDatabase() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

    }
    private void updateImages() {
        Database db = new Database(this);
        db.getImagesByTheme("new", new Database.DatabaseCallback<List<Image>>() {
            @Override
            public void onSuccess(List<Image> result) {
                System.out.println(result.get(0));

//                System.out.println(theme);
                List<String> lst = new ArrayList<>();
                lst.add(String.valueOf(result.get(0)));

                parentAdapter.addTheme("new");

                parentAdapter.addImage(result.get(0), "new");

            }

            @Override
            public void onError(Exception e) {
                System.out.println("got an error");
            }
        });
    }
}
