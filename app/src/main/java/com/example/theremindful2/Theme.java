package com.example.theremindful2;

import java.util.List;

public class Theme {
    private final String name;
    private final List<String> photos;  // Changed from List<Integer> to List<String>

    public Theme(String name, List<String> photos) {
        this.name = name;
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhotos() {
        return photos;
    }
}
