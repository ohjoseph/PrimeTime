package com.practice.android.primetime;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.Date;

/**
 * Created by Joseph on 6/21/16.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_POSITION_TODAY = 0;
    private static final int TAB_POSITION_GRAPHS = 1;
    private static final int TAB_POSITION_PIE_CHART = 2;
    private static final int TAB_POSITION_HISTORY = 3;

    private TabLayout mTabLayout;
    private Context mContext;

    public TabPagerAdapter(FragmentManager manager, TabLayout tabLayout, Context context) {
        super(manager);
        mTabLayout = tabLayout;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_POSITION_TODAY:
                String dateString = SharedPreferences.formatDate(mContext, new Date());
                return TimeListFragment.newInstance(dateString);
            case TAB_POSITION_GRAPHS:
                return new SampleFragment();
            case TAB_POSITION_PIE_CHART:
                return new SampleFragment();
            case TAB_POSITION_HISTORY:
                return new DayListFragment();
            default:
                return new SampleFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public void updateTabs() {
        mTabLayout.getTabAt(TAB_POSITION_TODAY).setIcon(R.drawable.tab_home);
        mTabLayout.getTabAt(TAB_POSITION_GRAPHS).setIcon(R.drawable.tab_graph);
        mTabLayout.getTabAt(TAB_POSITION_PIE_CHART).setIcon(R.drawable.tab_pie_chart);
        mTabLayout.getTabAt(TAB_POSITION_HISTORY).setIcon(R.drawable.tab_history);
    }
}
