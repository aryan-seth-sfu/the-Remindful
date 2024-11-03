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

public class MainActivity extends AppCompatActivity {

//    private Button u_button;
//    Uri Image;



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

        // setting listener
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("hello there");
//                Intent i = new Intent(MainActivity.this, FilePicker.class);
//                startActivity(i);

//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(
//                        Intent.createChooser(intent, "select a file"),
//                        1
//                );
                try {
                    // Create and start the intent
                    Intent i = new Intent(MainActivity.this, com.example.theremindful2.FilePicker.class);

                    setContentView(R.layout.activity_main);

                    startActivity(i);  // Use startActivityForResult instead of startActivity
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
                }



            }
        });





        //        SwingUtilities.invokeLater(() -> {
//            ImageUploadExample example = new ImageUploadExample();
//            example.setVisible(true);
//        });




        //u_button = findViewById(R.id.upload_button);



//        u_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                final int ACTIVITY_SELECT_IMAGE = 1234;
//                intent.setType("image/*");
//                //audioPickerLauncher.launch(Intent.createChooser(intent, "pick auido"));
////                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
//            }
//        });

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
//            String imageUriString = data.getStringExtra("imageUri");
//            if (imageUriString != null) {
//                Uri imageUri = Uri.parse(imageUriString);
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                    ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);
////                    parentViewPager.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

//    public void startActivityForResult (Intent i, int intt) {
//        System.out.println("hello world");
//    }
// ActivityResultLauncher to start a launcer that gets a result (audio file) from it
// register for activity result will be a launcher that starts activity and deals with audio file
// new activitiy result contracts... starts activity fetches result
//private ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
//        new ActivityResultContracts.StartActivityForResult(), audioReturned-> {
//
//            if (audioReturned.getResultCode() == RESULT_OK){
//                Intent datafile = audioReturned.getData();
//                if (datafile != null){
//                    Image = datafile.getData();
//
//
//                }
//
//            }
//        }
//);


}
