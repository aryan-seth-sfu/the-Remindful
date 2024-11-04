package com.example.theremindful2.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class Image {
    @PrimaryKey(autoGenerate = true)
    private int imageId;

    @NonNull
    private String imagePath;

    @NonNull
    private String theme;

    private String caption;

    public Image(@NonNull String imagePath, @NonNull String theme, String caption) {
        this.imagePath = imagePath;
        this.theme = theme;
        this.caption = caption;
    }

    // Getters and setters
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }

    @NonNull
    public String getImagePath() { return imagePath; }
    public void setImagePath(@NonNull String imagePath) { this.imagePath = imagePath; }

    @NonNull
    public String getTheme() { return theme; }
    public void setTheme(@NonNull String theme) { this.theme = theme; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
}