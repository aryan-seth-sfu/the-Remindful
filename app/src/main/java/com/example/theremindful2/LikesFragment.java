package com.example.theremindful2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private static final String ARG_ANALYTICS_DATA = "analyticsData";
    private List<AnalyticsAdapter.AnalyticsItem> analyticsData;

    public static LikesFragment newInstance(ArrayList<AnalyticsAdapter.AnalyticsItem> data) {
        LikesFragment fragment = new LikesFragment();
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
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.likesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (analyticsData != null) {
            AnalyticsAdapter adapter = new AnalyticsAdapter(analyticsData, getContext());
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
