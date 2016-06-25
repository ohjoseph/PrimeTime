package com.practice.android.primetime;

import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private NoSwipeViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.activity_tab_layout);

        // Create new PagerAdapter
        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),
                mTabLayout, MainActivity.this);

        // Set up ViewPager
        mViewPager = (NoSwipeViewPager) findViewById(R.id.no_swipe_viewPager);
        mViewPager.setPageTransformer(false, mViewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(final int position) {
                // Update the UI of fragment
                UpdateFragmentUI fragment =
                        (UpdateFragmentUI) mPagerAdapter.instantiateItem(mViewPager, position);
                fragment.updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });

        // Finish setting up TabLayout
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);

        // Add tab icons
        mPagerAdapter.updateTabs();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Force reconstruction of TimeLab next time
        TimeLab.nullifyInstance();
    }
}
