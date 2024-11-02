package com.example.theremindful2;

import androidx.annotation.NonNull;
import java.util.Objects;

public class Photo {
    private final String theme;
    private final int resourceId;

    public Photo(String theme, int resourceId) {
        this.theme = theme;
        this.resourceId = resourceId;
    }

    // Getters
    public String getTheme() {
        return theme;
    }

    public int getResourceId() {
        return resourceId;
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return resourceId == photo.resourceId &&
                Objects.equals(theme, photo.theme);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(theme, resourceId);
    }

    // toString() method
    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
                "theme='" + theme + '\'' +
                ", resourceId=" + resourceId +
                '}';
    }
}