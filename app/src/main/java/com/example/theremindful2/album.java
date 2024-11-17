package com.example.theremindful2;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.Objects;

public class album extends AppCompatActivity{
    private static final String METADATA_FILE_NAME = "themes_metadata.json";
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        FlexboxLayout photoTap = findViewById(R.id.AlbumPhoto);

        TextView albumName = findViewById(R.id.AlbumName);
        FlexboxLayout allPhotos = findViewById(R.id.AllPhotosView);

        Intent intentSelf = getIntent();
        String albumNameString = intentSelf.getStringExtra("album");
        albumName.setText(albumNameString);

        List<String> albumImages = getImagePathsForTheme(this, albumNameString);
        for (String imagePaths : albumImages){
            ImageView image = new ImageView(album.this);
            File imageFile = new File(imagePaths);
            Uri imageUri = Uri.fromFile(imageFile);
            image.setImageURI(imageUri);
            // Set layout parameters to control the size
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                    300,  // Width of the image, adjust as needed
                    300   // Height of the image, adjust as needed
            );
            layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
            image.setLayoutParams(layoutParams);
            // Add the ImageView to the FlexboxLayout
            allPhotos.addView(image);
        }


        ImageButton uploadButton = findViewById(R.id.AlbumAddPhoto);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                albumName.setVisibility(View.INVISIBLE);

                List<File> imageFiles = getAllImagesInInternalStorage();
                uploadButton.setVisibility(View.INVISIBLE);
                photoTap.setVisibility(View.INVISIBLE);
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
                            Log.d("imageUriTest", String.valueOf(imageUri));
                            addImagePathToTheme(album.this, albumNameString, imageFile.getPath());
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
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button editAlbumName = findViewById(R.id.editAlbumName);
        Button deleteAlbum = findViewById(R.id.deleteAlbum);
        Button cancelEdit = findViewById(R.id.cancelEditAlbum);
        ImageButton editButton = findViewById(R.id.editButton);

        editAlbumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumName.setVisibility(View.VISIBLE);
                editAlbumName.setVisibility(View.INVISIBLE);
                deleteAlbum.setVisibility(View.INVISIBLE);
                allPhotos.setVisibility(View.VISIBLE);
                cancelEdit.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
            }
        });
        deleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumName.setVisibility(View.VISIBLE);
                editAlbumName.setVisibility(View.INVISIBLE);
                deleteAlbum.setVisibility(View.INVISIBLE);
                allPhotos.setVisibility(View.VISIBLE);
                cancelEdit.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);

                //TODO delete album

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allPhotos.setVisibility(View.INVISIBLE);
                albumName.setVisibility(View.INVISIBLE);
                editAlbumName.setVisibility(View.VISIBLE);
                deleteAlbum.setVisibility(View.VISIBLE);
                cancelEdit.setVisibility(View.VISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
            }
        });
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumName.setVisibility(View.VISIBLE);
                editAlbumName.setVisibility(View.INVISIBLE);
                deleteAlbum.setVisibility(View.INVISIBLE);
                allPhotos.setVisibility(View.VISIBLE);
                cancelEdit.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
            }
        });


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
        ImageButton uploadButton = findViewById(R.id.AlbumAddPhoto);
        TextView albumName = findViewById(R.id.AlbumName);
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
                        albumName.setVisibility(View.VISIBLE);
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
    public List<String> getImagePathsForTheme(Context context, String themeName) {
        List<String> imagePaths = new ArrayList<>();
        BufferedReader reader = null;

        try {
            // Locate the metadata file
            File metadataFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
            if (metadataFile.exists()) {
                // Read the file content
                FileInputStream inputStream = new FileInputStream(metadataFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                // Parse the JSON object
                JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
                JSONArray themesArray = jsonObject.getJSONArray("themes");

                // Loop through the themes to find the matching one
                for (int i = 0; i < themesArray.length(); i++) {
                    JSONObject themeObject = themesArray.getJSONObject(i);
                    String tag = themeObject.getString("tag");

                    if (tag.equalsIgnoreCase(themeName)) {
                        // Found the matching theme, extract its image paths
                        JSONArray imagesArray = themeObject.getJSONArray("images");
                        for (int j = 0; j < imagesArray.length(); j++) {
                            imagePaths.add(imagesArray.getString(j)); // Add each path to the list
                        }
                        break; // Exit loop since the theme was found
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("getImagePathsForTheme", "Metadata file not found", e);
        } catch (IOException | JSONException e) {
            Log.e("getImagePathsForTheme", "Error reading metadata file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e("getImagePathsForTheme", "Error closing reader", e);
            }
        }

        return imagePaths;
    }
    public static void addImagePathToTheme(Context context, String tag, String imagePath) {
        File jsonFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
        BufferedReader reader = null;

        try {
            // Check if the file exists
            if (!jsonFile.exists()) {
                Log.e("addImagePathToTheme", "Themes metadata file does not exist.");
                return;
            }

            // Read the existing JSON file
            FileInputStream inputStream = new FileInputStream(jsonFile);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse the JSON
            JSONObject rootObject = new JSONObject(jsonBuilder.toString());
            JSONArray themesArray = rootObject.getJSONArray("themes");

            // Search for the theme by tag
            boolean tagFound = false;
            for (int i = 0; i < themesArray.length(); i++) {
                JSONObject themeObject = themesArray.getJSONObject(i);
                String currentTag = themeObject.getString("tag");

                if (currentTag.equalsIgnoreCase(tag)) {
                    tagFound = true;

                    // Get the images array and add the new image path
                    JSONArray imagesArray = themeObject.getJSONArray("images");
                    imagesArray.put(imagePath);

                    // Update the theme in the array
                    themesArray.put(i, themeObject);
                    break;
                }
            }

            // If the tag was found, update the JSON file, else log an error
            if (tagFound) {
                // Write the updated JSON back to the file
                try (FileWriter writer = new FileWriter(jsonFile)) {
                    writer.write(rootObject.toString(4)); // Pretty print with 4-space indentation
                    writer.flush();
                }
                Log.d("addImagePathToTheme", "Image path added successfully to tag: " + tag);
            } else {
                Log.e("addImagePathToTheme", "Tag not found: " + tag);
            }
        } catch (JSONException | IOException e) {
            Log.e("addImagePathToTheme", "Error adding image path to theme", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e("addImagePathToTheme", "Error closing reader", e);
            }
        }
    }



}
