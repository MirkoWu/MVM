package com.mirkowu.lib_photo.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.adapter.PreviewImageAdapter;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.utils.FileUtils;
import com.mirkowu.lib_photo.view.ViewPagerFixed;
import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_util.utilcode.util.ThreadUtils;

import java.io.File;
import java.util.ArrayList;


/**
 * 通用的图片预览界面
 */
public class PreviewActivity extends AppCompatActivity implements OnPageChangeListener, View.OnClickListener {
    public static final String KEY_ORIGIN_DATA = "key_origin_data"; //总的图片列表
    public static final String KEY_CURRENT_POS = "key_current_pos"; //当前图片下标
    public static final String KEY_SAVE_PATH = "key_save_path"; //下载时 要保存的路径

    private ViewPagerFixed mViewPager;
    private PreviewImageAdapter mAdapter;
    private LinearLayout mLlSave;
    private int mCurPosition;
    private ArrayList<MediaBean> mOriginList;

    private String mSavePath;
    private Toolbar mToolbar;
    private boolean mIsHidden = false; //标题栏是否隐藏
    private long mLastShowHiddenTime; //上次显示的时间

    /**
     * 网络图片 可能会下载
     *
     * @param context
     * @param savePath
     * @param originData
     * @param currentPos
     */
    public static void start(Context context, String savePath, ArrayList<MediaBean> originData, int currentPos) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(KEY_CURRENT_POS, currentPos);
        intent.putExtra(KEY_SAVE_PATH, savePath);
        intent.putParcelableArrayListExtra(KEY_ORIGIN_DATA, originData);
        context.startActivity(intent);
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
        setContentView(R.layout.ivp_activity_preview);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.ivp_toolbar_color));

        mSavePath = getIntent().getStringExtra(KEY_SAVE_PATH);
        mCurPosition = getIntent().getIntExtra(KEY_CURRENT_POS, 0);
        mOriginList = getIntent().getParcelableArrayListExtra(KEY_ORIGIN_DATA);
        if (mOriginList == null) {
            mOriginList = new ArrayList<>();
        }
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mLlSave = findViewById(R.id.llSave);
        mViewPager = findViewById(R.id.mViewPager);
        ImageView ivSave = findViewById(R.id.ivSave);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!TextUtils.isEmpty(mSavePath)) {
            mLlSave.setVisibility(View.VISIBLE);
            ivSave.setOnClickListener(this);
        }
        mAdapter = new PreviewImageAdapter(mOriginList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChangeToolbarState();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurPosition);
        mViewPager.addOnPageChangeListener(this);
        setPosTitle();
    }

    private void setPosTitle() {
        setTitle(getString(R.string.ivp_preview_position, mCurPosition + 1, mOriginList.size()));
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
        if (id == R.id.ivSave) { //保存图片
            doDownloadTask();
        }
    }

    private void doDownloadTask() {
        final File saveFile = new File(mSavePath);
        String url = mOriginList.get(mCurPosition).path;
        ThreadUtils.executeByIo(new ThreadUtils.Task<Bitmap>() {
            @Override
            public Bitmap doInBackground() throws Throwable {
                Bitmap bitmap = ImagePicker.getInstance().getImageEngine()
                        .loadAsBitmap(PreviewActivity.this, url);
                return bitmap;
            }

            @Override
            public void onSuccess(Bitmap result) {
                FileUtils.saveBitmap2File(PreviewActivity.this, result, saveFile,
                        System.currentTimeMillis() + ".jpg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PreviewActivity.this, getString(R.string.ivp_save_successed,
                                saveFile.getAbsoluteFile()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PreviewActivity.this, getString(R.string.ivp_save_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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

        if (!TextUtils.isEmpty(mSavePath) && mLlSave != null) {
            mLlSave.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(mLlSave, 0);
            ViewCompat.animate(mLlSave).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
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

        if (!TextUtils.isEmpty(mSavePath) && mLlSave != null) {
            ViewCompat.animate(mLlSave).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}