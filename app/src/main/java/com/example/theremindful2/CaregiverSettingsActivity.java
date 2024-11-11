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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        List<Uri> imageUriList = new ArrayList<>();

        FlexboxLayout albums = findViewById(R.id.flexBoxAlbum);

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) { // Or any extension you're using
                    Uri imageUri = Uri.fromFile(file);
                    imageUriList.add(imageUri);

                }
                if (file.isFile() && file.getName().endsWith(".json"))   {
                    ImageView image = new ImageView(this);
                    image.setImageResource(R.drawable.ic_launcher_foreground);
                    // Set layout parameters to control the size
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                            300,  // Width of the image, adjust as needed
                            300   // Height of the image, adjust as needed
                    );
                    layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
                    image.setLayoutParams(layoutParams);
                    image.setTag(file.getName());
                    // Add the ImageView to the FlexboxLayout
                    albums.addView(image);

                }
            }
        }
        FlexboxLayout flexboxImages = findViewById(R.id.flexBoxImages);

        for (Uri imageUri : imageUriList) {
            addImageToFlexBoxLayout(imageUri, flexboxImages);
        }
        // New Album
        Button newAlbum = (Button)findViewById(R.id.newAlbumsButton);
        newAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CaregiverSettingsActivity.this,"Album Button Pressed",Toast.LENGTH_SHORT).show();
                JSONObject albumInfo = new JSONObject();
                String albumPath = UUID.randomUUID().toString() + "_info.json";
                try{
                    albumInfo.put("album_name","New Album");
                    JSONArray imagesArray = new JSONArray();
                    albumInfo.put("images",imagesArray);

                    // Save the JSON metadata to a file
                    File albumInfoFile = new File(directory, albumPath);
                    try (FileWriter writer = new FileWriter(albumInfoFile)) {
                        writer.write(albumInfo.toString());
                    }
                }catch(IOException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // Create an ImageView for the album thumbnail
                ImageView thumbnailView = new ImageView(CaregiverSettingsActivity.this);
                thumbnailView.setImageResource(R.drawable.ic_launcher_foreground); // Set placeholder image
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                        300,  // Width of the thumbnail
                        300   // Height of the thumbnail
                );
                layoutParams.setMargins(16, 16, 16, 16); // Optional margin
                thumbnailView.setLayoutParams(layoutParams);
                thumbnailView.setTag(albumPath);
                thumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CaregiverSettingsActivity.this, album.class);
                        Toast.makeText(CaregiverSettingsActivity.this,"album Button Pressed" + thumbnailView.getTag(),Toast.LENGTH_SHORT).show();
                        intent.putExtra("album", thumbnailView.getTag().toString());
                        startActivity(intent);
                    }
                });
                // Add the thumbnail ImageView to the FlexboxLayout
                albums.addView(thumbnailView);
            }
        });
        //Make all image preview clickable to go to photo preview
        for(int images = 0; images < flexboxImages.getChildCount(); images++){
            View view = flexboxImages.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CaregiverSettingsActivity.this, photo_view.class);
                        Object Tag = view.getTag();
                        intent.putExtra("Uri", Tag.toString());
                        startActivity(intent);

                    }
                });
            }
        }


        //Make all albums(album images) clickable to go to album preview images

        for(int images = 0; images < albums.getChildCount(); images++){
            View view = albums.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CaregiverSettingsActivity.this,"album Button Pressed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaregiverSettingsActivity.this, album.class);
                        Object album = view.getTag();
                        Toast.makeText(CaregiverSettingsActivity.this,"album Button Pressed" + album,Toast.LENGTH_SHORT).show();

                        intent.putExtra("album", album.toString());
                        startActivity(intent);
                    }
                });
            }
        }


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
    private void addImageToFlexBoxLayout(Uri imageUri, FlexboxLayout flexBox){
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);
        imageView.setTag(imageUri);
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