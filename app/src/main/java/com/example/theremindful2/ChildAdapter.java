package com.example.theremindful2;

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

    public ChildAdapter(List<String> photos, String themeName) {
        this.photos = photos;
        this.themeName = themeName;
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

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTheme = itemView.findViewById(R.id.textViewTheme);
        }

        public void bind(String photoFilePath, String themeName) {
            // Convert the file path to a Uri
            File imageFile = new File(photoFilePath);
            if (imageFile.exists()) {
                Uri imageUri = Uri.fromFile(imageFile); // Create Uri from file path
                imageView.setImageURI(imageUri);       // Set the image using the Uri
            } else {
                Log.e("bind", "Image file not found: " + photoFilePath);
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // Fallback to a placeholder image
            }

            // Set the theme name in the text view
            textViewTheme.setText(themeName);
        }

    }
}