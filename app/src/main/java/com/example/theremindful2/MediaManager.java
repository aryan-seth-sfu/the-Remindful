//package com.example.theremindful2;
//
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//
//public class MediaManager {
//    private DatabaseHelper db;
//    private Context context;
//
//    public MediaManager(Context context) {
//        this.context = context;
//        this.db = DatabaseHelper.getInstance(context);
//    }
//
//    // Add an image from Uri (e.g., when user picks image from gallery)
//    public void addImage(Uri sourceUri, String theme, String description) {
//        try {
//            // Create directory if it doesn't exist
//            File directory = new File(context.getFilesDir(), "images/" + theme);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Create file in app's private storage
//            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
//            File destinationFile = new File(directory, fileName);
//
//            // Copy file to app's private storage
//            InputStream is = context.getContentResolver().openInputStream(sourceUri);
//            OutputStream os = new FileOutputStream(destinationFile);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//            os.flush();
//            os.close();
//            is.close();
//
//            // Add entry to database
//            String filePath = destinationFile.getAbsolutePath();
//            if (db.addImage(context, sourceUri, theme, description)) {
//                Log.d("MediaManager", "Image added successfully");
//            } else {
//                Log.e("MediaManager", "Error adding image");
//            }
//
//        } catch (Exception e) {
//            Log.e("MediaManager", "Error adding image: " + e.getMessage());
//        }
//    }
//
//    // Add audio file (e.g., when user records audio or picks from storage)
//    public void addAudio(Uri sourceUri, String theme, String description) {
//        try {
//            // Create directory if it doesn't exist
//            File directory = new File(context.getFilesDir(), "audio/" + theme);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Create file in app's private storage
//            String fileName = "audio_" + System.currentTimeMillis() + ".mp3";
//            File destinationFile = new File(directory, fileName);
//
//            // Copy file to app's private storage
//            InputStream is = context.getContentResolver().openInputStream(sourceUri);
//            OutputStream os = new FileOutputStream(destinationFile);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//            os.flush();
//            os.close();
//            is.close();
//
//            // Add entry to database
//            String filePath = destinationFile.getAbsolutePath();
//            long id = db.addMediaItem(theme, filePath, description, "audio");
//            Log.d("MediaManager", "Added audio with ID: " + id);
//
//        } catch (Exception e) {
//            Log.e("MediaManager", "Error adding audio: " + e.getMessage());
//        }
//    }
//
//    // Add a new image theme
//    public void addImageTheme(String themeName) {
//        db.addMediaItem(themeName,null,null, "image");
//    }
//
//    // Add a new audio theme
//    public void addAudioTheme(String themeName) {
//        db.addMediaItem(themeName,null,null, "audio");
//    }
//
//    // Get all images for a theme
//    public List<String> getImagesForTheme(String theme) {
//        return db.getImagesByTheme(theme);
//    }
//
//    // Get all audio files for a theme
//    public List<String> getAudioForTheme(String theme) {
//        return db.getAudioByTheme(theme);
//    }
//
//    // Get all themes
//    public List<String> getAllThemes() {
//        return db.getAllThemes();
//    }
//
//    // Get all themes with images
//    public List<Theme> getAllThemesWithImages() {
//        return db.getAllThemesWithImages();
//    }
//
//    // Get description for an image
//    public String getDescription(String filePath) {
//        return db.getDescription(filePath);
//    }
//
//    // Get all theme with a specific image
//    public List<Theme> filterThemesByImagePath(String imagePath) {
//        return db.filterThemesByImagePath(imagePath);
//    }
//
//    // Remove image from a Theme
//    public boolean removeImageFromTheme(String imagePath, String themeName) {
//        return db.removeImageFromTheme(imagePath, themeName);
//    }
//
//
//    public void addImage(Uri newImageUri, List<String> selectedTags, String description) {
//        if (db.addImage(context, newImageUri, selectedTags, description)){
//            Log.d("MediaManager", "Image added successfully");
//        } else {
//            Log.e("MediaManager", "Error adding image");
//
//        }
//    }
//}


package com.example.theremindful2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaManager {
    private DatabaseHelper db;
    private Context context;

    public MediaManager(Context context) {

        this.context = context;
        this.db = DatabaseHelper.getInstance(context);
    }

    // Add an image with multiple themes
    public boolean addImage(Uri sourceUri, List<String> themes, String description) {


        if (db.addImage(context, sourceUri, themes, description)) {
            Log.d("MediaManager", "Image added successfully");
        } else {
            Log.e("MediaManager", "Error adding image");
        }
        return false;
    }

    // Add audio file
    public void addAudio(Uri sourceUri, List<String> themes, String description) {
        try {
            // Create base audio directory
            File baseDir = new File(context.getFilesDir(), "audio");
            baseDir.mkdirs();

            // Create audio file
            String fileName = "audio_" + System.currentTimeMillis() + ".mp3";
            File audioFile = new File(baseDir, fileName);
            String filePath = audioFile.getAbsolutePath();

            // Copy audio file
            try (InputStream is = context.getContentResolver().openInputStream(sourceUri);
                 OutputStream os = new FileOutputStream(audioFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }

            // Add to database
            long mediaId = db.addMediaItem(filePath, description, "audio");

            // Process themes
            for (String theme : themes) {
                long themeId = db.addTheme(theme);
                if (themeId != -1) {
                    db.addMediaTheme(mediaId, themeId);
                }
            }

            Log.d("MediaManager", "Added audio successfully");

        } catch (Exception e) {
            Log.e("MediaManager", "Error adding audio: " + e.getMessage());
        }
    }

    // Get all images for a theme
    public List<String> getImagesForTheme(String theme) {
        return db.getImagesByTheme(theme);
    }

    // Get all audio files for a theme
    public List<String> getAudioForTheme(String theme) {
        return db.getAudioByTheme(theme);
    }

    // Get all themes
    public List<String> getAllThemes() {
        return db.getAllThemes();
    }

    // Get description for an image
    public String getDescription(String filePath) {
        return db.getDescription(filePath);
    }

    // Filter themes by image path
    public List<Theme> filterThemesByImagePath(String imagePath) {
        return db.filterThemesByImagePath(imagePath);
    }

    // Remove image from theme
    public boolean removeImageFromTheme(String imagePath, String themeName) {
        return db.removeImageFromTheme(imagePath, themeName);
    }

    // Update description
    public boolean updateDescription(String filePath, String newDescription) {
        return db.updateDescription(filePath, newDescription) > 0;
    }

    // Delete media item completely
    public void deleteMediaItem(String filePath) {
        // Delete file first
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        // Then remove from database
        db.deleteMediaItem(filePath);
    }

    // Get all theme with images
    public List<Theme> getAllThemesWithImages() {
        return db.getAllThemesWithImages();
    }

    // Add a theme
    public void addTheme(String themeName) {
        db.addTheme(themeName);
    }


}