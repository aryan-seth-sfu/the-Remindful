package com.example.theremindful2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final String TAG = "AnalyticsAdapter";

    private final List<AnalyticsItem> analyticsData; // Data source for the RecyclerView
    private final Context context;

    // Constructor
    public AnalyticsAdapter(List<AnalyticsItem> analyticsData, Context context) {
        this.analyticsData = analyticsData;
        this.context = context;
    }

    // ViewHolder for Header
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView sectionHeaderTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            sectionHeaderTextView = itemView.findViewById(R.id.sectionHeaderTextView);
        }
    }

    // ViewHolder for Item
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView analyticsTextView;
        public final ImageView analyticsImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            analyticsTextView = itemView.findViewById(R.id.analyticsTextView);
            analyticsImageView = itemView.findViewById(R.id.analyticsImageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        AnalyticsItem item = analyticsData.get(position);
        return item.getImageResId() == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_analytics_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_analytics, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AnalyticsItem item = analyticsData.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).sectionHeaderTextView.setText(item.getDescription());
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.analyticsTextView.setText(item.getDescription());

            if (item.getImageResId() != 0) {
                // If a drawable resource is set
                itemHolder.analyticsImageView.setImageResource(item.getImageResId());
                itemHolder.analyticsImageView.setVisibility(View.VISIBLE);
            } else {
                // Hide the ImageView if no image is available
                itemHolder.analyticsImageView.setVisibility(View.GONE);
            }
        }
    }

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
