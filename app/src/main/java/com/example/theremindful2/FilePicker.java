package com.example.theremindful2;
import com.example.theremindful2.MainActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FilePicker extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private RecyclerView selectedFilesRecyclerView;
    private TextView selectedFileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't show layout initially, directly open file picker
        openFilePicker();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");  // For images only
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

                        Intent i = new Intent(FilePicker.this, MainActivity.class);
                        setResult(RESULT_OK, i);

//                        i.putExtra("filePATH",FileAbsPath);
                        startActivity(i);

                        finish();
                    }
                });
            } else {
                Intent i = new Intent(FilePicker.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            // If user canceled the picker, finish the activity
            Intent i = new Intent(FilePicker.this, MainActivity.class);
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
}
//    private static final int PICK_FILE_REQUEST_CODE = 1;
//    private static final String TAG = "FilePicker";
//    private Uri selectedFileUri;
//    private TextView selectedFileTextView;
//    private Button selectFileButton;
//    private FloatingActionButton uploadButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        System.out.println("open file picker here");
//
//        // Initialize views
////        selectedFileTextView = findViewById(R.id.selected_file_text_view);
////        selectFileButton = findViewById(R.id.selectFileButton);
//        uploadButton = findViewById(R.id.upload_button);
//
//        // Initially disable upload button
////        uploadButton.setEnabled(false);
//
//        uploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                System.out.println("open file picker here");
//
//                openFilePicker();
//            }
//        });
//
////        uploadButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (selectedFileUri != null) {
////                    uploadFile(selectedFileUri);
////                }
////            }
////        });
//    }
//
//    private void openFilePicker() {
//        System.out.println("inside open file Picker");
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*"); // All file types
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        try {
//            startActivityForResult(
//                    Intent.createChooser(intent, "Select a file"),
//                    PICK_FILE_REQUEST_CODE
//            );
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "Please install a file manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
////            if (data != null) {
////                selectedFileUri = data.getData();
////                String fileName = getFileName(selectedFileUri);
////                selectedFileTextView.setText("Selected File: " + fileName);
////                uploadButton.setEnabled(true); // Enable upload button
////                Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
////
////    private String getFileName(Uri uri) {
////        String result = null;
////        if (uri.getScheme().equals("content")) {
////            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
////                if (cursor != null && cursor.moveToFirst()) {
////                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
////                    if (index != -1) {
////                        result = cursor.getString(index);
////                    }
////                }
////            }
////        }
////        if (result == null) {
////            result = uri.getPath();
////            int cut = result.lastIndexOf('/');
////            if (cut != -1) {
////                result = result.substring(cut + 1);
////            }
////        }
////        return result;
////    }
////
////    private void uploadFile(Uri fileUri) {
////        // Show progress dialog
////        ProgressDialog progressDialog = new ProgressDialog(this);
////        progressDialog.setTitle("Uploading");
////        progressDialog.setMessage("Please wait...");
////        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
////        progressDialog.setProgress(0);
////        progressDialog.setMax(100);
////        progressDialog.show();
////
////        // Simulate upload progress (replace with actual upload code)
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    // Simulate file upload progress
////                    for (int i = 0; i <= 100; i += 10) {
////                        Thread.sleep(200); // Simulate network delay
////                        int finalI = i;
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                progressDialog.setProgress(finalI);
////                            }
////                        });
////                    }
////
////                    // Upload complete
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            progressDialog.dismiss();
////                            Toast.makeText(file_picker.this,
////                                    "Upload complete!", Toast.LENGTH_SHORT).show();
////                            // Optionally return result to MainActivity
////                            Intent resultIntent = new Intent();
////                            resultIntent.putExtra("fileUri", fileUri.toString());
////                            setResult(RESULT_OK, resultIntent);
////                            finish();
////                        }
////                    });
////
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        }).start();
////    }


