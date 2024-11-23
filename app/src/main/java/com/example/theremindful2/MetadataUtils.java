package com.example.theremindful2;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
public class MetadataUtils {
    private static final String METADATA_FILE_NAME = "themes_metadata.json";
    private static final String TAG = "MetadataUtils";
    private static final String IMAGES_METADATA_FILE_NAME = "image_only_metadata.json";

    public static void saveImageMetadata(Context context, String imageAbsolutePath, List<String> tags) {
        FileWriter writer = null;
        BufferedReader reader = null;
        Log.d("selected tags in saveImageMetaData", tags.toString());
        try {
            File metadataFile = new File(context.getFilesDir(), METADATA_FILE_NAME);
            JSONObject metadata = new JSONObject();

            // Read existing metadata file if it exists
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


            // Update the themes array with tags and image paths
            JSONArray themesArray = metadata.getJSONArray("themes");
            for (String tag : tags) {
                boolean tagExists = false;

                // Check if the tag already exists
                for (int i = 0; i < themesArray.length(); i++) {
                    JSONObject themeObject = themesArray.getJSONObject(i);

                    if (themeObject.getString("tag").equals(tag)) {
                        tagExists = true;

                        // Add the image path to the images array
                        JSONArray imagesArray = themeObject.getJSONArray("images");
                        if (!imagesArray.toString().contains(imageAbsolutePath)) { // Avoid duplicates
                            imagesArray.put(imageAbsolutePath);
                        }
                        break;
                    }
                }

                // If the tag doesn't exist, create a new theme object
                if (!tagExists) {
                    JSONObject newTheme = new JSONObject();
                    newTheme.put("tag", tag);

                    JSONArray imagesArray = new JSONArray();
                    imagesArray.put(imageAbsolutePath);

                    newTheme.put("images", imagesArray);
                    themesArray.put(newTheme);
                }
            }

            // Write updated metadata back to file
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

                    // Read the image paths array
                    JSONArray imagesArray = themeJson.getJSONArray("images");
                    List<String> imagePaths = new ArrayList<>();

                    for (int j = 0; j < imagesArray.length(); j++) {
                        imagePaths.add(imagesArray.getString(j)); // Load file paths as strings
                    }

                    // Create a Theme object and add it to the list
                    themes.add(new Theme(tag, imagePaths));
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Metadata file not found", e);
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
    // Updated function to load JSON from internal storage and get description
    public static String getDescriptionForImage(String imagePath, Context context) {
        try {
            // Load JSON data from internal storage
            String jsonData = loadJsonFromInternalStorage(context, IMAGES_METADATA_FILE_NAME); // Adjust the filename if needed
            if (jsonData != null) {
                // Parse the JSON data
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray imagesArray = jsonObject.getJSONArray("images");

                // Loop through the images array and match the path
                for (int i = 0; i < imagesArray.length(); i++) {
                    JSONObject imageObject = imagesArray.getJSONObject(i);
                    String path = imageObject.getString("path");
                    String description = imageObject.getString("description");

                    // Check if the paths match
                    if (path.equals(imagePath)) {
                        return description;  // Return the description for the matched image
                    }
                }
            } else {
                Log.e("MetadataUtils", "Failed to load JSON data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Return null if no description is found
    }

    // Helper method to load JSON data from internal storage
    private static String loadJsonFromInternalStorage(Context context, String fileName) {
        String json = null;
        try {
            // Get the file from internal storage
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists()) {
                // Open the file and read its contents
                FileInputStream fis = new FileInputStream(file);
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                fis.close();

                // Convert the byte array to a string
                json = new String(buffer, StandardCharsets.UTF_8);
            } else {
                Log.e("MetadataUtils", "File not found: " + file.getAbsolutePath());
            }
        } catch (IOException ex) {
            Log.e("MetadataUtils", "Error reading file from internal storage: " + ex.getMessage());
            ex.printStackTrace();
        }

        return json;
    }


}