package com.example.theremindful2.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageRepository {
    private final ImageDao imageDao;
    private final ExecutorService executorService;

    public ImageRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        imageDao = db.imageDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getAllImages(final ImageDataCallback callback) {
        executorService.execute(() -> {
            List<Image> images = imageDao.getAllImages();
            new Handler(Looper.getMainLooper()).post(() -> callback.onImageDataReceived(images));
        });
    }

    public void getImagesByTheme(String theme, final ImageDataCallback callback) {
        executorService.execute(() -> {
            List<Image> images = imageDao.getImagesByTheme(theme);
            new Handler(Looper.getMainLooper()).post(() -> callback.onImageDataReceived(images));
        });
    }

    public void saveImage(Image image, final SaveImageCallback callback) {
        executorService.execute(() -> {
            long id = imageDao.insertImage(image);
            new Handler(Looper.getMainLooper()).post(() -> callback.onImageSaved(id > 0));
        });
    }

    public void getAllThemes(final ThemeDataCallback callback) {
        executorService.execute(() -> {
            List<String> themes = imageDao.getAllThemes();
            new Handler(Looper.getMainLooper()).post(() -> callback.onThemeDataReceived(themes));
        });
    }

    // Callback interfaces
    public interface ImageDataCallback {
        void onImageDataReceived(List<Image> images);
    }

    public interface SaveImageCallback {
        void onImageSaved(boolean success);
    }

    public interface ThemeDataCallback {
        void onThemeDataReceived(List<String> themes);
    }
}