package com.example.theremindful2.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audio_entries")
public class Audio {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String audioPath;
    private String description;
    private Long duration;  // Duration in milliseconds
    private Long dateRecorded;
    private long createdAt;

    public Audio(String title, String audioPath, String description, Long duration) {
        this.title = title;
        this.audioPath = audioPath;
        this.description = description;
        this.duration = duration;
        this.dateRecorded = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Long getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(Long dateRecorded) { this.dateRecorded = dateRecorded; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}