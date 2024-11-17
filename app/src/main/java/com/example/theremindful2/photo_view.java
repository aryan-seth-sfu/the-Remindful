package com.example.theremindful2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class photo_view extends AppCompatActivity{
    private static final String IMAGES_METADATA_FILE_NAME = "image_only_metadata.json";
    private static final String TAGS_PREFS = "TagsPrefs";
    private static final String TAGS_KEY = "TagsList";
    private Set<String> tagsList;
    private List<String> selectedTags;
    private Uri imageUri;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.photo_view);


        // Initialize tags list with some default tags
        tagsList = new HashSet<>();
        tagsList.add("Nature");
        tagsList.add("Vacation");
        tagsList.add("Family");
        tagsList.add("Friends");

        loadTagsFromPreferences();

        selectedTags = new ArrayList<>();

        Intent intent = getIntent();
        String UriString = intent.getStringExtra("Uri");
        Uri imageUri = Uri.parse(UriString);

        imagePath = resolveImagePathFromUri(this, String.valueOf(imageUri));

        ImageView image = findViewById(R.id.imageView2);
        image.setImageURI(imageUri);

        ToggleButton descTagsToggle = findViewById(R.id.toggleDescTags);

        ImageButton editDescription = findViewById(R.id.editDescription);
        ImageButton addTagToPhoto = findViewById(R.id.addTagToPhoto);


        TextView descriptionView = findViewById(R.id.photoDescription);
        String description = getImageDescriptionByPath(this, imagePath);
        descriptionView.setText(description);

        addTagToPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTagsDialog();
            }
        });

        Button saveButton = findViewById(R.id.savePhotoChangesButton);

        Button editButton = findViewById(R.id.imageEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                descTagsToggle.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                if(descTagsToggle.isChecked()){
                    addTagToPhoto.setVisibility(View.VISIBLE);
                }else{
                    editDescription.setVisibility(View.VISIBLE);
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


        FlexboxLayout tagsContainer = findViewById(R.id.tagsContainer);

        descTagsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (descTagsToggle.isChecked()){
                    addTagToPhoto.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    //tags
                    tagsContainer.setVisibility(View.VISIBLE);
                    tagsContainer.removeAllViews();


                    TextView tagName = new TextView(photo_view.this);
                    tagName.setText("New Item");
                    tagName.setTextSize(16);
                    tagName.setBackgroundColor(getResources().getColor(android.R.color.white));
                    tagName.setTextColor(getResources().getColor(android.R.color.black));

                    // Create a new ImageView
                    ImageView imageView = new ImageView(photo_view.this);
                    imageView.setImageResource(R.drawable.baseline_delete_24); // Replace with your image
                    imageView.setLayoutParams(new FlexboxLayout.LayoutParams(100, 100)); // Adjust dimensions as needed

                    // Add both views to the FlexboxLayout
                    FlexboxLayout.LayoutParams textLayoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    textLayoutParams.setMargins(8, 8, 8, 8); // Add margins for spacing
                    tagName.setLayoutParams(textLayoutParams);

                    FlexboxLayout.LayoutParams imageLayoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    imageLayoutParams.setMargins(8, 8, 8, 8);
                    imageView.setLayoutParams(imageLayoutParams);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    // Add views to Flexbox
                    tagsContainer.addView(tagName);
                    tagsContainer.addView(imageView);

                }
                else{
                    editDescription.setVisibility(View.VISIBLE);
                    addTagToPhoto.setVisibility(View.INVISIBLE);
                    //description
                    tagsContainer.setVisibility(View.INVISIBLE);
                }
            }
        });

        editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDescriptionDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageAndReturn();
                TextView description = findViewById(R.id.photoDescription);
                String newDescription = (String) description.getText();
                Log.d("description", newDescription);
                Log.d("path", imagePath);
                editImageDescription(photo_view.this, imagePath, newDescription);
            }
        });


    }
    private void showEditDescriptionDialog() {
        TextView description = findViewById(R.id.photoDescription);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Description");
        // Set up the input
        final EditText input = new EditText(this);
        input.setText(description.getText());
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newDescription = input.getText().toString();
            description.setText(newDescription);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showEditTagsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags");
        // Convert tags list to an array
        String[] tagsArray = tagsList.toArray(new String[0]);
        boolean[] checkedTags = new boolean[tagsArray.length];
        builder.setMultiChoiceItems(tagsArray, checkedTags, (dialog, which, isChecked) -> {
            // Handle tag selection
            checkedTags[which] = isChecked;
        });
        builder.setPositiveButton("Add Custom Tag", (dialog, which) -> {
            showAddCustomTagDialog();
        });
        builder.setNegativeButton("OK", (dialog, which) -> {
            // Handle OK click
            selectedTags.clear();
            for (int i = 0; i < checkedTags.length; i++) {
                if (checkedTags[i]) {
                    selectedTags.add(tagsArray[i]);
                }
            }

            Toast.makeText(photo_view.this, "Selected Tags: " + selectedTags.toString(), Toast.LENGTH_SHORT).show();
        });
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void showAddCustomTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Custom Tag");
        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTag = input.getText().toString().trim();
            if (!newTag.isEmpty() && !tagsList.contains(newTag)) {
                tagsList.add(newTag);
                saveTagsToPreferences(); // Save the updated tags list
                Toast.makeText(photo_view.this, "Tag added: " + newTag, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(photo_view.this, "Tag already exists or is empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void saveImageAndReturn() {
        Log.d("Tags Selected", selectedTags.toString());
        Intent intentSelf = getIntent();
        String uriString = intentSelf.getStringExtra("Uri");
        Uri newImageUri = Uri.parse(uriString);

            if (newImageUri == null) {
                Toast.makeText(this, "Image URI is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            String filename = null;

            if ("content".equals(newImageUri.getScheme())) {
                // For content scheme Uris (e.g., from ContentResolver)
                try (Cursor cursor = this.getContentResolver().query(newImageUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            filename = cursor.getString(nameIndex);
                        }
                    }
                }
            } else if ("file".equals(newImageUri.getScheme())) {
                // For file scheme Uris
                filename = new File(newImageUri.getPath()).getName();
            }

            File directory = getFilesDir();
            File file = new File(directory, filename);

            // Gather selected tags
            if (tagsList == null || tagsList.isEmpty()) {
                Toast.makeText(this, "No tags selected", Toast.LENGTH_SHORT).show();
                return;
            }


            // Save metadata
            MetadataUtils.saveImageMetadata(this, file.getAbsolutePath(), selectedTags);
            // Return to the main screen
            Intent intent = new Intent(photo_view.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.d("saveImageAndReturn", "Image saved successfully: " + filename);
            finish();
        }

        private void loadTagsFromPreferences() {
            SharedPreferences sharedPreferences = getSharedPreferences(TAGS_PREFS, Context.MODE_PRIVATE);
            String json = sharedPreferences.getString(TAGS_KEY, null);
            if (json != null) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                tagsList = new Gson().fromJson(json, type);
            }
        }
        private void saveTagsToPreferences() {
            SharedPreferences sharedPreferences = getSharedPreferences(TAGS_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(tagsList);
            editor.putString(TAGS_KEY, json);
            editor.apply();
        }
    public static String getImageDescriptionByPath(Context context, String imagePath) {
        BufferedReader reader = null;

        try {
            File metadataFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);
            if (!metadataFile.exists()) {
                Log.e("ImageMetadata", "Metadata file does not exist.");
                return null;
            }

            FileInputStream inputStream = new FileInputStream(metadataFile);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse the JSON
            JSONObject rootObject = new JSONObject(jsonBuilder.toString());
            JSONArray imagesArray = rootObject.getJSONArray("images");

            // Find the image with the specified path
            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject imageObject = imagesArray.getJSONObject(i);
                if (imageObject.getString("path").equals(imagePath)) {
                    return imageObject.getString("description");
                }
            }

        } catch (IOException | JSONException e) {
            Log.e("ImageMetadata", "Error reading metadata file", e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                Log.e("ImageMetadata", "Error closing reader", e);
            }
        }

        // Return null if the image is not found
        return null;
        }
    public static void editImageDescription(Context context, String imagePath, String newDescription) {
        File jsonFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);
        BufferedReader reader = null;

        try {
            if (!jsonFile.exists()) {
                Log.e("editImage", "Metadata file does not exist.");
                return;
            }

            // Read existing JSON file
            FileInputStream inputStream = new FileInputStream(jsonFile);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse JSON
            JSONObject rootObject = new JSONObject(jsonBuilder.toString());
            JSONArray imagesArray = rootObject.getJSONArray("images");

            // Normalize the input imagePath
            String normalizedInputPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;

            boolean found = false;

            // Loop through the images array to find the matching image path
            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject imageObject = imagesArray.getJSONObject(i);
                String storedPath = imageObject.getString("path");
                Log.d("storePath", storedPath);
                // Normalize the stored path for comparison
                String normalizedStoredPath = storedPath.startsWith("/") ? storedPath.substring(1) : storedPath;

                if (normalizedStoredPath.equals(normalizedInputPath)) {
                    // Update the description
                    imageObject.put("description", newDescription);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Log.e("editImage", "Image path not found in the metadata file.");
                return;
            }

            // Write updated JSON back to the file
            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(rootObject.toString(4)); // Pretty print JSON with 4-space indentation
                writer.flush();
            }

            Log.d("editImage", "Image description updated successfully.");
        } catch (JSONException | IOException e) {
            Log.e("editImage", "Error editing image description", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e("editImage", "Error closing reader", e);
            }
        }
    }


    public static String resolveImagePathFromUri(Context context, String uriString) {
        try {
            // Parse the URI string
            Uri uri = Uri.parse(uriString);

            // Check if the URI is from the file scheme
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath(); // Return the file path directly
            }

            // For content scheme URIs
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    return filePath;
                }
            }

            Log.e("ImagePathResolver", "Could not resolve image path from URI: " + uriString);
        } catch (Exception e) {
            Log.e("ImagePathResolver", "Error resolving image path from URI", e);
        }

        return null; // Return null if the path could not be resolved
    }


}




