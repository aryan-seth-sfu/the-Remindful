package com.example.theremindful2;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
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