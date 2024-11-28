package com.example.theremindful2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Initialize UI components
        viewPager = findViewById(R.id.analyticsViewPager);
        tabLayout = findViewById(R.id.analyticsTabLayout);

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
    }

    private List<AnalyticsAdapter.AnalyticsItem> getMostViewedAnalyticsData() {
        List<AnalyticsAdapter.AnalyticsItem> analyticsData = new ArrayList<>();
        addAnalyticsSection(analyticsData, "Most Viewed Themes", databaseHelper.getMostViewedThemes(), R.drawable.ic_theme, "No themes viewed yet");
        return analyticsData;
    }

    private List<AnalyticsAdapter.AnalyticsItem> getLikesAnalyticsData() {
        List<AnalyticsAdapter.AnalyticsItem> analyticsData = new ArrayList<>();
        addAnalyticsSection(analyticsData, "Likes Count for Themes", databaseHelper.getLikesCountForThemes(), R.drawable.ic_theme, "No likes data for themes");

        // Log the photo likes data
        List<String> photoLikes = databaseHelper.getLikesCountForPhotos();
        Log.d("AnalyticsActivity", "Photo Likes Data: " + photoLikes);

        addAnalyticsSection(analyticsData, "Likes Count for Photos", photoLikes, R.drawable.ic_photo, "No likes data for photos");
        return analyticsData;
    }

    private void addAnalyticsSection(List<AnalyticsAdapter.AnalyticsItem> analyticsData, String header, List<String> items, int drawableResId, String emptyMessage) {
        if (header.equals("Likes Count for Photos")) {
            if (items != null && !items.isEmpty()) {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(header, 0));
                for (String item : items) {
                    // Split item by the space before the parentheses
                    int indexOfBracket = item.lastIndexOf(" (");
                    if (indexOfBracket != -1) {
                        String photoPath = item.substring(0, indexOfBracket).trim();
                        String likesCount = item.substring(indexOfBracket + 2, item.length() - 1).replace(" likes", "").trim();

                        // Add the parsed photo-like item
                        analyticsData.add(new AnalyticsAdapter.AnalyticsItem(likesCount + " likes", photoPath));
                    } else {
                        Log.w("AnalyticsActivity", "Invalid photo-like item format: " + item);
                    }
                }
            } else {
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(header, 0));
                analyticsData.add(new AnalyticsAdapter.AnalyticsItem(emptyMessage, R.drawable.ic_placeholder));
            }
        } else {
            // Existing logic for other sections
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
    }
}
