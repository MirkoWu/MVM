package com.mirkowu.lib_base.widget;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class RefreshHelper implements OnRefreshListener, OnRefreshLoadMoreListener {

    protected SmartRefreshLayout mInnerRefreshLayout;
    private RecyclerView mInnerRecyclerView;
    private OnRefreshListener mOnRefreshListener;
    protected int FIRST_PAGE = 0;//起始页下标
    protected int mPage = FIRST_PAGE;//当前页
    protected int PAGE_COUNT = 10;//每页请求数量

    public RefreshHelper(RecyclerView mInnerRecyclerView, OnRefreshListener onRefreshListener) {
        this(null, mInnerRecyclerView, onRefreshListener);
    }

    public RefreshHelper(SmartRefreshLayout mInnerRefreshLayout, RecyclerView mInnerRecyclerView, OnRefreshListener onRefreshListener) {
        this.mInnerRefreshLayout = mInnerRefreshLayout;
        this.mInnerRecyclerView = mInnerRecyclerView;
        this.mOnRefreshListener = onRefreshListener;

        if (mInnerRefreshLayout != null) {
            mInnerRefreshLayout.setOnRefreshListener(this);
            mInnerRefreshLayout.setEnableLoadMore(false);
        }
    }

    public void setFirstPageIndex(int firstIndex) {
        FIRST_PAGE = firstIndex;
    }

    public void setPageCount(int pageCount) {
        PAGE_COUNT = pageCount;
    }


    /**
     * 必须调用 初始化
     *
     * @param refreshLayout
     */
//    public void initRefresh(SmartRefreshLayout refreshLayout ) {
//       initRefresh(refreshLayout,null);
//    }
//    public void initRefresh(SmartRefreshLayout refreshLayout, RecyclerView recyclerView) {
//        this.mInnerRefreshLayout = refreshLayout;
//        this.mInnerRecyclerView = recyclerView;
//
//        if (mInnerRefreshLayout != null) {
//            mInnerRefreshLayout.setOnRefreshListener(this);
//            mInnerRefreshLayout.setEnableLoadMore(false);
//        }
//    }

    /**
     * 当前界面只有下拉刷新 没有上拉加载功能 用这个
     *
     * @param adapter
     * @param list
     */
    public void setLoadData(BaseQuickAdapter adapter, List<?> list) {
        if (mInnerRefreshLayout != null) mInnerRefreshLayout.finishRefresh();
        adapter.setNewData(list);
    }

    /**
     * 当有 下拉刷新  和 上拉加载功能 用这个
     *
     * @param adapter
     * @param list
     */
    public void setLoadMore(BaseQuickAdapter adapter, List<?> list) {
        setLoadMore(adapter, list, list != null && list.size() >= PAGE_COUNT);
    }

    public void setLoadMore(BaseQuickAdapter adapter, List<?> list, boolean hasMore) {
        if (mInnerRefreshLayout != null) mInnerRefreshLayout.finishRefresh();

        if (mPage == FIRST_PAGE) {
            adapter.setNewData(list);

            if (mOnRefreshListener != null) {
                mOnRefreshListener.onEmptyChange(list == null || list.isEmpty());
            }
        } else {
            if (list != null && !list.isEmpty()) {
                adapter.addData(list);
            }
        }
        if (mInnerRefreshLayout != null) {
            if (hasMore) {//开启加载更多
                mInnerRefreshLayout.finishLoadMore();
                mInnerRefreshLayout.setEnableLoadMore(true);
                mInnerRefreshLayout.setOnRefreshLoadMoreListener(this);
                mInnerRefreshLayout.setEnableLoadMoreWhenContentNotFull(true);

            } else {
                mInnerRefreshLayout.finishLoadMore();
                mInnerRefreshLayout.setEnableLoadMore(false);

                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onLoadNoMore();
                }
            }
        }
    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mPage = FIRST_PAGE;
        loadData();
    }

    public void onRefresh() {
        onRefresh(mInnerRefreshLayout);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mPage++;
        loadData();
    }

    public void finishLoad() {
        if (mInnerRefreshLayout != null) {
            mInnerRefreshLayout.finishRefresh();
            mInnerRefreshLayout.finishLoadMore();
        }
    }

    public void autoRefresh() {
        if (mInnerRecyclerView != null) {
            mInnerRecyclerView.scrollToPosition(0);
        }
        if (mInnerRefreshLayout != null && !mInnerRefreshLayout.isRefreshing()) {
            mInnerRefreshLayout.autoRefresh();
        }
    }

    public void loadData() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onLoadData(mPage);
        }
    }


    public static interface OnRefreshListener {
        /**
         * 刷新 或者 加载
         *
         * @param page
         */
        void onLoadData(int page);

        /**
         * 加载结束
         */
        void onLoadNoMore();

        /**
         * 设置当数据为空的提示
         *
         * @param isEmpty
         */
        void onEmptyChange(boolean isEmpty);
    }

}
