package com.example.theremindful2;
import java.util.List;

public class Theme {
    private final String name;
    private final List<Integer> photos;

    public Theme(String name, List<Integer> photos) {
        this.name = name;
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPhotos() {
        return photos;
    }
}
