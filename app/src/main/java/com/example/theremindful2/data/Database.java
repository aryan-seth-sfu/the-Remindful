package com.example.theremindful2.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// DatabaseHelper.java - A helper class to manage all database operations
public class Database {
    private final AppDatabase db;
    private final ImageDao imageDao;
    private final MusicTrackDao musicDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public Database(Context context) {
        db = AppDatabase.getDatabase(context);
        imageDao = db.imageDao();
        musicDao = db.musicTrackDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Interface for callbacks
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // IMAGE OPERATIONS
    public void saveImage(String imagePath, String theme, String caption, DatabaseCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                Image image = new Image(imagePath, theme, caption);
                long id = imageDao.insertImage(image);
                mainHandler.post(() -> callback.onSuccess(id));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void getAllImages(DatabaseCallback<List<Image>> callback) {
        executorService.execute(() -> {
            try {
                List<Image> images = imageDao.getAllImages();
                mainHandler.post(() -> callback.onSuccess(images));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void getImagesByTheme(String theme, DatabaseCallback<List<Image>> callback) {
        executorService.execute(() -> {
            try {
                List<Image> images = imageDao.getImagesByTheme(theme);
                mainHandler.post(() -> callback.onSuccess(images));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    // MUSIC OPERATIONS
    public void saveMusic(String title, String audioPath, DatabaseCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                MusicTrack track = new MusicTrack(title, audioPath);
                long id = musicDao.insertTrack(track);
                mainHandler.post(() -> callback.onSuccess(id));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void getAllMusic(DatabaseCallback<List<MusicTrack>> callback) {
        executorService.execute(() -> {
            try {
                List<MusicTrack> tracks = musicDao.getAllTracks();
                mainHandler.post(() -> callback.onSuccess(tracks));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

}