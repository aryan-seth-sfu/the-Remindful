package com.example.theremindful2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;

// First, we'll create the Theme class (which was likely a data class in Kotlin)
// Now the ParentAdapter
public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private final Context context;
    private final List<Theme> themes;

    public ParentAdapter(Context context) {
        this.context = context;
        this.themes = Arrays.asList(
                new Theme("Sunset", Arrays.asList(R.drawable.sunset1, R.drawable.sunset2)),
                new Theme("Fishing", Arrays.asList(R.drawable.fishing1, R.drawable.fishing2)),
                new Theme("Mountains", Arrays.asList(R.drawable.mountain1, R.drawable.mountain2)),
                new Theme("Beach", Arrays.asList(R.drawable.beach1, R.drawable.beach2, R.drawable.beach3, R.drawable.beach4))
        );
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