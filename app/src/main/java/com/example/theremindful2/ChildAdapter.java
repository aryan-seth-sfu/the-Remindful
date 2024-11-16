package com.example.theremindful2;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private final List<Integer> photos;
    private final String themeName;

    public ChildAdapter(List<Integer> photos, String themeName) {
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

        public void bind(int photoResId, String themeName) {
            Uri imageUri = Uri.parse("android.resource://com.example.theremindful2/" + photoResId);
            imageView.setImageURI(imageUri);
            textViewTheme.setText(themeName);
        }
    }
}