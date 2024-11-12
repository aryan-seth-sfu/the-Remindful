package com.example.theremindful2;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class album extends AppCompatActivity{
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);
        // Enable the action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FlexboxLayout photoTap = findViewById(R.id.AlbumPhoto);


        Button uploadButton = findViewById(R.id.AlbumAddPhoto);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<File> imageFiles = getAllImagesInInternalStorage();
                uploadButton.setVisibility(View.INVISIBLE);
                photoTap.setVisibility(View.INVISIBLE);
                FlexboxLayout allPhotos = findViewById(R.id.AllPhotosView);
                allPhotos.setVisibility(View.VISIBLE);
                allPhotos.removeAllViews();
                try {
                    for (File imageFile : imageFiles) {
                        ImageView image = new ImageView(album.this);
                        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                300,  // Width of the thumbnail
                                300   // Height of the thumbnail
                        );
                        layoutParams.setMargins(16, 16, 16, 16);
                        image.setLayoutParams(layoutParams);

                        // Get the URI for the image file
                        Uri imageUri = FileProvider.getUriForFile(
                                album.this,
                                getPackageName() + ".fileprovider",
                                imageFile
                        );

                        if (imageUri != null) {
                            image.setImageURI(imageUri);
                            allPhotos.addView(image);
                            image.setTag(imageUri);
                        } else {
                            Log.e("ImageError", "Image URI is null for file: " + imageFile.getName());
                        }
                    }
                } catch (Exception e) {
                    Log.e("ImageLoadingError", "Error loading image", e);
                    e.printStackTrace();
                }


                Log.d("Image URI", imageFiles.toString());
                clickableUpdate();
            }
        });



        //Make all photos in the album view clickable leading to image view

        for(int images = 0; images < photoTap.getChildCount();images++){
            View view = photoTap.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(album.this, photo_view.class);
                        startActivity(intent);
                    }
                });
            }
        }


    }
    public boolean onSupportNavigateUp() {
        // Handles the action when the back button is pressed
        onBackPressed();
        return true;
    }
    public List<File> getAllImagesInInternalStorage() {
        File directory = getFilesDir(); // Use getExternalFilesDir() for external storage
        List<File> imageFiles = new ArrayList<>();

        // List all files in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                // Check if the file is an image (e.g., .jpg, .png)
                if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                    imageFiles.add(file);
                }
            }
        }

        return imageFiles;
    }
    public void clickableUpdate(){
        FlexboxLayout allPhotos = findViewById(R.id.AllPhotosView);
        FlexboxLayout photoTap = findViewById(R.id.AlbumPhoto);
        Button uploadButton = findViewById(R.id.AlbumAddPhoto);
        for(int images = 0; images < allPhotos.getChildCount(); images++){
            View view = allPhotos.getChildAt(images);
            if(view instanceof ImageView){
                ImageView imageView = (ImageView) view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create a new ImageView instance (or copy the existing one)
                        ImageView clickedImage = new ImageView(album.this);
                        clickedImage.setImageDrawable(imageView.getDrawable());  // Copy the image
                        Uri clickedImageUri = ((Uri) view.getTag());
                        String imagePath = String.valueOf(clickedImageUri);
                        Log.d("Image Uri", imagePath);
                        Uri imageUri = Uri.parse(imagePath);
                        // Set the same layout parameters (or modify them if necessary)
                        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                300,  // Width of the thumbnail
                                300   // Height of the thumbnail
                        );
                        layoutParams.setMargins(16, 16, 16, 16); // Optional margin
                        clickedImage.setLayoutParams(layoutParams);

                        // Add the copied ImageView to the 'photoTap' FlexboxLayout
                        photoTap.addView(clickedImage);
                        try {
                            JSONObject newAlbumData = new JSONObject();
                            newAlbumData.put("images",String.valueOf(imageUri));
                            Intent intent = getIntent();
                            Log.d("fileName", String.valueOf(intent.getStringExtra("album")));
                            addToJsonFile(album.this, String.valueOf(intent.getStringExtra("album")),newAlbumData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        clickedImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(album.this, photo_view.class);
                                intent.putExtra("Uri",String.valueOf(imageUri));
                                startActivity(intent);
                            }
                        });


                        allPhotos.setVisibility(View.INVISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                        photoTap.setVisibility(View.VISIBLE);

                    }
                });
            }
        }
    }
    public void addToJsonFile(Context context, String fileName, JSONObject newData) {
        File directory = context.getFilesDir(); // Internal storage directory
        File jsonFile = new File(directory, fileName);

        // Initialize JSONObject and JSONArray
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            // Read existing data if the file already exists
            if (jsonFile.exists()) {
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();

                // Parse the existing file content to a JSONObject
                jsonObject = new JSONObject(content.toString());

                // Get the JSON array if it exists, otherwise create a new one
                jsonArray = jsonObject.optJSONArray("images");
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
            } else {
                // If the file doesn't exist, create a new JSONObject and JSONArray
                jsonObject = new JSONObject();
                jsonArray = new JSONArray();
            }

            // Add new data to the JSON array
            jsonArray.put(newData);
            jsonObject.put("images", jsonArray);

            // Write the updated JSON object back to the file
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(jsonObject.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
