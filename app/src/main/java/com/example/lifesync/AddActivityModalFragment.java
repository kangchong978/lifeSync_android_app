package com.example.lifesync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

public class AddActivityModalFragment extends BottomSheetDialogFragment {

    static final private String[] dayOfWeek = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_activity_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.TabLayoutAddActivityModal);
        ViewPager viewPager = view.findViewById(R.id.ViewPagerAddActivityModal);
        viewPager.setAdapter(new CustomPagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(7);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class CustomPagerAdapter extends FragmentStatePagerAdapter {
        public CustomPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // Return the appropriate Fragment for each tab position
            return AddActivityTabViewFragment.newInstance( dayOfWeek[position]);
        }

        @Override
        public int getCount() {
            // Return the total number of tabs
            return dayOfWeek.length; // Change this to the number of tabs you have
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            // Set tab titles if needed
            return   dayOfWeek[position];
        }
    }
}
