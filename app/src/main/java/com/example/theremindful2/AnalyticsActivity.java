package com.example.theremindful2;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AnalyticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Setup RecyclerView and ProgressBar
        recyclerView = findViewById(R.id.analyticsRecyclerView);
        progressBar = findViewById(R.id.analyticsProgressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display analytics data
        loadAnalyticsData();
    }

    // Method to load analytics data
    private void loadAnalyticsData() {
        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Simulate data loading (use background threads or async tasks for real-world apps)
        recyclerView.postDelayed(() -> {
            List<AnalyticsAdapter.AnalyticsItem> analyticsData = getAnalyticsData();

            if (analyticsData.isEmpty()) {
                Toast.makeText(this, "No analytics data available", Toast.LENGTH_SHORT).show();
            } else {
                // Set adapter with data
                adapter = new AnalyticsAdapter(analyticsData, this);
                recyclerView.setAdapter(adapter);
            }

            // Hide ProgressBar and show RecyclerView
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }, 1000); // Simulated delay for data loading
    }

    // Fetch analytics data
    private List<AnalyticsAdapter.AnalyticsItem> getAnalyticsData() {
        List<AnalyticsAdapter.AnalyticsItem> analyticsData = new ArrayList<>();

        // Most Viewed Themes
        List<String> mostViewedThemes = databaseHelper.getMostViewedThemes();
        if (!mostViewedThemes.isEmpty()) {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("Most Viewed Themes", 0)); // Section header
            for (String theme : mostViewedThemes) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(theme, R.drawable.ic_theme));
            }
        } else {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("No themes viewed yet", R.drawable.ic_placeholder));
        }

        // Most Viewed Photos
        List<String> mostViewedPhotos = databaseHelper.getMostViewedPhotos();
        if (!mostViewedPhotos.isEmpty()) {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("Most Viewed Photos", 0)); // Section header
            for (String photo : mostViewedPhotos) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(photo, R.drawable.ic_photo));
            }
        } else {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("No photos viewed yet", R.drawable.ic_placeholder));
        }

        // Likes Count for Themes
        List<String> likedThemes = databaseHelper.getLikesCountForThemes();
        if (!likedThemes.isEmpty()) {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("Likes Count for Themes", 0)); // Section header
            for (String theme : likedThemes) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(theme, R.drawable.ic_theme));
            }
        } else {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("No likes data for themes", R.drawable.ic_placeholder));
        }

        // Likes Count for Photos
        List<String> likedPhotos = databaseHelper.getLikesCountForPhotos();
        if (!likedPhotos.isEmpty()) {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("Likes Count for Photos", 0)); // Section header
            for (String photo : likedPhotos) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(photo, R.drawable.ic_photo));
            }
        } else {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem("No likes data for photos", R.drawable.ic_placeholder));
        }

        return analyticsData;
    }
}
