package com.example.lifesync;

import android.content.DialogInterface;
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

public class AddActivityModalFragment extends BottomSheetDialogFragment  {

    final int todayDayOfWeek;

    public AddActivityModalFragment(int todayDayOfWeek) {
        this.todayDayOfWeek = todayDayOfWeek;
    }

    public interface OnAddActivityListener {
        void onActivityAdded(String activityName);
    }
    private OnAddActivityListener mListener;

    // Rest of your BottomSheetDialogFragment code

    // Call this method to set the listener
    public void setOnAddActivityListener(OnAddActivityListener listener) {
        mListener = listener;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // You can return a default value or handle the case where no value is selected
        if (mListener != null) {
            mListener.onActivityAdded("Default Activity Name");
        }
    }
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
        viewPager.setCurrentItem(this.todayDayOfWeek);
    }


    private static class CustomPagerAdapter extends FragmentStatePagerAdapter {
        public CustomPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // Return the appropriate Fragment for each tab position
            return AddActivityTabViewFragment.newInstance(position);
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
            return dayOfWeek[position];
        }
    }
}
