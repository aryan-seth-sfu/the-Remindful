package com.example.theremindful2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CareGiverImagesSettingsActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> FilePickerLauncher;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_images);

        //saved images
        File directory = getFilesDir(); // Internal storage directory
        File[] files = directory.listFiles(); // List all files

        List<Uri> imageUriList = new ArrayList<>();
        List<String> albumsList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) { // Or any extension you're using
                    Uri imageUri = Uri.fromFile(file);
                    imageUriList.add(imageUri);

                }
                else if (file.isFile() && file.getName().endsWith(".json")) {
                    albumsList.add(file.getName());
                }
                else{
                    Log.e("CaregiverSettingsActivity", "Failed to load album metadata for file: " + file.getName());
                }
            }
        }
        FlexboxLayout imageAlbumLayout = findViewById(R.id.ImageAlbumLayout);

        for (Uri imageUri : imageUriList) {
            addImageToFlexBoxLayout(imageUri, imageAlbumLayout);
        }

        for(int images = 0; images < imageAlbumLayout.getChildCount(); images++){
            View view = imageAlbumLayout.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CareGiverImagesSettingsActivity.this, photo_view.class);
                        Object Tag = view.getTag();
                        intent.putExtra("Uri", Tag.toString());
                        startActivity(intent);

                    }
                });
            }
        }

        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setChecked(true);
        toggleButton.setTextColor(getResources().getColor(R.color.white));
        toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray));

// Use setOnCheckedChangeListener to listen for state changes
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("toggle", "Checked: " + isChecked);
                    toggleButton.setTextColor(getResources().getColor(R.color.white));
                    toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray)); // Background tint
                    imageAlbumLayout.removeAllViews();
                    for (Uri imageUri : imageUriList) {
                        addImageToFlexBoxLayout(imageUri, imageAlbumLayout);
                    }
                    for(int images = 0; images < imageAlbumLayout.getChildCount(); images++){
                        View view = imageAlbumLayout.getChildAt(images);
                        if(view instanceof ImageView){
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(CareGiverImagesSettingsActivity.this, photo_view.class);
                                    Object Tag = view.getTag();
                                    intent.putExtra("Uri", Tag.toString());
                                    startActivity(intent);

                                }
                            });
                        }
                    }
                } else {
                    Log.d("toggle", "Checked: " + isChecked);
                    toggleButton.setTextColor(getResources().getColor(R.color.black)); // Change text color
                    toggleButton.getBackground().setTint(getResources().getColor(R.color.light_gray)); // Background tint
                    imageAlbumLayout.removeAllViews();
                    for( int fileNamesCount = 0; fileNamesCount < albumsList.size(); fileNamesCount++) {
                        ImageView image = new ImageView(CareGiverImagesSettingsActivity.this);
                        image.setImageResource(R.drawable.ic_launcher_foreground);
                        // Set layout parameters to control the size
                        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                300,  // Width of the image, adjust as needed
                                300   // Height of the image, adjust as needed
                        );
                        layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
                        image.setLayoutParams(layoutParams);
                        image.setTag(albumsList.get(fileNamesCount));
                        // Add the ImageView to the FlexboxLayout
                        imageAlbumLayout.addView(image);
                    }
                    for(int images = 0; images < imageAlbumLayout.getChildCount(); images++){
                        View view = imageAlbumLayout.getChildAt(images);
                        if(view instanceof ImageView){
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
                                    Object album = view.getTag();
                                    intent.putExtra("album", album.toString());
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                }
            }
        });

        FilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                String imageFilename = UUID.randomUUID().toString() + ".jpg";
                                Uri savedImageUri = StorageUtils.saveImageFile(getApplicationContext(), imageFilename, imageBytes);
                                if (savedImageUri != null) {
                                    addImageToFlexBoxLayout(savedImageUri, imageAlbumLayout);
                                    Toast.makeText(CareGiverImagesSettingsActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CareGiverImagesSettingsActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Log.e("CaregiverSettingsActivity", "Error processing image: " + e.getMessage());
                                Toast.makeText(CareGiverImagesSettingsActivity.this, "Error processing image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        ImageButton addImage = findViewById(R.id.imageButton);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                    try {
                        Intent i = new Intent(CareGiverImagesSettingsActivity.this, FilePicker.class);
                        FilePickerLauncher.launch(i);
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
                        Toast.makeText(CareGiverImagesSettingsActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //create new album
                    JSONObject albumInfo = new JSONObject();
                    String albumPath = UUID.randomUUID().toString() + "_info.json";
                    try {
                        albumInfo.put("album_name", "New Album");
                        JSONArray imagesArray = new JSONArray();
                        albumInfo.put("images", imagesArray);

                        boolean success = StorageUtils.saveToFile(getApplicationContext(), albumPath, albumInfo.toString());
                        if (!success) {
                            Toast.makeText(CareGiverImagesSettingsActivity.this, "Failed to save album metadata", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        Log.e("CareGiverImagesSettingsActivity", "Error creating album metadata: " + e.getMessage());
                        Toast.makeText(CareGiverImagesSettingsActivity.this, "Error creating album metadata", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create an ImageView for the album thumbnail
                    ImageView thumbnailView = new ImageView(CareGiverImagesSettingsActivity.this);
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
                            Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
                            Toast.makeText(CareGiverImagesSettingsActivity.this, "album Button Pressed" + thumbnailView.getTag(), Toast.LENGTH_SHORT).show();
                            intent.putExtra("album", thumbnailView.getTag().toString());
                            startActivity(intent);
                        }
                    });
                    // Add the thumbnail ImageView to the FlexboxLayout
                    imageAlbumLayout.addView(thumbnailView);
                }
            }
        });
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    private void addImageToFlexBoxLayout(Uri imageUri, FlexboxLayout flexBox) {
        // Set the FlexboxLayout to wrap lines when there's not enough space
        flexBox.setFlexWrap(FlexWrap.WRAP);
        flexBox.setJustifyContent(JustifyContent.FLEX_START); // Align items at the start of each line

        // Create an ImageView for the image
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
