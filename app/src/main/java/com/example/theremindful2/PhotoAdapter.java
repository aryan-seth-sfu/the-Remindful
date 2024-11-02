package com.example.theremindful2;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theremindful2.databinding.ItemPhotoBinding;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> photos;

    public PhotoAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoBinding binding = ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PhotoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private final ItemPhotoBinding binding;

        public PhotoViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Photo photo) {
            binding.imageView.setImageResource(photo.getResourceId());
            binding.textViewTheme.setText(photo.getTheme());
        }
    }
}