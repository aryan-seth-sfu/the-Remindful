package com.example.theremindful2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private final Context context;
    private final List<Theme> themes;

    public ParentAdapter(Context context, List<Theme> themes) {
        this.context = context;
        this.themes = themes;  // Now the themes are loaded dynamically from the storage
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parent_pager, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        Theme theme = themes.get(position);
        holder.bind(theme);
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        private final ViewPager2 childViewPager;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            childViewPager = itemView.findViewById(R.id.childViewPager);
        }

        public void bind(Theme theme) {
            // Pass theme name to the ChildAdapter along with photos
            childViewPager.setAdapter(new ChildAdapter(theme.getPhotos(), theme.getName()));
            childViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }
    }
}
