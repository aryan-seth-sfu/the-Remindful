package com.example.theremindful2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragmentAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public AnalyticsFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setFragments(Fragment mostViewedFragment, Fragment likesFragment) {
        fragmentList.clear();
        fragmentList.add(mostViewedFragment);
        fragmentList.add(likesFragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
