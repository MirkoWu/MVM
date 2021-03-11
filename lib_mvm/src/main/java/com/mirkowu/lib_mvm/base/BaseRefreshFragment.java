package com.mirkowu.lib_mvm.base;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mirkowu.lib_mvm.core.BaseMVMFragment;
import com.mirkowu.lib_mvm.core.BaseMediator;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public abstract class BaseRefreshFragment<M extends BaseMediator> extends BaseMVMFragment<M>
        implements OnRefreshListener, OnRefreshLoadMoreListener {

    protected SmartRefreshLayout mInnerRefreshLayout;
    protected RecyclerView mInnerRecyclerView;
    protected int mPage = 1;
    protected int PAGE_COUNT = 10;

    /**
     * 必须调用 初始化
     *
     * @param refreshLayout
     */
    public void initRefresh(SmartRefreshLayout refreshLayout/*, RecyclerView recyclerView*/) {
        initRefresh(refreshLayout, null);
    }

    public void initRefresh(SmartRefreshLayout refreshLayout, RecyclerView recyclerView) {
        this.mInnerRefreshLayout = refreshLayout;
        this.mInnerRecyclerView = recyclerView;
        if (mInnerRefreshLayout != null) {
            mInnerRefreshLayout.setOnRefreshListener(this);
            mInnerRefreshLayout.setEnableLoadMore(false);
        }
    }

    /**
     * 当前界面只有下拉刷新 没有上拉加载功能 用这个
     *
     * @param adapter
     * @param list
     */
    public void setLoadData(BaseQuickAdapter adapter, List<?> list) {
        if (mInnerRefreshLayout != null) mInnerRefreshLayout.finishRefresh();
        adapter.setNewData(list);
        setEmptyView(list == null || list.isEmpty(), null);
//        if(list != null && list.size()>0){
//            View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_unline_unload_view, null);
//            adapter.setFooterView(view);
//            adapter.notifyDataSetChanged();
//        }else {
//            adapter.removeAllFooterView();
//        }
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

        if (mPage == 1) {
            adapter.setNewData(list);
            setEmptyView(list == null || list.isEmpty(), null);
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
                //  mInnerRefreshLayout.finishLoadMoreWithNoMoreData();
            }
//            if(!hasMore){
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_unline_unload_view, null);
//                adapter.setFooterView(view);
//                adapter.notifyDataSetChanged();
//            }else {
//                adapter.removeAllFooterView();
//            }
        }
    }

    public void finishLoad() {
        if (mInnerRefreshLayout != null) {
            mInnerRefreshLayout.finishRefresh();
            mInnerRefreshLayout.finishLoadMore();
        }
    }

    public void onRefresh() {
        onRefresh(mInnerRefreshLayout);
    }

    @Override
    public void onRefresh(@Nullable RefreshLayout refreshLayout) {
        mPage = 1;
        loadData();
    }


    public void autoRefresh() {
        if (mInnerRecyclerView != null) {
            mInnerRecyclerView.scrollToPosition(0);
        }
        if (mInnerRefreshLayout != null && !mInnerRefreshLayout.isRefreshing()) {
            mInnerRefreshLayout.autoRefresh();
        }
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mPage++;
        loadData();
    }

    protected abstract void loadData();

    /**
     * 设置当数据为空的提示
     *
     * @param isEmpty
     */
    protected abstract void setEmptyView(boolean isEmpty, String msg);

}
