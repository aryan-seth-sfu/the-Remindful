package com.example.theremindful2.data;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

public class AudioRepository {
    private final AudioDao audioDao;

    public AudioRepository(AppDatabase database) {
        this.audioDao = database.audioDao();  // Changed from AudioDao() to audioDao()
    }

    public void getAllAudio(OnAudioLoadedListener listener) {
        new Thread(() -> {
            List<Audio> audioList = audioDao.getAllAudio();
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onAudioLoaded(audioList));
        }).start();
    }

    public void insertAudio(Audio audio, OnAudioAddedListener listener) {
        new Thread(() -> {
            long id = audioDao.insertAudio(audio);  // Changed from insert() to insertAudio()
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onAudioAdded(id));
        }).start();
    }

    public void deleteAudio(Audio audio, OnAudioDeletedListener listener) {
        new Thread(() -> {
            audioDao.deleteAudio(audio);  // Changed from delete() to deleteAudio()
            new Handler(Looper.getMainLooper()).post(
                    listener::onAudioDeleted);
        }).start();
    }

    public void getAudioByDateRange(long startDate, long endDate, OnAudioLoadedListener listener) {
        new Thread(() -> {
            List<Audio> audioList = audioDao.getAudioByDateRange(startDate, endDate);
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onAudioLoaded(audioList));
        }).start();
    }

    public void getAudioById(long audioId, OnSingleAudioLoadedListener listener) {
        new Thread(() -> {
            Audio audio = audioDao.getAudioById(audioId);
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onSingleAudioLoaded(audio));
        }).start();
    }

    public void getRecentAudio(int limit, OnAudioLoadedListener listener) {
        new Thread(() -> {
            List<Audio> audioList = audioDao.getRecentAudio(limit);
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onAudioLoaded(audioList));
        }).start();
    }

    public void searchAudio(String query, OnAudioLoadedListener listener) {
        new Thread(() -> {
            List<Audio> audioList = audioDao.searchAudio(query);
            new Handler(Looper.getMainLooper()).post(() ->
                    listener.onAudioLoaded(audioList));
        }).start();
    }

    // Callback interfaces
    public interface OnAudioLoadedListener {
        void onAudioLoaded(List<Audio> audioList);
    }

    public interface OnAudioAddedListener {
        void onAudioAdded(long audioId);
    }

    public interface OnAudioDeletedListener {
        void onAudioDeleted();
    }

    public interface OnSingleAudioLoadedListener {
        void onSingleAudioLoaded(Audio audio);
    }
}