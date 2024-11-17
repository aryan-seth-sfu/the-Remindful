package com.example.theremindful2;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MetadataUtils {

    private static final String METADATA_FILE_NAME = "themes_metadata.json";
    private static final String TAG = "MetadataUtils";

    // Change the image identifier from int (drawable resource ID) to String (file path)
    public static void saveImageMetadata(Context context, String imagePath, List<String> selectedTags) {
        FileWriter writer = null;
        BufferedReader reader = null;
        try {
            File metadataFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
            JSONObject metadata = new JSONObject();

            // Read existing metadata if it exists
            if (metadataFile.exists()) {
                FileInputStream inputStream = new FileInputStream(metadataFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                metadata = new JSONObject(jsonBuilder.toString());
            } else {
                metadata.put("themes", new JSONArray());
            }

            // Get the existing themes array
            JSONArray themesArray = metadata.getJSONArray("themes");

            // Iterate through the selected tags
            for (String tag : selectedTags) {
                boolean tagExists = false;

                // Iterate through themesArray to find the theme with the matching tag
                for (int i = 0; i < themesArray.length(); i++) {
                    JSONObject themeObject = themesArray.getJSONObject(i);
                    if (themeObject.getString("tag").equals(tag)) {
                        // Tag exists, add the image to this theme
                        tagExists = true;
                        JSONArray imagesArray = themeObject.getJSONArray("images");

                        // Avoid duplicate images being added to the theme
                        if (!isImageAlreadyAdded(imagesArray, imagePath)) {
                            imagesArray.put(imagePath);
                        }
                        break;
                    }
                }

                // If the tag does not exist, create a new theme for it
                if (!tagExists) {
                    JSONObject newTheme = new JSONObject();
                    newTheme.put("tag", tag);
                    JSONArray imagesArray = new JSONArray();
                    imagesArray.put(imagePath);
                    newTheme.put("images", imagesArray);
                    themesArray.put(newTheme);
                }
            }

            // Write updated metadata to the file
            writer = new FileWriter(metadataFile);
            writer.write(metadata.toString());

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error saving image metadata", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources", e);
            }
        }
    }

    // Helper method to check if an image is already in the images array
    private static boolean isImageAlreadyAdded(JSONArray imagesArray, String imagePath) {
        for (int i = 0; i < imagesArray.length(); i++) {
            if (imagesArray.optString(i).equals(imagePath)) {
                return true;  // Image is already added
            }
        }
        return false;
    }

    public static List<Theme> loadThemesFromStorage(Context context) {
        List<Theme> themes = new ArrayList<>();
        BufferedReader reader = null;
        try {
            File metadataFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
            if (metadataFile.exists()) {
                FileInputStream inputStream = new FileInputStream(metadataFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                // Deserialize JSON
                JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
                JSONArray themesArray = jsonObject.getJSONArray("themes");
                for (int i = 0; i < themesArray.length(); i++) {
                    JSONObject themeJson = themesArray.getJSONObject(i);
                    String tag = themeJson.getString("tag");
                    JSONArray imagesArray = themeJson.getJSONArray("images");
                    List<String> imagePaths = new ArrayList<>();
                    for (int j = 0; j < imagesArray.length(); j++) {
                        imagePaths.add(imagesArray.getString(j));
                    }
                    themes.add(new Theme(tag, imagePaths));
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading themes from storage", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing reader", e);
            }
        }
        return themes;
    }
}
