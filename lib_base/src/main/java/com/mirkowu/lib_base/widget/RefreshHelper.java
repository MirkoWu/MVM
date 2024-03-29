package com.mirkowu.lib_base.widget;

import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class RefreshHelper implements OnRefreshListener, OnRefreshLoadMoreListener {

    protected SmartRefreshLayout mRefreshLayout;
    private ViewGroup mInnerContainer;
    private OnRefreshListener mOnRefreshListener;
    /*** 每页请求数量*/
    private int PAGE_COUNT = 10;
    /*** 起始页下标*/
    private int FIRST_PAGE = 0;
    /*** 当前页*/
    protected int mCurPage = FIRST_PAGE;

    public RefreshHelper(@NonNull RecyclerView recyclerView, OnRefreshListener onRefreshListener) {
        this(null, recyclerView, onRefreshListener);
    }

    public RefreshHelper(SmartRefreshLayout refreshLayout, @NonNull RecyclerView recyclerView, OnRefreshListener onRefreshListener) {
        this(refreshLayout, (ViewGroup) recyclerView, onRefreshListener);
    }

    public RefreshHelper(SmartRefreshLayout refreshLayout, @NonNull ViewGroup innerContainer, OnRefreshListener onRefreshListener) {
        this.mRefreshLayout = refreshLayout;
        this.mInnerContainer = innerContainer;
        this.mOnRefreshListener = onRefreshListener;

        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setEnableLoadMore(false);
        }
    }

    /**
     * 设置起始页下标
     *
     * @param firstIndex 从第几页开始 eg.从第1页开始
     */
    public RefreshHelper setFirstPageIndex(int firstIndex) {
        FIRST_PAGE = firstIndex;
        return this;
    }

    public int getFirstPageIndex() {
        return FIRST_PAGE;
    }

    public int getCurPage() {
        return mCurPage;
    }

    public void setCurPage(int curPage) {
        mCurPage = curPage;
    }

    public boolean isFirstPage() {
        return mCurPage == FIRST_PAGE;
    }

    /**
     * 设置每页加载多少条数据
     *
     * @param pageCount
     */
    public RefreshHelper setPageCount(int pageCount) {
        PAGE_COUNT = pageCount;
        return this;
    }

    public int getPageCount() {
        return PAGE_COUNT;
    }

    /**
     * 当前界面只有下拉刷新 没有上拉加载功能 用这个
     *
     * @param adapter
     * @param list
     */
    public void setLoadData(BaseRVAdapter adapter, List<?> list) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
        adapter.setData(list);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onEmptyChange(list == null || list.isEmpty());
        }
    }

    /**
     * 当有 下拉刷新  和 上拉加载功能 用这个
     *
     * @param adapter
     * @param list
     */
    public void setLoadMore(BaseRVAdapter adapter, List<?> list) {
        setLoadMore(adapter, list, list != null && list.size() >= PAGE_COUNT);
    }

    public void setLoadMore(BaseRVAdapter adapter, List<?> list, boolean hasMore) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }

        if (mCurPage == FIRST_PAGE) {
            adapter.setData(list);

            if (mOnRefreshListener != null) {
                mOnRefreshListener.onEmptyChange(list == null || list.isEmpty());
            }
        } else {
            if (list != null && !list.isEmpty()) {
                adapter.addData(list);
            }
        }
        if (mRefreshLayout != null) {
            if (hasMore) {
                //开启加载更多
                mRefreshLayout.finishLoadMore();
                mRefreshLayout.setEnableLoadMore(true);
                mRefreshLayout.setOnRefreshLoadMoreListener(this);
                mRefreshLayout.setEnableLoadMoreWhenContentNotFull(true);

            } else {
                mRefreshLayout.finishLoadMore();
                mRefreshLayout.setEnableLoadMore(false);

                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onLoadNoMore();
                }
            }
        }
    }

    /**
     * 加载数据，用于首页
     */
    public void refresh() {
        onRefresh(mRefreshLayout);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mCurPage = FIRST_PAGE;
        loadData();
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mCurPage++;
        loadData();
    }

    /**
     * 加载数据用于 刷新当前页
     */
    public void loadData() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onLoadData(mCurPage);
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (mInnerContainer != null) {
            if (mInnerContainer instanceof RecyclerView) {
                ((RecyclerView) mInnerContainer).scrollToPosition(0);
            } else if (mInnerContainer instanceof ScrollView) {
                mInnerContainer.scrollTo(0, 0);
            }
        }
        if (mRefreshLayout != null && !mRefreshLayout.isRefreshing()) {
            mRefreshLayout.autoRefresh(0);
        }
    }

    /**
     * 结束加载 包括下拉刷新和上拉加载
     */
    public void finishLoad() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
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
