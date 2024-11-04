package com.example.theremindful2;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.theremindful2.data.Image;

import java.util.Arrays;
import java.util.List;

public class Theme {
    private final String name;
    private final List<Image> photos;

    public Theme(String name, List<Image> photos) {
        this.name = name;
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public List<Image> getPhotos() {
        return photos;
    }
    public void addPhoto(Image photo) {
        photos.add(photo);
    }
}