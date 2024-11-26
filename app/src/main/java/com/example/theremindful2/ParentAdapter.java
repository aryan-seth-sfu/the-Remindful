package com.example.theremindful2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private final Context context;
    private final List<Theme> themes;

    public ParentAdapter(Context context) {
        this.context = context;
        this.themes = MetadataUtils.loadThemesFromStorage(context);
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parent_pager, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        if (themes.isEmpty()) {
            // Handle the case when themes list is empty
            Toast.makeText(context, "No themes available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use modulo operator to simulate infinite scrolling
        int actualPosition = position % themes.size();
        Theme theme = themes.get(actualPosition);
        holder.bind(theme);
    }

    @Override
    public int getItemCount() {
        return themes.isEmpty() ? 0 : Integer.MAX_VALUE; // Infinite scrolling if themes exist
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        private final ViewPager2 childViewPager;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            childViewPager = itemView.findViewById(R.id.childViewPager);
        }

        public void bind(Theme theme) {
            List<String> photos = theme.getPhotos();
            if (photos == null || photos.isEmpty()) {
                // Handle empty photo list
                Toast.makeText(context, "No photos available for this theme", Toast.LENGTH_SHORT).show();
                childViewPager.setAdapter(null); // Avoid crashes by setting no adapter
                return;
            }

            // Pass theme name to the ChildAdapter along with photos
            childViewPager.setAdapter(new ChildAdapter(photos, theme.getName(),context));
            childViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

            // Start at a middle position to enable infinite scrolling
            int startPosition = Integer.MAX_VALUE / 2;
            childViewPager.setCurrentItem(startPosition - (startPosition % photos.size()), false);
        }
    }
}
