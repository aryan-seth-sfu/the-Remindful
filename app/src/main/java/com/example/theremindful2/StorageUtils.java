package com.example.theremindful2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


// the music files and images are typically binary files, so we save them differently see below
// the saveToFile and loadFromFile functions are used for textual data

public class StorageUtils {

    // Save function: Saves a string to a specified file in the internal storage
    public static boolean saveToFile(Context context, String filename, String content) {
        File directory = context.getFilesDir();  // Internal storage directory
        File file = new File(directory, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            return true;  // Return true if the save operation was successful
        } catch (IOException e) {
            Log.e("FileUtils", "Error saving file: " + e.getMessage());
            e.printStackTrace();
            return false;  // Return false if an error occurred
        }
    }

    // Load function: Loads a string from a specified file in the internal storage
    public static String loadFromFile(Context context, String filename) {
        File directory = context.getFilesDir();  // Internal storage directory
        File file = new File(directory, filename);

        if (!file.exists()) {
            Log.e("FileUtils", "File not found: " + filename);
            return null;  // Return null if the file does not exist
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading file: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null if an error occurred
        }
        return content.toString();  // Return the content of the file
    }

    // Save an image file
    public static Uri saveImageFile(Context context, String filename, byte[] imageBytes) {
        File directory = context.getFilesDir();  // Internal storage directory
        File file = new File(directory, filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
            return Uri.fromFile(file);  // Return the Uri of the saved file
        } catch (IOException e) {
            Log.e("FileUtils", "Error saving image file: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null if an error occurred
        }
    }

    // Save a music file
    public static Uri saveMusicFile(Context context, String filename, byte[] musicBytes) {
        File directory = context.getFilesDir();  // Internal storage directory
        File file = new File(directory, filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(musicBytes);
            return Uri.fromFile(file);  // Return the Uri of the saved music file
        } catch (IOException e) {
            Log.e("FileUtils", "Error saving music file: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null if an error occurred
        }
    }

    // Load a music file
    public static byte[] loadMusicFile(Context context, String filename) {
        File directory = context.getFilesDir();  // Internal storage directory
        File file = new File(directory, filename);

        if (!file.exists()) {
            Log.e("FileUtils", "File not found: " + filename);
            return null;  // Return null if the file does not exist
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] musicBytes = new byte[(int) file.length()];
            fis.read(musicBytes);
            return musicBytes;  // Return the byte array of the music file
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading music file: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null if an error occurred
        }
    }
}
