package com.example.theremindful2.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "music_tracks")
public class MusicTrack {
    @PrimaryKey(autoGenerate = true)
    private int trackId;

    @NonNull
    private String title;

    @NonNull
    private String audioPath;
    @ColumnInfo(name = "play_count")
    private int playCount;

    public MusicTrack(@NonNull String title, @NonNull String audioPath) {
        this.title = title;
        this.audioPath = audioPath;
        this.playCount = 0;
    }

    // Getters and setters
    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    @NonNull
    public String getAudioPath() { return audioPath; }
    public void setAudioPath(@NonNull String audioPath) { this.audioPath = audioPath; }

    public int getPlayCount() { return playCount; }
    public void setPlayCount(int playCount) { this.playCount = playCount; }
}