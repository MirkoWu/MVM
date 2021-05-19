//package com.mirkowu.lib_mvm.base;
//
//
//import com.mirkowu.lib_mvm.core.BaseMVMFragment;
//import com.mirkowu.lib_mvm.core.BaseMediator;
//
///**
// * 懒加载Fragment
// */
//public abstract class BaseLazyFragment<M extends BaseMediator> extends BaseMVMFragment<M> {
//
//    protected boolean isFirstLoad = true;
//    private int position;
//    private String title;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isFirstLoad) {
//            isFirstLoad = false;
//            onLazyLoad();
//        }
//    }
//
//    protected abstract void onLazyLoad();
//
//
//    public void setPosition(int position) {
//        this.position = position;
//    }
//
//    public int getPosition() {
//        return position;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//}
