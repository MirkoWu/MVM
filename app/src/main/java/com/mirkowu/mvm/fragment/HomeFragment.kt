package com.mirkowu.mvm.fragment

import android.os.Bundle
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseFragment
import com.mirkowu.mvm.mvvm.MVVMMediator

class HomeFragment : BaseFragment<MVVMMediator>() {
    fun newInstance(): HomeFragment{
        val args = Bundle()

        val fragment = HomeFragment()
        fragment.arguments = args
        return fragment
    }
    override fun getLayoutId()= R.layout.fragment_home

    override fun initialize() {

    }
}