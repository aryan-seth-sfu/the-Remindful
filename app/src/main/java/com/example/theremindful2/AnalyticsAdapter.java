package com.example.theremindful2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.ViewHolder> {

    private final List<String> analyticsData; // Data source for the RecyclerView

    // Constructor
    public AnalyticsAdapter(List<String> analyticsData) {
        this.analyticsData = analyticsData;
    }

    // ViewHolder: Represents a single item view in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView analyticsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            analyticsTextView = itemView.findViewById(R.id.analyticsTextView); // Reference to the TextView
        }
    }

    // Inflate the item layout and return a new ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items (e.g., item_analytics.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analytics, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at the current position
        String data = analyticsData.get(position);
        // Set the data to the TextView
        holder.analyticsTextView.setText(data);
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return analyticsData.size();
    }
}
