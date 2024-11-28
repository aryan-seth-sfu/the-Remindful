package com.example.theremindful2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CareGiverImagesSettingsActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> FilePickerLauncher;
    private static final String METADATA_FILE_NAME = "themes_metadata.json";
    private static final String IMAGES_METADATA_FILE_NAME = "image_only_metadata.json";
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_images);

        TextView Home = findViewById(R.id.Home);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CareGiverImagesSettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //saved images
        File directory = getFilesDir(); // Internal storage directory
        File[] files = directory.listFiles(); // List all files

        List<Uri> imageUriList = new ArrayList<>();
        List<Pair<String, String>> albumsList = new ArrayList<>();
        File metadataFile = new File(getFilesDir(), METADATA_FILE_NAME);
        if(!metadataFile.exists()){
            try {
                metadataFile.createNewFile();
                try {
                    // Define theme tags
                    String[] tags = {getString(R.string.DefaultTag1), getString(R.string.DefaultTag2), getString(R.string.DefaultTag3), getString(R.string.DefaultTag4)};

                    // Create the root JSON object
                    JSONObject rootObject = new JSONObject();
                    JSONArray themesArray = new JSONArray();

                    // Add themes with empty image arrays
                    for (String tag : tags) {
                        JSONObject themeObject = new JSONObject();
                        themeObject.put(getString(R.string.tagText), tag);
                        themeObject.put(getString(R.string.images), new JSONArray()); // Empty images array
                        themesArray.put(themeObject);
                    }

                    // Add the themes array to the root object
                    rootObject.put(getString(R.string.themeText), themesArray);

                    // Write the JSON to the specified file
                    try (FileWriter writer = new FileWriter(metadataFile)) {
                        writer.write(rootObject.toString(4)); // Indented with 4 spaces
                        writer.flush();
                    }
                } catch (IOException | org.json.JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            BufferedReader reader = null;
            try{
                FileInputStream inputStream = new FileInputStream(metadataFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                // Parse the JSON
                JSONObject rootObject = new JSONObject(jsonBuilder.toString());
                JSONArray themesArray = rootObject.getJSONArray(getString(R.string.themeText));

                for (int i = 0; i < themesArray.length(); i++) {
                    JSONObject themeObject = themesArray.getJSONObject(i);

                    // Extract the tag (theme name)
                    String tag = themeObject.getString(getString(R.string.tagText));

                    // Extract the first image path, if available
                    JSONArray imagesArray = themeObject.getJSONArray(getString(R.string.images));
                    String firstImagePath = imagesArray.length() > 0 ? imagesArray.getString(0) : null;

                    // Add theme name and first image path to the list
                    albumsList.add(new Pair<>(tag, firstImagePath));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e("getThemeNames", getString(R.string.CloseReaderError), e);
                }
            }
        }

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(getString(R.string.imageFileType))) { // Or any extension you're using
                    Uri imageUri = Uri.fromFile(file);
                    imageUriList.add(imageUri);

                }
            }
        }
        FlexboxLayout imageAlbumLayout = findViewById(R.id.ImageAlbumLayout);
        imageAlbumLayout.removeAllViews();
        if(!albumsList.isEmpty()) {
            for (Pair<String, String> theme : albumsList) {

                // Create a horizontal LinearLayout for each image-text pair
                LinearLayout itemLayout = new LinearLayout(CareGiverImagesSettingsActivity.this);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Set layout parameters for the LinearLayout
                FlexboxLayout.LayoutParams itemLayoutParams = new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.MATCH_PARENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                );
                itemLayout.setLayoutParams(itemLayoutParams);

                ImageView image = new ImageView(CareGiverImagesSettingsActivity.this);
                if(theme.second == null){
                    image.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground,null));
                }
                else{
                    File imageFile = new File(theme.second);
                    Uri imageUri = Uri.fromFile(imageFile);
                    image.setImageURI(imageUri);
                }
                image.setTag(theme.first);
                // Set layout parameters to control the size
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                        300,  // Width of the image, adjust as needed
                        300   // Height of the image, adjust as needed
                );
                layoutParams.setMargins(16, 16, 16, 16); // Optional margin around each image
                image.setLayoutParams(layoutParams);
                // Add the ImageView to the FlexboxLayout


                TextView textView = new TextView(CareGiverImagesSettingsActivity.this);
                textView.setTextSize(16); // Optional: Set text size
                textView.setTextColor(Color.BLACK); // Optional: Set text color
                textView.setText(theme.first);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // Set layout parameters for the TextView
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                textParams.setMargins(150, 100, 8, 8); // Optional margin around the text
                textView.setLayoutParams(textParams);

                // Center the text vertically
                textView.setGravity(Gravity.CENTER_VERTICAL);

                itemLayout.addView(image);
                itemLayout.addView(textView);




                imageAlbumLayout.addView(itemLayout);
            }
            for (int images = 0; images < imageAlbumLayout.getChildCount(); images++) {
                View view = imageAlbumLayout.getChildAt(images);
                if (view instanceof LinearLayout) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout LinearLayOut = (LinearLayout) view;
                            if (LinearLayOut.getChildAt(0) instanceof ImageView){
                                Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
                                Object album = LinearLayOut.getChildAt(0).getTag();
                                Log.d("album extra", album.toString());
                                intent.putExtra("album", album.toString());
                                startActivity(intent);
                            }

                        }
                    });
                }
            }
        }

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
                        intent.putExtra("Uri", (Uri)Tag);
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
//                if (isChecked) {
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
//                                    intent.putExtra(getString(R.string.UriText), Tag.toString());
//                                    startActivity(intent);
//
//                                }
//                            });
//                        }
//                    }
//                } else {
//                    toggleButton.setTextColor(getResources().getColor(R.color.black)); // Change text color
//                    toggleButton.getBackground().setTint(getResources().getColor(R.color.light_gray)); // Background tint
//                    imageAlbumLayout.removeAllViews();
//                    if(!albumsList.isEmpty()) {
//                        for (Pair<String, String> theme : albumsList) {
//
//                            // Create a horizontal LinearLayout for each image-text pair
//                            LinearLayout itemLayout = new LinearLayout(CareGiverImagesSettingsActivity.this);
//                            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//                            // Set layout parameters for the LinearLayout
//                            FlexboxLayout.LayoutParams itemLayoutParams = new FlexboxLayout.LayoutParams(
//                                    FlexboxLayout.LayoutParams.MATCH_PARENT,
//                                    FlexboxLayout.LayoutParams.WRAP_CONTENT
//                            );
//                            itemLayout.setLayoutParams(itemLayoutParams);
//
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
//
//
//                            TextView textView = new TextView(CareGiverImagesSettingsActivity.this);
//                            textView.setTextSize(16); // Optional: Set text size
//                            textView.setTextColor(Color.BLACK); // Optional: Set text color
//                            textView.setText(theme.first);
//                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//
//                            // Set layout parameters for the TextView
//                            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                            );
//                            textParams.setMargins(150, 100, 8, 8); // Optional margin around the text
//                            textView.setLayoutParams(textParams);
//
//                            // Center the text vertically
//                            textView.setGravity(Gravity.CENTER_VERTICAL);
//
//                            itemLayout.addView(image);
//                            itemLayout.addView(textView);
//
//
//
//
//                            imageAlbumLayout.addView(itemLayout);
//                        }
//                        for (int images = 0; images < imageAlbumLayout.getChildCount(); images++) {
//                            View view = imageAlbumLayout.getChildAt(images);
//                            if (view instanceof LinearLayout) {
//                                view.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        LinearLayout LinearLayOut = (LinearLayout) view;
//                                        if (LinearLayOut.getChildAt(0) instanceof ImageView){
//                                            Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
//                                            Object album = LinearLayOut.getChildAt(0).getTag();
//                                            intent.putExtra(getString(R.string.album), album.toString());
//                                            startActivity(intent);
//                                        }
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
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
                                String imageFilename = UUID.randomUUID().toString() + getString(R.string.imageFileType);
                                Uri savedImageUri = StorageUtils.saveImageFile(getApplicationContext(), imageFilename, imageBytes);
                                if (savedImageUri != null) {
                                    addImageToFlexBoxLayout(savedImageUri, imageAlbumLayout);
                                    Toast.makeText(CareGiverImagesSettingsActivity.this, getString(R.string.ImageSaveSuccess), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CareGiverImagesSettingsActivity.this, getString(R.string.ImageSaveFail), Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(CareGiverImagesSettingsActivity.this, getString(R.string.ProcessImageError), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CareGiverImagesSettingsActivity.this, getString(R.string.OpeningFilePickerError), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //create new album
                    JSONObject albumInfo = new JSONObject();
                    String albumName = getString(R.string.DefaultNewAlbum);

                    addNewTheme(CareGiverImagesSettingsActivity.this, albumName);

                    // Create an ImageView for the album thumbnail
                    ImageView thumbnailView = new ImageView(CareGiverImagesSettingsActivity.this);
                    thumbnailView.setImageResource(R.drawable.ic_launcher_foreground); // Set placeholder image
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                            300,  // Width of the thumbnail
                            300   // Height of the thumbnail
                    );
                    layoutParams.setMargins(16, 16, 16, 16); // Optional margin
                    thumbnailView.setLayoutParams(layoutParams);
                    thumbnailView.setTag(albumName);
                    thumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CareGiverImagesSettingsActivity.this, album.class);
                            intent.putExtra(getString(R.string.album), thumbnailView.getTag().toString());
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

    public void addNewTheme(Context context, String newTheme) {
        File jsonFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
        BufferedReader reader = null;

        try {
            // Check if the file exists
            if (!jsonFile.exists()) {
                Log.e("addNewTheme", getString(R.string.MetaDataFileDoesNotExists));
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
            JSONArray themesArray = rootObject.getJSONArray(getString(R.string.themeText));

            // Check if the theme already exists
            for (int i = 0; i < themesArray.length(); i++) {
                JSONObject themeObject = themesArray.getJSONObject(i);
                if (themeObject.getString(getString(R.string.tagText)).equalsIgnoreCase(newTheme)) {
                    Log.e(getString(R.string.addNewThemeLog), getString(R.string.ThemeExistError) + newTheme);
                    return; // Exit if the theme exists
                }
            }

            // Add the new theme
            JSONObject newThemeObject = new JSONObject();
            newThemeObject.put(getString(R.string.tagText), newTheme);
            newThemeObject.put(getString(R.string.images), new JSONArray()); // Empty images array
            themesArray.put(newThemeObject);

            // Write the updated JSON back to the file
            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(rootObject.toString(4)); // Pretty print with 4-space indentation
                writer.flush();
            }

            Log.d(getString(R.string.addNewThemeLog), getString(R.string.AddNewThemeSuccess) + newTheme);
        } catch (JSONException | IOException e) {
            Log.e(getString(R.string.addNewThemeLog), getString(R.string.AddNewThemeError), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(getString(R.string.addNewThemeLog), getString(R.string.CloseReaderError), e);
            }
        }
        _addNewTheme(context, newTheme);
        Log.d(getString(R.string.addNewThemeLog), getString(R.string.AddNewThemeSuccess) + newTheme);
    }


    // aryan database functions
    private static void _addNewTheme(Context ctx, String newTheme) {
        MediaManager mediaManager = new MediaManager(ctx);
        mediaManager.addTheme(newTheme);
    }
}