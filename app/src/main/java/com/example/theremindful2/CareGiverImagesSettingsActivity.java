package com.example.theremindful2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theremindful2.data.Image;
import com.example.theremindful2.data.ImageRepository;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

//package com.example.theremindful2;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.Pair;
//import android.view.View;
//import android.widget.CompoundButton;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.flexbox.FlexWrap;
//import com.google.android.flexbox.FlexboxLayout;
//import com.google.android.flexbox.JustifyContent;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class CareGiverImagesSettingsActivity extends AppCompatActivity {
//    private ActivityResultLauncher<Intent> FilePickerLauncher;
//    private static final String METADATA_FILE_NAME = "themes_metadata.json";
//    private static final String IMAGES_METADATA_FILE_NAME = "image_only_metadata.json";
//    @SuppressLint("ResourceAsColor")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_caregiver_images);
//
//        //saved images
//        File directory = getFilesDir(); // Internal storage directory
//        File[] files = directory.listFiles(); // List all files
//
//        List<Uri> imageUriList = new ArrayList<>();
//        List<Pair<String, String>> albumsList = new ArrayList<>();
//        File metadataFile = new File(getFilesDir(), METADATA_FILE_NAME);
//        if(!metadataFile.exists()){
//            try {
//                metadataFile.createNewFile();
//                try {
//                    // Define theme tags
//                    String[] tags = {"Nature", "Vacation", "Family", "Friends"};
//
//                    // Create the root JSON object
//                    JSONObject rootObject = new JSONObject();
//                    JSONArray themesArray = new JSONArray();
//
//                    // Add themes with empty image arrays
//                    for (String tag : tags) {
//                        JSONObject themeObject = new JSONObject();
//                        themeObject.put("tag", tag);
//                        themeObject.put("images", new JSONArray()); // Empty images array
//                        themesArray.put(themeObject);
//                    }
//
//                    // Add the themes array to the root object
//                    rootObject.put("themes", themesArray);
//
//                    // Write the JSON to the specified file
//                    try (FileWriter writer = new FileWriter(metadataFile)) {
//                        writer.write(rootObject.toString(4)); // Indented with 4 spaces
//                        writer.flush();
//                    }
//
//                    System.out.println("JSON file created successfully: " + metadataFile.getAbsolutePath());
//                } catch (IOException | org.json.JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        else{
//            BufferedReader reader = null;
//            try{
//                FileInputStream inputStream = new FileInputStream(metadataFile);
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//                StringBuilder jsonBuilder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    jsonBuilder.append(line);
//                }
//
//                // Parse the JSON
//                JSONObject rootObject = new JSONObject(jsonBuilder.toString());
//                JSONArray themesArray = rootObject.getJSONArray("themes");
//
//                for (int i = 0; i < themesArray.length(); i++) {
//                    JSONObject themeObject = themesArray.getJSONObject(i);
//
//                    // Extract the tag (theme name)
//                    String tag = themeObject.getString("tag");
//
//                    // Extract the first image path, if available
//                    JSONArray imagesArray = themeObject.getJSONArray("images");
//                    String firstImagePath = imagesArray.length() > 0 ? imagesArray.getString(0) : null;
//
//                    // Add theme name and first image path to the list
//                    albumsList.add(new Pair<>(tag, firstImagePath));
//                }
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } finally {
//                try {
//                    if (reader != null) {
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    Log.e("getThemeNames", "Error closing reader", e);
//                }
//            }
//        }
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile() && file.getName().endsWith(".jpg")) { // Or any extension you're using
//                    Uri imageUri = Uri.fromFile(file);
//                    imageUriList.add(imageUri);
//
//                }
//            }
//        }
//        FlexboxLayout imageAlbumLayout = findViewById(R.id.ImageAlbumLayout);
//
//        for (Uri imageUri : imageUriList) {
//            addImageToFlexBoxLayout(imageUri, imageAlbumLayout);
//        }
//
//        for(int images = 0; images < imageAlbumLayout.getChildCount(); images++){
//            View view = imageAlbumLayout.getChildAt(images);
//            if(view instanceof ImageView){
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(CareGiverImagesSettingsActivity.this, photo_view.class);
//                        Object Tag = view.getTag();
//                        intent.putExtra("Uri", Tag.toString());
//                        startActivity(intent);
//
//                    }
//                });
//            }
//        }
//
//        ToggleButton toggleButton = findViewById(R.id.toggleButton);
//        toggleButton.setChecked(true);
//        toggleButton.setTextColor(getResources().getColor(R.color.white));
//        toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray));
//
//// Use setOnCheckedChangeListener to listen for state changes
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    Log.d("toggle", "Checked: " + isChecked);
//                    toggleButton.setTextColor(getResources().getColor(R.color.white));
//                    toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray)); // Background tint
//                    imageAlbumLayout.removeAllViews();
//                    for (Uri imageUri : imageUriList) {
//                        addImageToFlexBoxLayout(imageUri, imageAlbumLayout);
//                    }
//                    for(int images = 0; images < imageAlbumLayout.getChildCount(); images++){
//                        View view = imageAlbumLayout.getChildAt(images);
//                        if(view instanceof ImageView){
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent = new Intent(CareGiverImagesSettingsActivity.this, photo_view.class);
//                                    Object Tag = view.getTag();
//                                    intent.putExtra("Uri", Tag.toString());
//                                    startActivity(intent);
//
//                                }
//                            });
//                        }
//                    }
//                } else {
//                    //TODO: change layout of album preview
//                    Log.d("toggle", "Checked: " + isChecked);
//                    toggleButton.setTextColor(getResources().getColor(R.color.black)); // Change text color
//                    toggleButton.getBackground().setTint(getResources().getColor(R.color.light_gray)); // Background tint
//                    imageAlbumLayout.removeAllViews();
//                    if(!albumsList.isEmpty()) {
//                        for (Pair<String, String> theme : albumsList) {
//                            ImageView image = new ImageView(CareGiverImagesSettingsActivity.this);
//                            if(theme.second == null){
//                                image.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground,null));
//                            }
//                            else{
//                                File imageFile = new File(theme.second);
//                                Uri imageUri = Uri.fromFile(imageFile);
//                                image.setImageURI(imageUri);
//                            }
//                            image.setTag(theme.first);
//                            // Set layout parameters to control the size
//                            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
//                                    300,  // Width of the image, adjust as needed
//                                    300   // Height of the image, adjust as needed
//                            );
//                            layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
//                            image.setLayoutParams(layoutParams);
//                            // Add the ImageView to the FlexboxLayout
//                            imageAlbumLayout.addView(image);
//                        }
//                        for (int images = 0; images < imageAlbumLayout.getChildCount(); images++) {
//                            View view = imageAlbumLayout.getChildAt(images);
//                            if (view instanceof ImageView) {
//                                view.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
//                                        Object album = view.getTag();
//                                        Log.d("album extra", album.toString());
//                                        intent.putExtra("album", album.toString());
//                                        startActivity(intent);
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//        FilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    Intent data = result.getData();
//                    if (data != null) {
//                        Uri selectedImageUri = data.getData();
//                        if (selectedImageUri != null) {
//                            try {
//                                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
//                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                                byte[] imageBytes = byteArrayOutputStream.toByteArray();
//                                String imageFilename = UUID.randomUUID().toString() + ".jpg";
//                                Uri savedImageUri = StorageUtils.saveImageFile(getApplicationContext(), imageFilename, imageBytes);
//                                //TODO: add saved image json file for quick lookup of saved tags and descriptions -> make necessary changes
//
//                                if (savedImageUri != null) {
//                                    addImageToFlexBoxLayout(savedImageUri, imageAlbumLayout);
//                                    Toast.makeText(CareGiverImagesSettingsActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(CareGiverImagesSettingsActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (IOException e) {
//                                Log.e("CaregiverSettingsActivity", "Error processing image: " + e.getMessage());
//                                Toast.makeText(CareGiverImagesSettingsActivity.this, "Error processing image", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//        ImageButton addImage = findViewById(R.id.imageButton);
//        addImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (toggleButton.isChecked()) {
//                    try {
//                        Intent i = new Intent(CareGiverImagesSettingsActivity.this, FilePicker.class);
//                        FilePickerLauncher.launch(i);
//                        } catch (Exception e) {
//                        Log.e("MainActivity", "Error starting FilePickerActivity: " + e.getMessage());
//                        Toast.makeText(CareGiverImagesSettingsActivity.this, "Error opening file picker", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    //create new album
//                    JSONObject albumInfo = new JSONObject();
//                    String albumName = "new album";
//
//                    addNewTheme(CareGiverImagesSettingsActivity.this, albumName);
//
//                    // Create an ImageView for the album thumbnail
//                    ImageView thumbnailView = new ImageView(CareGiverImagesSettingsActivity.this);
//                    thumbnailView.setImageResource(R.drawable.ic_launcher_foreground); // Set placeholder image
//                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
//                            300,  // Width of the thumbnail
//                            300   // Height of the thumbnail
//                    );
//                    layoutParams.setMargins(16, 16, 16, 16); // Optional margin
//                    thumbnailView.setLayoutParams(layoutParams);
//                    thumbnailView.setTag(albumName);
//                    thumbnailView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
//                            Toast.makeText(CareGiverImagesSettingsActivity.this, "album Button Pressed" + thumbnailView.getTag(), Toast.LENGTH_SHORT).show();
//                            intent.putExtra("album", thumbnailView.getTag().toString());
//                            startActivity(intent);
//                        }
//                    });
//                    // Add the thumbnail ImageView to the FlexboxLayout
//                    imageAlbumLayout.addView(thumbnailView);
//                }
//            }
//        });
//        ImageButton backButton = findViewById(R.id.back_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//
//    }
//    private void addImageToFlexBoxLayout(Uri imageUri, FlexboxLayout flexBox) {
//        // Set the FlexboxLayout to wrap lines when there's not enough space
//        flexBox.setFlexWrap(FlexWrap.WRAP);
//        flexBox.setJustifyContent(JustifyContent.FLEX_START); // Align items at the start of each line
//
//        // Create an ImageView for the image
//        ImageView imageView = new ImageView(this);
//        imageView.setImageURI(imageUri);
//        imageView.setTag(imageUri);
//
//        // Set layout parameters to control the size
//        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
//                300,  // Width of the image, adjust as needed
//                300   // Height of the image, adjust as needed
//        );
//        layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
//        imageView.setLayoutParams(layoutParams);
//
//        // Add the ImageView to the FlexboxLayout
//        flexBox.addView(imageView);
//    }
//    public static void addImageToMetadataFile(Context context, String imagePath, String description) {
//        File jsonFile = new File(context.getFilesDir(), "images_metadata.json");
//        BufferedReader reader = null;
//
//        try {
//            JSONObject rootObject;
//            JSONArray imagesArray;
//
//            // Check if the JSON file exists
//            if (!jsonFile.exists()) {
//                // If the file does not exist, create it with an empty "images" array
//                rootObject = new JSONObject();
//                imagesArray = new JSONArray();
//                rootObject.put("images", imagesArray);
//
//                try (FileWriter writer = new FileWriter(jsonFile)) {
//                    writer.write(rootObject.toString(4)); // Create initial JSON file
//                }
//            } else {
//                // Read existing JSON file
//                FileInputStream inputStream = new FileInputStream(jsonFile);
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//                StringBuilder jsonBuilder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    jsonBuilder.append(line);
//                }
//
//                // Parse JSON
//                rootObject = new JSONObject(jsonBuilder.toString());
//                imagesArray = rootObject.getJSONArray("images");
//            }
//
//            // Add the new image object
//            JSONObject newImageObject = new JSONObject();
//            // Use the image path as it is without concatenation
//            newImageObject.put("path", imagePath);
//            newImageObject.put("description", description);
//            imagesArray.put(newImageObject);
//
//            // Write updated JSON back to the file
//            try (FileWriter writer = new FileWriter(jsonFile)) {
//                writer.write(rootObject.toString(4)); // Pretty print JSON with 4-space indentation
//                writer.flush();
//            }
//
//            Log.d("addImage", "Image added successfully to JSON metadata file.");
//        } catch (JSONException | IOException e) {
//            Log.e("addImage", "Error updating image metadata file", e);
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                Log.e("addImage", "Error closing reader", e);
//            }
//        }
//    }
//
//    public static void createMetadataFileIfNotExists(Context context) {
//        File metadataFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);
//
//        if (!metadataFile.exists()) {
//            try (FileWriter writer = new FileWriter(metadataFile)) {
//                // Initialize the file with an empty "images" array
//                JSONObject rootObject = new JSONObject();
//                rootObject.put("images", new JSONArray());
//                writer.write(rootObject.toString(4)); // Pretty-print with 4 spaces
//                writer.flush();
//                Log.d("Metadata", "Metadata file created successfully.");
//            } catch (IOException | JSONException e) {
//                Log.e("Metadata", "Error creating metadata file", e);
//            }
//        } else {
//            Log.d("Metadata", "Metadata file already exists.");
//        }
//    }
//    public static String getImageDescriptionByPath(Context context, String imagePath) {
//        BufferedReader reader = null;
//
//        try {
//            File metadataFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);
//            if (!metadataFile.exists()) {
//                Log.e("ImageMetadata", "Metadata file does not exist.");
//                return null;
//            }
//
//            FileInputStream inputStream = new FileInputStream(metadataFile);
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder jsonBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonBuilder.append(line);
//            }
//
//            // Parse the JSON
//            JSONObject rootObject = new JSONObject(jsonBuilder.toString());
//            JSONArray imagesArray = rootObject.getJSONArray("images");
//
//            // Find the image with the specified path
//            for (int i = 0; i < imagesArray.length(); i++) {
//                JSONObject imageObject = imagesArray.getJSONObject(i);
//                if (imageObject.getString("path").equals(imagePath)) {
//                    return imageObject.getString("description");
//                }
//            }
//
//        } catch (IOException | JSONException e) {
//            Log.e("ImageMetadata", "Error reading metadata file", e);
//        } finally {
//            try {
//                if (reader != null) reader.close();
//            } catch (IOException e) {
//                Log.e("ImageMetadata", "Error closing reader", e);
//            }
//        }
//
//        // Return null if the image is not found
//        return null;
//    }
//    public static void addNewTheme(Context context, String newTheme) {
//        File jsonFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
//        BufferedReader reader = null;
//
//        try {
//            // Check if the file exists
//            if (!jsonFile.exists()) {
//                Log.e("addNewTheme", "Themes metadata file does not exist.");
//                return;
//            }
//
//            // Read the existing JSON file
//            FileInputStream inputStream = new FileInputStream(jsonFile);
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder jsonBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonBuilder.append(line);
//            }
//
//            // Parse the JSON
//            JSONObject rootObject = new JSONObject(jsonBuilder.toString());
//            JSONArray themesArray = rootObject.getJSONArray("themes");
//
//            // Check if the theme already exists
//            for (int i = 0; i < themesArray.length(); i++) {
//                JSONObject themeObject = themesArray.getJSONObject(i);
//                if (themeObject.getString("tag").equalsIgnoreCase(newTheme)) {
//                    Log.e("addNewTheme", "Theme already exists: " + newTheme);
//                    return; // Exit if the theme exists
//                }
//            }
//
//            // Add the new theme
//            JSONObject newThemeObject = new JSONObject();
//            newThemeObject.put("tag", newTheme);
//            newThemeObject.put("images", new JSONArray()); // Empty images array
//            themesArray.put(newThemeObject);
//
//            // Write the updated JSON back to the file
//            try (FileWriter writer = new FileWriter(jsonFile)) {
//                writer.write(rootObject.toString(4)); // Pretty print with 4-space indentation
//                writer.flush();
//            }
//
//            Log.d("addNewTheme", "New theme added successfully: " + newTheme);
//        } catch (JSONException | IOException e) {
//            Log.e("addNewTheme", "Error adding new theme", e);
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                Log.e("addNewTheme", "Error closing reader", e);
//            }
//        }
//    }
//
//
//
//}
public class CareGiverImagesSettingsActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> FilePickerLauncher;
    private ImageRepository repository;
    private FlexboxLayout imageAlbumLayout;
    private ToggleButton toggleButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_images);

        // Initialize repository
        repository = new ImageRepository(this);
        imageAlbumLayout = findViewById(R.id.ImageAlbumLayout);
        toggleButton = findViewById(R.id.toggleButton);

        // Initial setup
        setupViews();
        loadImages();

        // Initialize FilePickerLauncher
        FilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleImagePickerResult(result)
        );
    }

    private void setupViews() {
        // Setup toggle button
        toggleButton.setChecked(true);
        toggleButton.setTextColor(getResources().getColor(R.color.white));
        toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray));

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateToggleButtonAppearance(isChecked);
            loadImages();
        });

        // Setup add image button
        ImageButton addImage = findViewById(R.id.imageButton);
        addImage.setOnClickListener(v -> handleAddImageClick());

        // Setup back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadImages() {
        if (toggleButton.isChecked()) {
            // Load all images
            repository.getAllImages(images -> {
                imageAlbumLayout.removeAllViews();
                for (Image image : images) {
                    addImageToFlexBoxLayout(Uri.parse(image.getUri()), imageAlbumLayout);
                }
                setupImageClickListeners();
            });
        } else {
            // Load themes/albums
            repository.getAllThemes(themes -> {
                imageAlbumLayout.removeAllViews();
                for (String theme : themes) {
                    repository.getImagesByTheme(theme, themeImages -> {
                        if (!themeImages.isEmpty()) {
                            addThemeThumbnail(theme, Uri.parse(themeImages.get(0).getUri()));
                        } else {
                            addEmptyThemeThumbnail(theme);
                        }
                    });
                }
            });
        }
    }

    private void addThemeThumbnail(String theme, Uri thumbnailUri) {
        ImageView thumbnailView = new ImageView(this);
        thumbnailView.setImageURI(thumbnailUri);
        thumbnailView.setTag(theme);
        setupThumbnailLayout(thumbnailView);
        thumbnailView.setOnClickListener(v -> openAlbum(theme));
        imageAlbumLayout.addView(thumbnailView);
    }

    private void addEmptyThemeThumbnail(String theme) {
        ImageView thumbnailView = new ImageView(this);
        thumbnailView.setImageResource(R.drawable.ic_launcher_foreground);
        thumbnailView.setTag(theme);
        setupThumbnailLayout(thumbnailView);
        thumbnailView.setOnClickListener(v -> openAlbum(theme));
        imageAlbumLayout.addView(thumbnailView);
    }

    private void setupThumbnailLayout(ImageView thumbnailView) {
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(300, 300);
        layoutParams.setMargins(16, 16, 16, 16);
        thumbnailView.setLayoutParams(layoutParams);
    }

    private void handleAddImageClick() {
        if (toggleButton.isChecked()) {
            launchImagePicker();
        } else {
            createNewTheme();
        }
    }

    private void launchImagePicker() {
        try {
            Intent intent = new Intent(this, FilePicker.class);
            FilePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("CareGiverSettings", "Error launching FilePicker: " + e.getMessage());
            Toast.makeText(this, "Error opening file picker", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImagePickerResult(ActivityResult result) {
        Intent data = result.getData();
        if (data != null && data.getData() != null) {
            processSelectedImage(data.getData());
        }
    }

    private void processSelectedImage(Uri selectedImageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String imageFilename = UUID.randomUUID().toString() + ".jpg";

            StorageUtils.saveImageWithMetadata(
                    getApplicationContext(),
                    imageFilename,
                    imageBytes,
                    null,
                    toggleButton.isChecked() ? null : "Default Theme",
                    uri -> {
                        if (uri != null) {
                            // Save to database
                            Image image = new Image(imageFilename, uri.toString(), null,
                                    toggleButton.isChecked() ? null : "Default Theme");

                            repository.saveImage(image, success -> {
                                if (success) {
                                    addImageToFlexBoxLayout(uri, imageAlbumLayout);
                                    Toast.makeText(this, "Image saved successfully",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to save image",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
            );
        } catch (IOException e) {
            Log.e("CareGiverSettings", "Error processing image: " + e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewTheme() {
        String newThemeName = "New Album " + System.currentTimeMillis();
        Image placeholderImage = new Image(
                "placeholder.jpg",
                "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground,
                null,
                newThemeName
        );

        repository.saveImage(placeholderImage, success -> {
            if (success) {
                addEmptyThemeThumbnail(newThemeName);
            }
        });
    }

    private void addImageToFlexBoxLayout(Uri imageUri, FlexboxLayout flexBox) {
        Log.d("ImageDebug", "Adding image with URI: " + imageUri.toString());
        flexBox.setFlexWrap(FlexWrap.WRAP);
        flexBox.setJustifyContent(JustifyContent.FLEX_START);

        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);
        imageView.setTag(imageUri);

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(300, 300);
        layoutParams.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(layoutParams);

        flexBox.addView(imageView);
    }

    private void setupImageClickListeners() {
        for (int i = 0; i < imageAlbumLayout.getChildCount(); i++) {
            View view = imageAlbumLayout.getChildAt(i);
            if (view instanceof ImageView) {
                view.setOnClickListener(v -> {
                    Intent intent = new Intent(this, photo_view.class);
                    intent.putExtra("Uri", v.getTag().toString());
                    startActivity(intent);
                });
            }
        }
    }

    private void openAlbum(String theme) {
        Intent intent = new Intent(this, album.class);
        intent.putExtra("album", theme);
        startActivity(intent);
    }

    private void updateToggleButtonAppearance(boolean isChecked) {
        if (isChecked) {
            toggleButton.setTextColor(getResources().getColor(R.color.white));
            toggleButton.getBackground().setTint(getResources().getColor(R.color.dark_gray));
        } else {
            toggleButton.setTextColor(getResources().getColor(R.color.black));
            toggleButton.getBackground().setTint(getResources().getColor(R.color.light_gray));
        }
    }
}