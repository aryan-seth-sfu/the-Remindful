package com.example.theremindful2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.example.theremindful2.data.Audio;
import com.example.theremindful2.data.AudioRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AddRemoveSongs {
    private final AudioRepository repository;
    private List<Audio> currentPlaylist;
    private int currentPlaylistIndex = 0;

    // Constants for SharedPreferences
    private static final String PLAYLIST_ORDER_KEY = "playlist_order";

    public AddRemoveSongs(AudioRepository repository) {
        this.repository = repository;
        this.currentPlaylist = new ArrayList<>();
        loadAudioFromDatabase();
    }

    // Initialize new playlist
    public void newPlaylist() {
        currentPlaylist = new ArrayList<>();
        currentPlaylistIndex = 0;
        loadAudioFromDatabase();
    }

    private void loadAudioFromDatabase() {
        repository.getAllAudio(new AudioRepository.OnAudioLoadedListener() {
            @Override
            public void onAudioLoaded(List<Audio> audioList) {
                currentPlaylist = new ArrayList<>(audioList);
            }
        });
    }

    // Add song to playlist and database
    public void addSong(Uri uri, Context context) {
        String title = getFileNameFromUri(uri, context);
        Audio audio = new Audio(uri.toString(), title, null, null);

        repository.insertAudio(audio, new AudioRepository.OnAudioAddedListener() {
            @Override
            public void onAudioAdded(long audioId) {
                loadAudioFromDatabase();
            }
        });
    }

    // Remove song from playlist and database
    public void removeSong(Audio audio) {
        repository.deleteAudio(audio, new AudioRepository.OnAudioDeletedListener() {
            @Override
            public void onAudioDeleted() {
                currentPlaylist.remove(audio);
                if (currentPlaylistIndex >= currentPlaylist.size()) {
                    currentPlaylistIndex = Math.max(0, currentPlaylist.size() - 1);
                }
            }
        });
    }

    // Get current song
    public Audio getCurrentSong() {
        if (!currentPlaylist.isEmpty() && currentPlaylistIndex < currentPlaylist.size()) {
            return currentPlaylist.get(currentPlaylistIndex);
        }
        return null;
    }

    // Move to next song
    public Audio getNextSong() {
        if (!currentPlaylist.isEmpty()) {
            currentPlaylistIndex = (currentPlaylistIndex + 1) % currentPlaylist.size();
            return getCurrentSong();
        }
        return null;
    }

    // Move to previous song
    public Audio getPreviousSong() {
        if (!currentPlaylist.isEmpty()) {
            if (currentPlaylistIndex == 0) {
                currentPlaylistIndex = currentPlaylist.size() - 1;
            } else {
                currentPlaylistIndex--;
            }
            return getCurrentSong();
        }
        return null;
    }

    // Get current playlist index
    public int getCurrentIndex() {
        return currentPlaylistIndex;
    }

    // Set current playlist index
    public void setCurrentIndex(int index) {
        if (index >= 0 && index < currentPlaylist.size()) {
            currentPlaylistIndex = index;
        }
    }

    // Get all songs in playlist
    public List<Audio> allSongs() {
        return currentPlaylist;
    }

    // Check if playlist is empty
    public boolean isEmpty() {
        return currentPlaylist.isEmpty();
    }

    // Convert playlist to JSON for saving state
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(currentPlaylist);
    }

    // Load playlist from JSON
    public void fromJson(String json) {
        Gson gson = new Gson();
        TypeToken<List<Audio>> token = new TypeToken<List<Audio>>() {};
        List<Audio> savedPlaylist = gson.fromJson(json, token.getType());
        if (savedPlaylist != null) {
            currentPlaylist = savedPlaylist;
        }
    }

    // Shuffle playlist
    public void shufflePlaylist() {
        if (!currentPlaylist.isEmpty()) {
            Audio currentSong = getCurrentSong();
            java.util.Collections.shuffle(currentPlaylist);
            // If there was a current song, find its new position
            if (currentSong != null) {
                currentPlaylistIndex = currentPlaylist.indexOf(currentSong);
            }
        }
    }

    // Clear playlist
    public void clearPlaylist() {
        currentPlaylist.clear();
        currentPlaylistIndex = 0;
    }

    // Get playlist size
    public int getPlaylistSize() {
        return currentPlaylist.size();
    }

    private String getFileNameFromUri(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    // Search functionality
    public void searchSongs(String query) {
        repository.searchAudio(query, new AudioRepository.OnAudioLoadedListener() {
            @Override
            public void onAudioLoaded(List<Audio> audioList) {
                currentPlaylist = new ArrayList<>(audioList);
                currentPlaylistIndex = 0;
            }
        });
    }

    // Get recent songs
    public void getRecentSongs(int limit) {
        repository.getRecentAudio(limit, new AudioRepository.OnAudioLoadedListener() {
            @Override
            public void onAudioLoaded(List<Audio> audioList) {
                currentPlaylist = new ArrayList<>(audioList);
                currentPlaylistIndex = 0;
            }
        });
    }

    // Refresh playlist from database
    public void refreshPlaylist() {
        loadAudioFromDatabase();
    }
}