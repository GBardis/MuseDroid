package com.example.musedroid.musedroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static Fragment1 fragment1;
    public static Fragment2 fragment2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Fragment1();
        } else if (position == 1) {
            return new Fragment2();
        } else if (position == 2) {
            return new Fragment3();
        } else return new Fragment();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        switch (position) {
            case 0:
                fragment1 = (Fragment1) createdFragment;
                break;
            case 1:
                fragment2 = (Fragment2) createdFragment;
                break;
        }
        return createdFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
