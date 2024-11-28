package com.example.theremindful2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MostViewedFragment extends Fragment {

    private static final String ARG_ANALYTICS_DATA = "analyticsData";
    private List<AnalyticsAdapter.AnalyticsItem> analyticsData;
    private BarChart barChartMostViewed;

    public static MostViewedFragment newInstance(ArrayList<AnalyticsAdapter.AnalyticsItem> data) {
        MostViewedFragment fragment = new MostViewedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ANALYTICS_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            analyticsData = (List<AnalyticsAdapter.AnalyticsItem>) getArguments().getSerializable(ARG_ANALYTICS_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_most_viewed, container, false);

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.mostViewedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (analyticsData != null) {
            AnalyticsAdapter adapter = new AnalyticsAdapter(analyticsData, getContext());
            recyclerView.setAdapter(adapter);
        }

        // BarChart setup
        barChartMostViewed = view.findViewById(R.id.barChartMostViewed);
        setupMostViewedChart();

        return view;
    }

    private void setupMostViewedChart() {
        // Replace this with actual data retrieval logic from the database
        List<String> mostViewedThemes = DatabaseHelper.getInstance(getContext()).getMostViewedThemes();
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int othersCount = 0;
        int maxEntries = 4;

        for (int i = 0; i < mostViewedThemes.size(); i++) {
            String[] split = mostViewedThemes.get(i).split(" \\(");
            String themeName = split[0];
            int count = Integer.parseInt(split[1].replace(" views)", ""));

            if (i < maxEntries) {
                entries.add(new BarEntry(i, count));
                labels.add(themeName);
            } else {
                othersCount += count;
            }
        }

        if (othersCount > 0) {
            entries.add(new BarEntry(maxEntries, othersCount));
            labels.add("Others");
        }

        BarDataSet dataSet = new BarDataSet(entries, "Most Viewed Themes");
        dataSet.setColors(getDynamicColors(labels.size()));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartMostViewed.setData(barData);

        XAxis xAxis = barChartMostViewed.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);

        barChartMostViewed.getAxisRight().setEnabled(false);
        barChartMostViewed.getAxisLeft().setTextSize(12f);
        barChartMostViewed.getDescription().setEnabled(false);
        barChartMostViewed.getLegend().setEnabled(false);
        barChartMostViewed.animateY(1000);
        barChartMostViewed.invalidate();
    }

    private List<Integer> getDynamicColors(int count) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            colors.add(android.graphics.Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }
}
