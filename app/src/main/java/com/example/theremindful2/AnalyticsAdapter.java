package com.example.theremindful2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.ViewHolder> {

    private final List<AnalyticsItem> analyticsData; // Data source for the RecyclerView
    private final Context context;

    // Constructor
    public AnalyticsAdapter(List<AnalyticsItem> analyticsData, Context context) {
        this.analyticsData = analyticsData;
        this.context = context;
    }

    // ViewHolder: Represents a single item view in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView analyticsTextView;
        public final ImageView analyticsImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            analyticsTextView = itemView.findViewById(R.id.analyticsTextView); // Reference to the TextView
            analyticsImageView = itemView.findViewById(R.id.analyticsImageView); // Reference to the ImageView
        }
    }

    // Inflate the item layout and return a new ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items (e.g., item_analytics.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analytics, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at the current position
        AnalyticsItem item = analyticsData.get(position);

        // Set description text
        holder.analyticsTextView.setText(item.getDescription());

        // Handle either file paths or drawable resources for images
        if (item.getImageResId() != 0) {
            // If a drawable resource is set
            holder.analyticsImageView.setImageResource(item.getImageResId());
            holder.analyticsImageView.setVisibility(View.VISIBLE);
        } else if (item.getFilePath() != null) {
            // If a file path is set
            File imageFile = new File(item.getFilePath());
            if (imageFile.exists()) {
                Uri imageUri = Uri.fromFile(imageFile);
                holder.analyticsImageView.setImageURI(imageUri);
                holder.analyticsImageView.setVisibility(View.VISIBLE);
            } else {
                holder.analyticsImageView.setImageResource(R.drawable.ic_placeholder); // Fallback placeholder
                holder.analyticsImageView.setVisibility(View.VISIBLE);
            }
        } else {
            // Hide the ImageView if no image is available
            holder.analyticsImageView.setVisibility(View.GONE);
        }
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return analyticsData.size();
    }

    // AnalyticsItem: Custom data class to handle both file paths and resource IDs
    public static class AnalyticsItem {
        private final String description;
        private final String filePath; // For image file paths
        private final int imageResId; // For drawable resources

        // Constructor for file paths
        public AnalyticsItem(String description, String filePath) {
            this.description = description;
            this.filePath = filePath;
            this.imageResId = 0; // No drawable resource
        }

        // Constructor for drawable resources
        public AnalyticsItem(String description, int imageResId) {
            this.description = description;
            this.filePath = null; // No file path
            this.imageResId = imageResId;
        }

        public String getDescription() {
            return description;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getImageResId() {
            return imageResId;
        }
    }
}
