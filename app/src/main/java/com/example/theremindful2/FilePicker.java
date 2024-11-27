package com.example.theremindful2;
import com.example.theremindful2.MainActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.UUID;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FilePicker extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private RecyclerView selectedFilesRecyclerView;
    private TextView selectedFileTextView;
    private static final String IMAGES_METADATA_FILE_NAME = "image_only_metadata.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't show layout initially, directly open file picker
        openFilePicker();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(getString(R.string.ImageSetType));  // For images only
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                // Now show the layout with selected file
                setContentView(R.layout.file_picker);

                Uri selectedFileUri = data.getData();
                String fileName = getFileName(selectedFileUri);

                // Find views after setting content view
                selectedFileTextView = findViewById(R.id.selectedFileTextView);
                ImageView selectedImageView = findViewById(R.id.selectedImageView);

                // Show file name
                selectedFileTextView.setText("Selected: " + fileName);

                // Show image preview
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedFileUri);
                    selectedImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Setup confirm button if needed
                Button confirmButton = findViewById(R.id.confirmButton);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle confirmation (e.g., save file URI, return to previous activity)
//                        Intent resultIntent = new Intent();
//                        i.setData(selectedFileUri);
//                        String FileAbsPath = saveImageToInternalStorage(selectedFileUri,fileName);

                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedFileUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (bitmap != null) {
                            try {
                                String fileName = "image_" + UUID.randomUUID().toString() + getString(R.string.imageFileType);

                                String filepath = getFilesDir() + "/" +fileName;
                                createMetadataFileIfNotExists(FilePicker.this);
                                addImageToMetadataFile(FilePicker.this, filepath ,"");

                                // Define the file name and location
                                File file = new File(getFilesDir(), fileName);

                                // Open a file output stream to save the image
                                FileOutputStream fos = new FileOutputStream(file);

                                // Compress the bitmap and write to the output stream
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                                // Close the output stream
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        Intent i = new Intent(FilePicker.this, CareGiverImagesSettingsActivity.class);
                        setResult(RESULT_OK, i);

//                        i.putExtra("filePATH",FileAbsPath);
                        startActivity(i);

                        finish();
                    }
                });
            } else {
                Intent i = new Intent(FilePicker.this, CareGiverImagesSettingsActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            // If user canceled the picker, finish the activity
            Intent i = new Intent(FilePicker.this, CareGiverImagesSettingsActivity.class);
            startActivity(i);
            finish();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String saveImageToInternalStorage(Uri imageUri, String fileName) {
        try {
            // Convert URI to Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Create file
            File directory = getFilesDir();
            File imageFile = new File(directory, fileName);

            // Save bitmap to file
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void addImageToMetadataFile(Context context, String imagePath, String description) {
        FileWriter writer = null;
        BufferedReader reader = null;

        try {
            // Define the JSON file
            File imageMetadataFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);

            // Create a root JSON object
            JSONObject rootObject = new JSONObject();
            JSONArray imagesArray = new JSONArray();

            // If the file exists, read the existing data
            if (imageMetadataFile.exists()) {
                FileInputStream inputStream = new FileInputStream(imageMetadataFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                rootObject = new JSONObject(jsonBuilder.toString());
                imagesArray = rootObject.getJSONArray("images");
            }

            // Create a new JSON object for the image
            JSONObject imageObject = new JSONObject();
            imageObject.put("path", imagePath);
            imageObject.put("description", description);

            // Add the image object to the array
            imagesArray.put(imageObject);

            // Update the root object
            rootObject.put("images", imagesArray);

            // Write the updated JSON back to the file
            writer = new FileWriter(imageMetadataFile);
            writer.write(rootObject.toString(4)); // Pretty-print with 4 spaces
            writer.flush();

        } catch (IOException | JSONException e) {
            Log.e("ImageMetadata", "Error updating image metadata file", e);
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
            } catch (IOException e) {
                Log.e("ImageMetadata", "Error closing resources", e);
            }
        }
    }
    public static void createMetadataFileIfNotExists(Context context) {
        File metadataFile = new File(context.getFilesDir(), IMAGES_METADATA_FILE_NAME);

        if (!metadataFile.exists()) {
            try (FileWriter writer = new FileWriter(metadataFile)) {
                // Initialize the file with an empty "images" array
                JSONObject rootObject = new JSONObject();
                rootObject.put("images", new JSONArray());
                writer.write(rootObject.toString(4)); // Pretty-print with 4 spaces
                writer.flush();
                Log.d("Metadata", "Metadata file created successfully.");
            } catch (IOException | JSONException e) {
                Log.e("Metadata", "Error creating metadata file", e);
            }
        } else {
            Log.d("Metadata", "Metadata file already exists.");
        }
    }
}
