package com.example.theremindful2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Objects;

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

            textDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageView.setVisibility(View.VISIBLE);
                    textDescription.setVisibility(View.GONE);
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String description = MetadataUtils.getDescriptionForImage((String) imageView.getTag(),context);
                    Log.d("getTag", (String) imageView.getTag());
                    Log.d("context", String.valueOf(context));
                    Log.d("description", description != null ? description : "No description found");

                    // Make the ImageView invisible
                    imageView.setVisibility(View.GONE);
                    textDescription.setVisibility(View.VISIBLE);
                    textDescription.setText(description);
                    textDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                }
            });
        }

        public void bind(String photoFilePath, String themeName) {
            // Convert the file path to a Uri
            File imageFile = new File(photoFilePath);
            if (imageFile.exists() && imageFile.isFile()) {
                // If the file exists, load it
                imageView.setTag(photoFilePath);
                Uri imageUri = Uri.fromFile(imageFile); // Create Uri from file path
                imageView.setImageURI(imageUri);         // Set the image using the Uri
            } else {
                // If the image file doesn't exist, set a placeholder
                Log.e("bind", "Image file not found: " + photoFilePath);
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // Fallback to a placeholder image
            }

            // Set the theme name in the text view
            textViewTheme.setText(themeName);
        }


    }
    public Context getContext(){
        return context;
    }
}