package com.example.theremindful2;

public class AnalyticsItem {
    private final String title;
    private final int iconResId; // Optional icon resource ID

    // Constructor accepting title and optional icon
    public AnalyticsItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for icon resource ID
    public int getIconResId() {
        return iconResId;
    }
}
