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
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private final List<String> photos;
    private final String themeName;
    private final Context context;

    public ChildAdapter(List<String> photos, String themeName, Context context) {
        this.photos = photos;
        this.themeName = themeName;
        this.context = context;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        if (photos == null || photos.isEmpty()) {
            Log.e("ChildAdapter", "No photos available to display.");
            return;
        }
        int actualPosition = position % photos.size();
        holder.bind(photos.get(actualPosition), themeName);
    }

    @Override
    public int getItemCount() {
        return (photos == null || photos.isEmpty()) ? 0 : Integer.MAX_VALUE;
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textViewTheme;
        private final TextView textDescription;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTheme = itemView.findViewById(R.id.textViewTheme);
            textDescription = itemView.findViewById(R.id.textDescription);

            textDescription.setOnClickListener(view -> {
                imageView.setVisibility(View.VISIBLE);
                textDescription.setVisibility(View.GONE);
            });

            imageView.setOnClickListener(view -> {
                String description = MetadataUtils.getDescriptionForImage((String) imageView.getTag(), context);
                Log.d("getTag", (String) imageView.getTag());
                Log.d("context", String.valueOf(context));
                Log.d("description", description != null ? description : "No description found");

                imageView.setVisibility(View.GONE);
                textDescription.setVisibility(View.VISIBLE);
                textDescription.setText(description);
                textDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // Log photo interaction
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                long mediaId = dbHelper.addMediaItem((String) imageView.getTag(), description, "image");
                dbHelper.logPhotoInteraction(mediaId);
            });
        }

        public void bind(String photoFilePath, String themeName) {
            File imageFile = new File(photoFilePath);
            if (imageFile.exists() && imageFile.isFile()) {
                imageView.setTag(photoFilePath);
                Uri imageUri = Uri.fromFile(imageFile);
                imageView.setImageURI(imageUri);
            } else {
                Log.e("bind", "Image file not found: " + photoFilePath);
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // Fallback to a placeholder image
            }

            textViewTheme.setText(themeName);
        }
    }
}
