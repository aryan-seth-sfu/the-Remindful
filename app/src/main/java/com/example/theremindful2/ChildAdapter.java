package com.example.theremindful2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;

import java.io.File;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private final List<String> photos;  // Changed from List<Integer> to List<String>
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
        holder.bind(photos.get(position), themeName);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textViewTheme;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTheme = itemView.findViewById(R.id.textViewTheme);
        }

        public void bind(String photoPath, String themeName) {
            File file = new File(photoPath);
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file));
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // Set a default placeholder image
            }
            textViewTheme.setText(themeName);
        }
    }
}
