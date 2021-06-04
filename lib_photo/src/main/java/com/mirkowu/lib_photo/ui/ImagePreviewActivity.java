package com.mirkowu.lib_photo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.io.File;
import java.util.ArrayList;


public class ImagePreviewActivity extends AppCompatActivity implements OnPageChangeListener, View.OnClickListener {
    //    public static final String KEY_MAX_SELECT_COUNT = "max_selected_count";//已选择的图片列表
//    public static final String KEY_SELECTED_DATA = "selected_data";//已选择的图片列表
//    public static final String KEY_ORIGIN_DATA = "origin_data";//总的图片列表
    public static final String KEY_CURRENT_POS = "current_pos";//当前图片下标
    public static final String KEY_SAVE_PATH = "save_path";//下载时 要保存的路径
    public static final String KEY_SUBMIT = "key_submit"; //是否提交
    public static final String KEY_NOTIFY_DATA = "key_notify_data"; //是否更新数据
    public static final String KEY_FROM_PICK = "key_from_pick";//是否来自挑选挑选
    public static final int REQUEST_CODE_PREVIEW = 0x1101;//

    private ViewPagerFixed mViewPager;
    private PreviewImageAdapter adapter;
    private RelativeLayout rlBottom;
    private LinearLayout llSave;
    private TextView tvPick;
    private Button btnCommit;
    private int currentPos;
    private ArrayList<MediaBean> mOriginList;
    private ArrayList<MediaBean> mSelectedList;
    private boolean mFromPick;// 是否来自挑选

    private String savePath;
    private Toolbar mToolbar;
    private boolean mIsHidden = false;//标题栏是否隐藏
    private long mLastShowHiddenTime;//上次显示的时间
    private int mMaxSelectCount;//最大可选择数量
    private boolean mNotifyData = false;

    /**
     * 网络图片 可能会下载
     *
     * @param context
     * @param savePath
     * @param originData
     * @param currentPos
     */
//    public static void startFromPreview(Context context, String savePath, ArrayList<String> originData, int currentPos) {
//        Intent intent = new Intent(context, ImagePreviewActivity.class);
//        intent.putExtra(KEY_FROM_PICK, false);
//        intent.putExtra(KEY_CURRENT_POS, currentPos);
//        intent.putExtra(KEY_SAVE_PATH, savePath);
//        intent.putStringArrayListExtra(KEY_ORIGIN_DATA, originData);
//        context.startActivity(intent);
//    }
//
//    public static void startFromPick(Fragment fragment, ArrayList<String> selectedData, int currentPos, int maxCount) {
//        Intent intent = new Intent(fragment.getContext(), ImagePreviewActivity.class);
//        intent.putExtra(KEY_FROM_PICK, true);
//        intent.putExtra(KEY_CURRENT_POS, currentPos);
//        intent.putExtra(KEY_MAX_SELECT_COUNT, maxCount);
//        // intent.putStringArrayListExtra(KEY_ORIGIN_DATA, originData);
//        intent.putStringArrayListExtra(KEY_SELECTED_DATA, selectedData);
//        fragment.startActivityForResult(intent, REQUEST_CODE_PREVIEW);
//    }
    public static void startFromPick(Fragment fragment, int currentPos) {
        Intent intent = new Intent(fragment.getContext(), ImagePreviewActivity.class);
        intent.putExtra(KEY_FROM_PICK, true);
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
        setTheme(R.style.IVP_NO_ACTIONBAR);
        setContentView(R.layout.ivp_activity_image_preview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = findViewById(R.id.mViewPager);
        llSave = findViewById(R.id.llSave);
        ImageView ivSave = findViewById(R.id.ivSave);

        savePath = getIntent().getStringExtra(KEY_SAVE_PATH);
        if (!TextUtils.isEmpty(savePath)) {
            llSave.setVisibility(View.VISIBLE);
            ivSave.setOnClickListener(this);
        }


        initView();
    }

    private void delImage() {
        // 弹窗提示删除
        new AlertDialog.Builder(ImagePreviewActivity.this)
                .setTitle("确认删除？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        if (listViews.size() == 1) {
//                            originData.clear();
//                            intent.putStringArrayListExtra("pic", originData);
//                            setResult(1, intent);
//                            finish();
//                        } else {
//                            mOriginList.remove(currentPos);
//                            setTitle((currentPos + 1) + "/" + originData.size());
//                            mViewPager.removeAllViews();
//
//                            adapter.setListViews(listViews);
//                            adapter.notifyDataSetChanged();
//                            intent.putStringArrayListExtra("pic", originData);
//                            setResult(1, intent);
//                        }
                    }
                }).create().show();
    }

    private void initView() {
        mFromPick = getIntent().getBooleanExtra(KEY_FROM_PICK, false);
        mMaxSelectCount = ImagePicker.getInstance().getPickerConfig().getMaxPickCount();
        currentPos = getIntent().getIntExtra(KEY_CURRENT_POS, 0);
        mSelectedList = ResultModel.getList();

        if (mFromPick) {
            mOriginList = MediaModel.getCurChildList();
        } else {
            // mOriginList = getIntent().getStringArrayListExtra(KEY_ORIGIN_DATA);
        }
        if (mOriginList == null) mOriginList = new ArrayList<>();

        btnCommit = findViewById(R.id.btnCommit);
        rlBottom = findViewById(R.id.rlBottom);
        tvPick = findViewById(R.id.tvPick);

        if (!mFromPick) {
            rlBottom.setVisibility(View.GONE);
            btnCommit.setVisibility(View.GONE);
        }

        adapter = new PreviewImageAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChangeToolbarState();
            }
        }, mOriginList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPos);
        mViewPager.addOnPageChangeListener(this);
        tvPick.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        updateDoneText();
        setPosTitle();
    }

    private void setPosTitle() {
        setTitle(getString(R.string.ivp_preview_position, currentPos + 1, mOriginList.size()));
        if (mSelectedList != null && !mSelectedList.isEmpty()) {
            tvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_unselected, 0, 0, 0);
            for (MediaBean bean : mSelectedList) {
                if (TextUtils.equals(mOriginList.get(currentPos).path, bean.path)) {
                    tvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
                    return;
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPos = position;
        setPosTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivSave) { //保存图片
            final File saveFile = new File(savePath);
//            FileUtils.save2SDCard(ImagePreviewActivity.this, mOriginList.get(currentPos))
//                    .subscribe(new Consumer<Bitmap>() {
//                        @Override
//                        public void accept(@NonNull Bitmap bitmap) throws Exception {
//                            FileUtils.saveBitmap2File(ImagePreviewActivity.this, bitmap, saveFile, System.currentTimeMillis() + ".jpg");
//                            // ImageEngine.saveBitmapToGallery(ImagePreviewActivity.this,bitmap, saveFile, System.currentTimeMillis() + ".jpg");
//
//                            Toast.makeText(ImagePreviewActivity.this, getString(R.string.ivp_save_successed,
//                                    saveFile.getAbsoluteFile()), Toast.LENGTH_SHORT).show();
//
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(@NonNull Throwable throwable) throws Exception {
//                            Toast.makeText(ImagePreviewActivity.this,
//                                    getString(R.string.ivp_save_failed), Toast.LENGTH_SHORT).show();
//                        }
//                    });

        } else if (id == R.id.tvPick) { //选择
            mNotifyData = true; //选择后需要更新
            MediaBean mediaBean = mOriginList.get(currentPos);
            if (mMaxSelectCount == 1) {

                ResultModel.addSingle(mediaBean);
                tvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
            } else {


                if (ResultModel.contains(mediaBean)) {
                    ResultModel.remove(mediaBean);
                    tvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_unselected, 0, 0, 0);
                } else {
                    if (!ResultModel.checkCanAdd()) {
                        Toast.makeText(this, R.string.ivp_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ResultModel.addMulti(mediaBean);
                    tvPick.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ivp_btn_selected, 0, 0, 0);
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
            btnCommit.setEnabled(false);
            btnCommit.setText(R.string.ivp_action_done);
        } else {
            btnCommit.setEnabled(true);
            btnCommit.setText(getString(R.string.ivp_action_button_string,
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

        if (mFromPick && rlBottom != null) {
            rlBottom.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(rlBottom, 0);
            ViewCompat.animate(rlBottom).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
        }
        if (!TextUtils.isEmpty(savePath) && llSave != null) {
            llSave.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(llSave, 0);
            ViewCompat.animate(llSave).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
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
        if (mFromPick && rlBottom != null) {
            ViewCompat.animate(rlBottom).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
        if (!TextUtils.isEmpty(savePath) && llSave != null) {
            ViewCompat.animate(llSave).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @Override
    public void onBackPressed() {
        backPick(false);
    }

    private void backPick(boolean submitResult) {
        Intent intent = new Intent();
        //   intent.putExtra(KEY_SELECTED_DATA, mSelectedList);
        intent.putExtra(KEY_SUBMIT, submitResult);
        intent.putExtra(KEY_NOTIFY_DATA, mNotifyData);
        setResult(RESULT_OK, intent);
        finish();
    }

}

 
