package com.example.theremindful2;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private PreviewView previewView;
    private ImageView capturedImageView;
    private ImageCapture imageCapture;
    private String outputDirectory;
    private File photoFile;
    private Uri photoUri;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private boolean isBackCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.viewFinder);
        capturedImageView = findViewById(R.id.capturedImageView);
        Button captureButton = findViewById(R.id.camera_capture_button);
        Button switchCameraButton = findViewById(R.id.switch_camera_button);

        outputDirectory = getExternalFilesDir(null) + "/Reminiscences";
        File directory = new File(outputDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        captureButton.setOnClickListener(v -> takePhoto());
        switchCameraButton.setOnClickListener(v -> switchCamera());

        initializeCamera();
    }

    private void initializeCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: ", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;

        // Unbind all use cases before rebinding
        cameraProvider.unbindAll();

        // Select the camera based on the current setting
        cameraSelector = isBackCamera ? CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Bind the camera use cases to the lifecycle
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        photoFile = new File(outputDirectory, "IMG_" + timestamp + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
                        photoUri = Uri.fromFile(photoFile);
                        showSatisfactionDialog(); // Show the satisfaction dialog
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        Toast.makeText(CameraActivity.this, "Error taking photo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSatisfactionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you satisfied with this photo?")
                .setPositiveButton("Yes", (dialog, which) -> saveImageToAlbum(photoFile))
                .setNegativeButton("Retake", (dialog, which) -> {
                    // Allow the user to retake the photo
                    dialog.dismiss();
                })
                .show();
    }

    private void saveImageToAlbum(File photoFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Reminiscences");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContentResolver().openOutputStream(uri).write(Files.readAllBytes(photoFile.toPath()));
                }
                Toast.makeText(this, "Photo saved to album", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error saving photo to album: ", e);
                Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to save photo to album", Toast.LENGTH_SHORT).show();
        }

        // Return the file path to TaskDialogFragment
        Intent resultIntent = new Intent();
        resultIntent.putExtra("photo_path", photoFile.getAbsolutePath());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void switchCamera() {
        // Toggle the camera mode
        isBackCamera = !isBackCamera;
        bindCameraUseCases();
    }
}
