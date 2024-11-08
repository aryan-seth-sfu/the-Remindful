package com.example.theremindful2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.theremindful2.databinding.ActivityMainBinding;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CaregiverSettingsActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> FilePickerLauncher;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_settings);

        // Enable the action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //saved images
        File directory = getFilesDir(); // Internal storage directory
        File[] files = directory.listFiles(); // List all files

        List<Bitmap> imageBitmaps = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) { // Or any extension you're using
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageBitmaps.add(bitmap);
                }
            }
        }
        FlexboxLayout flexboxImages = findViewById(R.id.flexBoxImages);

        for (Bitmap bitmap : imageBitmaps) {
            addImageToFlexBoxLayout(bitmap, flexboxImages);
        }




        // New Album
        //TODO: create a new album
        Button newAlbum = (Button)findViewById(R.id.newAlbumsButton);
        newAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CaregiverSettingsActivity.this,"Album Button Pressed",Toast.LENGTH_SHORT).show();
            }
        });
        //Make all image preview clickable to go to photo preview
        for(int images = 0; images < flexboxImages.getChildCount(); images++){
            View view = flexboxImages.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CaregiverSettingsActivity.this,"image Button Pressed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaregiverSettingsActivity.this, photo_view.class);
                        startActivity(intent);

                    }
                });
            }
        }


        //Make all albums(album images) clickable to go to album preview images
        /*
        LinearLayout albumView = findViewById(R.id.layoutAlbums);
        for(int images = 0; images < albumView.getChildCount(); images++){
            View view = albumView.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CaregiverSettingsActivity.this,"album Button Pressed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaregiverSettingsActivity.this, album.class);
                        startActivity(intent);
                    }
                });
            }
        }
        */

        FilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        System.out.println(data.getStringExtra("filePATH"));
                    }
                });
        // Upload button -> with working image pick from device
        Button uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(CaregiverSettingsActivity.this, FilePicker.class);
                    FilePickerLauncher.launch(i);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                    Toast.makeText(CaregiverSettingsActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handles the action when the back button is pressed
        onBackPressed();
        return true;
    }
    private void addImageToFlexBoxLayout(Bitmap bitmap, FlexboxLayout flexBox){
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        // Set layout parameters to control the size
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                300,  // Width of the image, adjust as needed
                300   // Height of the image, adjust as needed
        );
        layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
        imageView.setLayoutParams(layoutParams);

        // Add the ImageView to the FlexboxLayout
        flexBox.addView(imageView);


    }

}