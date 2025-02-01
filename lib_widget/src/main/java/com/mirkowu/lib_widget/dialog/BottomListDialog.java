package com.mirkowu.lib_widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_widget.R;
import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.mirkowu.lib_widget.adapter.BaseRVHolder;
import com.mirkowu.lib_widget.decoration.LinearDecoration;

import java.util.List;

/**
 * 底部列表弹窗
 */

public class BottomListDialog extends BaseDialog {

    private CharSequence mTitle;
    private boolean mShowCancelBtn; //是否显示取消按钮
    private boolean mUseRoundBackground; // 是否使用圆角背景
    private List<CharSequence> mData;

    public BottomListDialog() {
        setShowBottom(true);
        setTouchOutCancel(false);
        setDialogCancelable(false);
        setRoundBackground(true);
        setShowCancelBtn(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.widget_dialog_bottom_list;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        //设置dialog在屏幕的位置 不用使其铺满整个屏幕 ，
//        // 也解决getDialog().getWindow().setLayout(MATCH_PARENT，MATCH_PARENT）和 setCancelable(true)冲突了
//        //该方法只对根布局为LinearLayout 适用
//        getDialog().getWindow().setGravity(Gravity.BOTTOM);
//
//        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
//        getDialog().getWindow().setAttributes(lp);
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
//        //使dialog和软键盘可以共存
//        getDialog().getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        setCancelable(true);
//    }

    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {
        TextView tvTitle = (TextView) viewHolder.getView(R.id.tvTitle);
        TextView tvCancel = (TextView) viewHolder.getView(R.id.tvCancel);
        RecyclerView rvList = (RecyclerView) viewHolder.getView(R.id.mRecyclerView);

        tvTitle.setText(mTitle);
        tvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? View.GONE : View.VISIBLE);
        tvCancel.setVisibility(mShowCancelBtn ? View.VISIBLE : View.GONE);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomListDialog.this.dismissAllowingStateLoss();
            }
        });
        LinearDecoration decoration = new LinearDecoration(getContext())
                .setSpaceColor(ContextCompat.getColor(getContext(), R.color.widget_color_line_e5))
                .setSpace(0.5f);
        if (!TextUtils.isEmpty(mTitle)) {
            decoration.setTopSpace(0.5f);
        }
        rvList.addItemDecoration(decoration);


        ListAdapter listAdapter = new ListAdapter();
        listAdapter.setData(mData);

        listAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(BottomListDialog.this, listAdapter.getItem(position), position);
                }
                BottomListDialog.this.dismissAllowingStateLoss();
            }
        });

        rvList.setAdapter(listAdapter);

        if (mUseRoundBackground) {
            LinearLayout llRoot = viewHolder.getView(R.id.llRoot);
            LinearLayout llContent = viewHolder.getView(R.id.llContent);
            llContent.setBackgroundResource(R.drawable.widget_card_bg_6dp);
            tvCancel.setBackgroundResource(R.drawable.widget_press_card_6dp);
        }

        setDialogCancelable(false);
        setTouchOutCancel(false);
        setShowBottom(true);
    }

    public BottomListDialog setTitle(CharSequence title) {
        this.mTitle = title;
        return this;
    }

    public BottomListDialog setData(List<CharSequence> data) {
        this.mData = data;
        return this;
    }

    public BottomListDialog setShowCancelBtn(boolean showCancelBtn) {
        this.mShowCancelBtn = showCancelBtn;
        return this;
    }

    public BottomListDialog setRoundBackground(boolean useRoundBackground) {
        this.mUseRoundBackground = useRoundBackground;
        return this;
    }

    public BottomListDialog setOnItemClickListener(OnItemClickListener<String> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
        return this;
    }

    @Override
    public BottomListDialog show(FragmentManager manager) {
        return (BottomListDialog) super.show(manager);
    }

    @Override
    public BottomListDialog show(FragmentActivity activity) {
        return (BottomListDialog) super.show(activity);
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(BottomListDialog dialog, T data, int position);
    }

    public static class ListAdapter extends BaseRVAdapter<CharSequence, BaseRVHolder> {
        @NonNull
        @Override
        public BaseRVHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
            return new BaseRVHolder(mLayoutInflater.inflate(R.layout.widget_item_bottom_list, parent, false));
        }

        @Override
        public void onBindHolder(@NonNull BaseRVHolder holder, CharSequence item, int position) {
            TextView tvContent = holder.getView(R.id.tvContent);
            tvContent.setText(item);
        }
    }

}
