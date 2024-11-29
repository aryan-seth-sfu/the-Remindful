package com.example.theremindful2;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaManager {
    private DatabaseHelper db;
    private Context context;

    private static int time_add_image;

    public MediaManager(Context context) {

        this.context = context;
        this.db = DatabaseHelper.getInstance(context);
    }

    // Add an image with multiple themes
    public void addImage(Uri sourceUri, List<String> themes, String description) {
        time_add_image++;
        if (time_add_image > 10) {
//            uploadToFirebase();
        }

        String fp = db.addImage(context, sourceUri, themes, description);
        uploadToFirebase(fp, themes, description);
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
            uploadAudioToFirebase(filePath, themes, description);

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

    // new online
    private void uploadToFirebase(String localFilePath, List<String> themes, String description) {
        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create reference using the existing filename
        File localFile = new File(localFilePath);
        String fileName = localFile.getName(); // This will be your "img_timestamp.jpg"
        StorageReference imageRef = storageRef.child("images/" + fileName);

        // Upload file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(Uri.fromFile(localFile));

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get download URL after successful upload
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                // Upload metadata to Realtime Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mediaRef = database.getReference("media");
                String mediaId = mediaRef.push().getKey();

                if (mediaId != null) {
                    // Create media entry
                    Map<String, Object> mediaItem = new HashMap<>();
                    mediaItem.put("localFilePath", localFilePath);
                    mediaItem.put("firebaseUrl", downloadUri.toString());
                    mediaItem.put("description", description);
                    mediaItem.put("themes", themes);
                    mediaItem.put("timestamp", System.currentTimeMillis());
                    mediaItem.put("type", "image");

                    // Save to Firebase Database
                    mediaRef.child(mediaId).setValue(mediaItem)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("MediaManager", "Image uploaded to Firebase successfully"))
                            .addOnFailureListener(e ->
                                    Log.e("MediaManager", "Failed to save metadata: " + e.getMessage()));
                }
            });
        }).addOnFailureListener(e ->
                Log.e("MediaManager", "Failed to upload image: " + e.getMessage()));
    }

    private void uploadAudioToFirebase(String localFilePath, List<String> themes, String description) {
        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create reference using the existing filename
        File localFile = new File(localFilePath);
        String fileName = localFile.getName(); // Will be "audio_timestamp.mp3"
        StorageReference audioRef = storageRef.child("audio/" + fileName);

        // Upload file to Firebase Storage
        UploadTask uploadTask = audioRef.putFile(Uri.fromFile(localFile));

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get download URL after successful upload
            audioRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                // Upload metadata to Realtime Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mediaRef = database.getReference("media");
                String mediaId = mediaRef.push().getKey();

                if (mediaId != null) {
                    // Create media entry
                    Map<String, Object> mediaItem = new HashMap<>();
                    mediaItem.put("localFilePath", localFilePath);
                    mediaItem.put("firebaseUrl", downloadUri.toString());
                    mediaItem.put("description", description);
                    mediaItem.put("themes", themes);
                    mediaItem.put("timestamp", System.currentTimeMillis());
                    mediaItem.put("type", "audio");

                    // Save to Firebase Database
                    mediaRef.child(mediaId).setValue(mediaItem)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("MediaManager", "Audio uploaded to Firebase successfully"))
                            .addOnFailureListener(e ->
                                    Log.e("MediaManager", "Failed to save audio metadata: " + e.getMessage()));
                }
            });
        }).addOnFailureListener(e ->
                Log.e("MediaManager", "Failed to upload audio: " + e.getMessage()));
    }

    public void initialize() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
//        executor.execute(() -> {

        db.fetchAllFromFirebase(new DatabaseHelper.FetchCompleteCallback() {
            @Override
            public void onComplete(boolean success, String error) {
                if (success) {
                    Log.d("MediaManager", "Data fetched from Firebase successfully");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("flag", true);
                    startActivity(context, intent, null);

                } else {
                    Log.e("MediaManager", "Error fetching data from Firebase: " + error);
                }
            }
        });
//        });
    }



//    // Online Functionality
//    // Updated MediaManager functions with Firebase Storage support
//
//    public void uploadToFirebase(String localFilePath, List<String> themes, String description,
//                                 UploadProgressCallback progressCallback) {
//        // Initialize Firebase Storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        // Create file reference with unique name
//        String fileName = "media_" + System.currentTimeMillis() + "_" + new File(localFilePath).getName();
//        String mediaType = localFilePath.contains("/audio/") ? "audio" : "images";
//        StorageReference fileRef = storageRef.child(mediaType + "/" + fileName);
//
//        // Upload file to Firebase Storage
//        Uri fileUri = Uri.fromFile(new File(localFilePath));
//        UploadTask uploadTask = fileRef.putFile(fileUri);
//
//        // Monitor upload progress
//        uploadTask.addOnProgressListener(snapshot -> {
//            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//            progressCallback.onProgress(progress);
//        }).addOnSuccessListener(taskSnapshot -> {
//            // Get download URL after successful upload
//            fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
//                // Now upload metadata to Realtime Database
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference mediaRef = database.getReference("media");
//                String mediaId = mediaRef.push().getKey();
//
//                if (mediaId == null) {
//                    progressCallback.onError("Failed to create Firebase key");
//                    return;
//                }
//
//                // Create media entry with download URL
//                MediaItem mediaItem = new MediaItem();
//                mediaItem.setLocalFilePath(localFilePath);
//                mediaItem.setFirebaseUrl(downloadUri.toString());
//                mediaItem.setDescription(description);
//                mediaItem.setThemes(themes);
//                mediaItem.setTimestamp(System.currentTimeMillis());
//                mediaItem.setType(mediaType);
//
//                // Upload metadata to Realtime Database
//                mediaRef.child(mediaId).setValue(mediaItem)
//                        .addOnSuccessListener(aVoid -> {
//                            progressCallback.onComplete(mediaItem);
//                        })
//                        .addOnFailureListener(e -> {
//                            progressCallback.onError("Error uploading metadata: " + e.getMessage());
//                        });
//            });
//        }).addOnFailureListener(e -> {
//            progressCallback.onError("Error uploading file: " + e.getMessage());
//        });
//    }
//
//    public void fetchFromFirebase(String themeFilter, Integer limit, boolean downloadFiles,
//                                  String downloadDirectory, FirebaseCallback callback) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference mediaRef = database.getReference("media");
//        Query query = mediaRef;
//
//        if (themeFilter != null) {
//            query = query.orderByChild("themes")
//                    .equalTo(themeFilter);
//        }
//
//        if (limit != null) {
//            query = query.limitToLast(limit);
//        }
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<MediaItem> mediaItems = new ArrayList<>();
//                AtomicInteger pendingDownloads = new AtomicInteger(0);
//
//                if (!snapshot.hasChildren()) {
//                    callback.onComplete(mediaItems);
//                    return;
//                }
//
//                for (DataSnapshot mediaSnapshot : snapshot.getChildren()) {
//                    MediaItem item = mediaSnapshot.getValue(MediaItem.class);
//                    if (item != null) {
//                        mediaItems.add(item);
//
//                        if (downloadFiles && item.getFirebaseUrl() != null) {
//                            pendingDownloads.incrementAndGet();
//                            downloadFile(item, downloadDirectory, new DownloadCallback() {
//                                @Override
//                                public void onComplete(String localPath) {
//                                    item.setLocalFilePath(localPath);
//                                    if (pendingDownloads.decrementAndGet() == 0) {
//                                        callback.onComplete(mediaItems);
//                                    }
//                                }
//
//                                @Override
//                                public void onError(String error) {
//                                    Log.e("MediaManager", "Download error: " + error);
//                                    if (pendingDownloads.decrementAndGet() == 0) {
//                                        callback.onComplete(mediaItems);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
//
//                if (pendingDownloads.get() == 0) {
//                    callback.onComplete(mediaItems);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                callback.onError(error.getMessage());
//            }
//        });
//    }
//
//    private void downloadFile(MediaItem item, String downloadDirectory, DownloadCallback callback) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference fileRef = storage.getReferenceFromUrl(item.getFirebaseUrl());
//
//        // Create directory if it doesn't exist
//        File directory = new File(context.getFilesDir(), downloadDirectory);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        // Create local file
//        String fileName = fileRef.getName();
//        File localFile = new File(directory, fileName);
//
//        fileRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//            callback.onComplete(localFile.getAbsolutePath());
//        }).addOnFailureListener(e -> {
//            callback.onError(e.getMessage());
//        });
//    }
//
//    // Updated MediaItem class
//    public static class MediaItem {
//        private String localFilePath;
//        private String firebaseUrl;
//        private String description;
//        private List<String> themes;
//        private long timestamp;
//        private String type;
//
//        // Default constructor required for Firebase
//        public MediaItem() {}
//
//        // Add getters and setters for new fields
//        public String getLocalFilePath() { return localFilePath; }
//        public void setLocalFilePath(String localFilePath) { this.localFilePath = localFilePath; }
//
//        public String getFirebaseUrl() { return firebaseUrl; }
//        public void setFirebaseUrl(String firebaseUrl) { this.firebaseUrl = firebaseUrl; }
//
//        // ... (other getters and setters remain the same)
//        public String getFilePath() { return localFilePath; }
//        public void setFilePath(String filePath) { this.localFilePath = filePath; }
//
//        public String getDescription() { return description; }
//        public void setDescription(String description) { this.description = description; }
//
//        public List<String> getThemes() { return themes; }
//        public void setThemes(List<String> themes) { this.themes = themes; }
//
//        public long getTimestamp() { return timestamp; }
//        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
//
//        public String getType() { return type; }
//        public void setType(String type) { this.type = type; }
//    }
//
//    // Callback interfaces
//    public interface UploadProgressCallback {
//        void onProgress(double percentage);
//        void onComplete(MediaItem mediaItem);
//        void onError(String error);
//    }
//
//    public interface DownloadCallback {
//        void onComplete(String localPath);
//        void onError(String error);
//    }
//
//    public interface FirebaseCallback {
//        void onComplete(List<MediaItem> mediaItems);
//        void onError(String error);
//    }

}