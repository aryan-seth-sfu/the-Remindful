package com.example.theremindful2;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private AnalyticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.analyticsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display analytics
        List<String> analyticsData = getAnalyticsData();
        if (analyticsData.isEmpty()) {
            Toast.makeText(this, "No analytics data available", Toast.LENGTH_SHORT).show();
        }

        adapter = new AnalyticsAdapter(analyticsData);
        recyclerView.setAdapter(adapter);
    }

    // Fetch analytics data
    private List<String> getAnalyticsData() {
        List<String> analyticsData = new ArrayList<>();

        // Get most viewed themes
        analyticsData.add("Most Viewed Themes:");
        analyticsData.addAll(databaseHelper.getMostViewedThemes());

        // Get most viewed photos
        analyticsData.add("Most Viewed Photos:");
        analyticsData.addAll(databaseHelper.getMostViewedPhotos());

        return analyticsData;
    }
}
