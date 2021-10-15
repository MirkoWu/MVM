package com.mirkowu.lib_base.adapter;

import android.os.Parcelable;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mirkowu.lib_base.fragment.BaseMVMFragment;
import com.mirkowu.lib_util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * BaseFragmentPagerAdapter 基类 配合ViewPager使用 简单快捷
 */
public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<String> titles;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (titles == null) {
            titles = new ArrayList<>();
        }
        this.titles = titles;
        this.fragments = fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        this.fragments = fragments;
    }

    public void setTitles(List<String> titles) {
        if (titles == null) {
            titles = new ArrayList<>();
        }
        this.titles = titles;
    }

    public void clear() {
        fragments = null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Throwable e) {
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment instanceof BaseMVMFragment) {
            CharSequence pageTitle = getPageTitle(position);
            ((BaseMVMFragment) fragment).setTitle(pageTitle);
            ((BaseMVMFragment) fragment).setPosition(position);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.size()) {
            return titles.get(position);
        }
        return super.getPageTitle(position); //标题长度不够时，返回null
    }


}