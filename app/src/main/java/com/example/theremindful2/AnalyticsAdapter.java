package com.example.theremindful2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.io.File;


public class AnalyticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View type constants
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_PHOTO_LIKES = 2;

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

    // ViewHolder for Photo with Likes
    public static class PhotoLikesViewHolder extends RecyclerView.ViewHolder {
        public final ImageView photoThumbnail;
        public final TextView photoLikesCount;

        public PhotoLikesViewHolder(View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
            photoLikesCount = itemView.findViewById(R.id.photoLikesCount);
        }
    }

    @Override
    public int getItemViewType(int position) {
        AnalyticsItem item = analyticsData.get(position);

        if (item.getImageResId() == 0 && item.getFilePath() == null) {
            return VIEW_TYPE_HEADER; // Header type
        } else if (item.getFilePath() != null) {
            return VIEW_TYPE_PHOTO_LIKES; // Photo with likes type
        } else {
            return VIEW_TYPE_ITEM; // Default item type
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_analytics_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_PHOTO_LIKES) {
            View view = inflater.inflate(R.layout.item_photo_likes, parent, false);
            return new PhotoLikesViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_analytics, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AnalyticsItem item = analyticsData.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).sectionHeaderTextView.setText(item.getDescription());
        } else if (holder instanceof ItemViewHolder) {
            bindItemViewHolder((ItemViewHolder) holder, item);
        } else if (holder instanceof PhotoLikesViewHolder) {
            bindPhotoLikesViewHolder((PhotoLikesViewHolder) holder, item);
        }
    }

    private void bindItemViewHolder(ItemViewHolder holder, AnalyticsItem item) {
        holder.analyticsTextView.setText(item.getDescription());
        if (item.getImageResId() != 0) {
            // Load drawable resource into ImageView
            holder.analyticsImageView.setImageResource(item.getImageResId());
            holder.analyticsImageView.setVisibility(View.VISIBLE);
        } else {
            holder.analyticsImageView.setVisibility(View.GONE);
        }
    }

    private void bindPhotoLikesViewHolder(PhotoLikesViewHolder holder, AnalyticsItem item) {
        File imageFile = new File(item.getFilePath());
        if (imageFile.exists()) {
            // Convert file path to URI and load using Glide
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.ic_placeholder) // Placeholder image while loading
                    .into(holder.photoThumbnail);
        } else {
            // Log error and show placeholder if file does not exist
            Log.e("AnalyticsAdapter", "File does not exist: " + item.getFilePath());
            holder.photoThumbnail.setImageResource(R.drawable.ic_placeholder);
        }
        holder.photoLikesCount.setText(item.getDescription());
    }


    @Override
    public int getItemCount() {
        return analyticsData.size();
    }

    // AnalyticsItem: Custom data class to handle file paths, resource IDs, and descriptions
    public static class AnalyticsItem {
        private final String description; // Text description or likes count
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
