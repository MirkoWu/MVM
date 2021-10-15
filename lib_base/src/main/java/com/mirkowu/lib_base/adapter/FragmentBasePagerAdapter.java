package com.mirkowu.lib_base.adapter;

import android.os.Parcelable;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mirkowu.lib_base.fragment.BaseMVMFragment;
import com.mirkowu.lib_util.LogUtil;


/**
 * FragmentStatePagerAdapter 基类 配合ViewPager使用 简单快捷
 * 已启用，请使用{@link BaseFragmentPagerAdapter}代替
 */
@Deprecated
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
            } catch (Throwable e) {
            }
        }
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
        Fragment fragment = fragments[position];
        if (fragment instanceof BaseMVMFragment) {
            CharSequence pageTitle = getPageTitle(position);
            ((BaseMVMFragment) fragment).setTitle(pageTitle.toString());
            ((BaseMVMFragment) fragment).setPosition(position);
        }

        return fragment;
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

    public void setTitle(String[] titles) {
        this.titles = titles;
    }

}