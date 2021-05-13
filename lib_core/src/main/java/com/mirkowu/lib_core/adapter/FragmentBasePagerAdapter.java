package com.mirkowu.lib_core.adapter;

import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mirkowu.lib_core.fragment.BaseMVMFragment;


/**
 * FragmentStatePagerAdapter 基类 配合ViewPager使用 简单快捷
 */
public class FragmentBasePagerAdapter extends FragmentStatePagerAdapter {
    private Fragment[] fragments;
    private String[] titles;

    public FragmentBasePagerAdapter(FragmentManager fm, Fragment... fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    public FragmentBasePagerAdapter(FragmentManager fm, Class<? extends Fragment> cls, String[] titles) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (titles == null || titles.length == 0) return;
        this.titles = titles;
        this.fragments = new Fragment[titles.length];
        for (int i = 0; i < titles.length; i++) {
            try {
                fragments[i] = cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments[position];
        if (fragment instanceof BaseMVMFragment) {
            ((BaseMVMFragment) fragment).setPosition(position);
            CharSequence pageTitle = getPageTitle(position);
            if (!TextUtils.isEmpty(pageTitle)) {
                ((BaseMVMFragment) fragment).setTitle(pageTitle.toString());
            }
        }

        return fragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.length) {
            return titles[position];
        }
        return super.getPageTitle(position);
    }

    public void setTitle(String[] orderMenu) {
        this.titles = orderMenu;
    }

}