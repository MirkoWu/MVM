package com.mirkowu.lib_photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.adapter.PreviewImageAdapter;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.mediaLoader.MediaModel;
import com.mirkowu.lib_photo.mediaLoader.ResultModel;
import com.mirkowu.lib_photo.view.ViewPagerFixed;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

import java.util.ArrayList;


public class AlbumPreviewActivity extends AppCompatActivity implements OnPageChangeListener, View.OnClickListener {
    public static final String KEY_SUBMIT = "key_submit"; //是否提交
    public static final String KEY_CURRENT_POS = "key_current_pos"; //当前图片下标
    public static final String KEY_NOTIFY_DATA = "key_notify_data"; //是否更新数据
    public static final int REQUEST_CODE_PREVIEW = 0x1101; //

    private ViewPagerFixed mViewPager;
    private PreviewImageAdapter mAdapter;
    private RelativeLayout mRlBottom;
    private TextView mTvPick;
    private Button mBtnCommit;
    private int mCurPosition;
    private ArrayList<MediaBean> mOriginList;
    private ArrayList<MediaBean> mSelectedList;

    private Toolbar mToolbar;
    private boolean mIsHidden = false; //标题栏是否隐藏
    private long mLastShowHiddenTime; //上次显示的时间
    private int mMaxSelectCount; //最大可选择数量
    private boolean mNotifyData = false; //是否通知刷新


    public static void start(Fragment fragment, int currentPos) {
        Intent intent = new Intent(fragment.getContext(), AlbumPreviewActivity.class);
        intent.putExtra(KEY_CURRENT_POS, currentPos);
        fragment.startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ivp_activity_album_preview);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.ivp_toolbar_color));


        initView();
    }

    private void initView() {
        mMaxSelectCount = ImagePicker.getInstance().getPickerConfig().getMaxPickCount();
        mCurPosition = getIntent().getIntExtra(KEY_CURRENT_POS, 0);
        mSelectedList = ResultModel.getList();
        mOriginList = MediaModel.getCurChildList();

        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mViewPager = findViewById(R.id.mViewPager);

        mBtnCommit = findViewById(R.id.btnCommit);
        mRlBottom = findViewById(R.id.rlBottom);
        mTvPick = findViewById(R.id.tvPick);

        mAdapter = new PreviewImageAdapter(mOriginList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChangeToolbarState();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurPosition);
        mViewPager.addOnPageChangeListener(this);
        mTvPick.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        updateDoneText();
        setPosTitle();
    }

    private void setPosTitle() {
        setTitle(getString(R.string.ivp_preview_position, mCurPosition + 1, mOriginList.size()));
        MediaBean curBean = mOriginList.get(mCurPosition);
        if (ResultModel.contains(curBean)) {
            mTvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
        } else {
            mTvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_unselected, 0, 0, 0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
        setPosTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvPick) { //选择
            mNotifyData = true; //选择后需要更新
            MediaBean mediaBean = mOriginList.get(mCurPosition);
            if (mMaxSelectCount == 1) {
                ResultModel.addSingle(mediaBean);
                mTvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
            } else {
                if (ResultModel.contains(mediaBean)) {
                    ResultModel.remove(mediaBean);
                    mTvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_unselected, 0, 0, 0);
                } else {
                    if (!ResultModel.checkCanAdd()) {
                        Toast.makeText(this, R.string.ivp_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ResultModel.addMulti(mediaBean);
                    mTvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
                }
            }

            updateDoneText();
        } else if (id == R.id.btnCommit) { //确定提交
            backPick(true);
        }
    }

    private void clickChangeToolbarState() {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500L) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showToolbar();
            } else {
                hideToolbar();
            }
        }
    }

    /**
     * 更新标题
     */
    public void updateDoneText() {
        if (ResultModel.isEmpty()) {
            mBtnCommit.setEnabled(false);
            mBtnCommit.setText(R.string.ivp_action_done);
        } else {
            mBtnCommit.setEnabled(true);
            mBtnCommit.setText(getString(R.string.ivp_action_button_string,
                    getString(R.string.ivp_action_done), ResultModel.size(), mMaxSelectCount));
        }
    }

    private void showToolbar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(View view) {
                            mIsHidden = false;
                        }
                    }).start();
        }

        if (mRlBottom != null) {
            mRlBottom.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(mRlBottom, 0);
            ViewCompat.animate(mRlBottom).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    private void hideToolbar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2))
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(View view) {
                            mIsHidden = true;
                        }
                    }).start();
        }

        if (mRlBottom != null) {
            ViewCompat.animate(mRlBottom).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @Override
    public void onBackPressed() {
        backPick(false);
    }

    private void backPick(boolean submitResult) {
        Intent intent = new Intent();
        intent.putExtra(KEY_SUBMIT, submitResult);
        intent.putExtra(KEY_NOTIFY_DATA, mNotifyData);
        setResult(RESULT_OK, intent);
        finish();
    }

}