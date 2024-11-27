package com.example.theremindful2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Random;
import android.graphics.Color;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private DatabaseHelper databaseHelper;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AnalyticsFragmentAdapter pagerAdapter;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Initialize UI components
        viewPager = findViewById(R.id.analyticsViewPager);
        tabLayout = findViewById(R.id.analyticsTabLayout);
        barChart = findViewById(R.id.barChart);

        // Set up ViewPager and TabLayout
        pagerAdapter = new AnalyticsFragmentAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Most Viewed");
            else tab.setText("Likes");
        }).attach();

        // Check permissions and load data
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                loadAnalyticsData();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                loadAnalyticsData();
            }
        } else {
            loadAnalyticsData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAnalyticsData();
        } else {
            Toast.makeText(this, "Permission denied. Unable to load images.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAnalyticsData() {
        ArrayList<AnalyticsAdapter.AnalyticsItem> mostViewedData = new ArrayList<>(getMostViewedAnalyticsData());
        ArrayList<AnalyticsAdapter.AnalyticsItem> likesData = new ArrayList<>(getLikesAnalyticsData());

        pagerAdapter.setFragments(
                MostViewedFragment.newInstance(mostViewedData),
                LikesFragment.newInstance(likesData)
        );

        setupChart();
    }

    private List<AnalyticsAdapter.AnalyticsItem> getMostViewedAnalyticsData() {
        List<AnalyticsAdapter.AnalyticsItem> analyticsData = new ArrayList<>();
        addAnalyticsSection(analyticsData, "Most Viewed Themes", databaseHelper.getMostViewedThemes(), R.drawable.ic_theme, "No themes viewed yet");
        return analyticsData;
    }

    private List<AnalyticsAdapter.AnalyticsItem> getLikesAnalyticsData() {
        List<AnalyticsAdapter.AnalyticsItem> analyticsData = new ArrayList<>();
        addAnalyticsSection(analyticsData, "Likes Count for Themes", databaseHelper.getLikesCountForThemes(), R.drawable.ic_theme, "No likes data for themes");
        addAnalyticsSection(analyticsData, "Likes Count for Photos", databaseHelper.getLikesCountForPhotos(), R.drawable.ic_photo, "No likes data for photos");
        return analyticsData;
    }

    private void addAnalyticsSection(List<AnalyticsAdapter.AnalyticsItem> analyticsData, String header, List<String> items, int drawableResId, String emptyMessage) {
        if (items != null && !items.isEmpty()) {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem(header, 0));
            for (String item : items) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(item, drawableResId));
            }
        } else {
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem(header, 0));
            analyticsData.add(new AnalyticsAdapter.AnalyticsItem(emptyMessage, R.drawable.ic_placeholder));
        }
    }

    private void setupChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> mostViewedThemes = databaseHelper.getMostViewedThemes();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < mostViewedThemes.size(); i++) {
            String[] split = mostViewedThemes.get(i).split(" \\(");
            String themeName = split[0];
            int count = Integer.parseInt(split[1].replace(" views)", ""));
            entries.add(new BarEntry(i, count));
            labels.add(themeName);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Most Viewed Themes");

        // Dynamically generate colors for the bars
        List<Integer> dynamicColors = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            dynamicColors.add(getRandomColor());
        }
        dataSet.setColors(dynamicColors); // Apply generated colors
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Configure the X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Set theme names as labels
        xAxis.setGranularity(1f); // Ensure one label per bar
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);

        // Disable the right Y-axis
        barChart.getAxisRight().setEnabled(false);

        // Configure the left Y-axis
        barChart.getAxisLeft().setTextSize(12f);

        // Disable legend and description (optional)
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Animate the chart
        barChart.animateY(1000);

        // Refresh the chart
        barChart.invalidate();
    }

    private int getRandomColor() {
        // Generate a random color
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

}
