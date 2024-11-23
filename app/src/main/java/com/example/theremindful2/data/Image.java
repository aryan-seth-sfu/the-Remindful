package com.example.theremindful2.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class Image {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String filename;
    private String uri;
    private String description;
    private String theme;
    private long dateTaken;
    private long createdAt;

    public Image(String filename, String uri, String description, String theme) {
        this.filename = filename;
        this.uri = uri;
        this.description = description;
        this.theme = theme;
        this.dateTaken = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public long getDateTaken() { return dateTaken; }
    public void setDateTaken(long dateTaken) { this.dateTaken = dateTaken; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}