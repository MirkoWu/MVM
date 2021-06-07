package com.mirkowu.lib_photo.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConstant;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

import java.util.ArrayList;

/**
 * Multi image selector
 */
public class ImagePickerActivity extends AppCompatActivity /*implements IPickerCallback*/ {


    //    private ArrayList<MediaBean> mSelectedList = new ArrayList<>();
    private Button mSubmitButton;
    private int mMaxSelectCount = PickerConstant.DEFAULT_IMAGE_SIZE;
    private ImagePickerFragment mPickerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTheme(R.style.IVP_NO_ACTIONBAR);
        setContentView(R.layout.ivp_activity_image_picker);

        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.ivp_toolbar_color));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor();
//        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        final Intent intent = getIntent();
//        mMaxSelectCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
//        final int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
//        final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
//        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
//            mSelectedList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
//        }
//        if (mode == MODE_SINGLE) {
//            mMaxSelectCount = DEFAULT_SINGLE_SIZE;
//        }
        mSubmitButton = findViewById(R.id.btnCommit);


        mSubmitButton.setVisibility(View.VISIBLE);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResult();
            }
        });

        //  Bundle bundle = getIntent().getExtras();
        if (savedInstanceState == null/* && bundle != null*/) {
//            Bundle bundle = new Bundle();
//            bundle.putInt(ImagePickerFragment.EXTRA_SELECT_COUNT, mMaxSelectCount);
//            bundle.putInt(ImagePickerFragment.EXTRA_SELECT_MODE, mode);
//            bundle.putBoolean(ImagePickerFragment.EXTRA_SHOW_CAMERA, isShow);
//            bundle.putStringArrayList(ImagePickerFragment.EXTRA_DEFAULT_SELECTED_LIST, mSelectedList);

//            if (bundle.containsKey(PickerConstant.EXTRA_DEFAULT_SELECTED_LIST)) {
//                mSelectedList = bundle.getStringArrayList(PickerConstant.EXTRA_DEFAULT_SELECTED_LIST);
//            }
            ArrayList<MediaBean> mSelectedList = ImagePicker.getInstance().getPickerConfig().getOriginSelectList();
            updateDoneText(mSelectedList);

            mPickerFragment = ImagePickerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, mPickerFragment)
                    .commit();
        }
    }

    /**
     * 提交结果
     */
    public void submitResult() {
//        mSelectedList = mPickerFragment.getSelectedList();
//        if (mSelectedList != null && mSelectedList.size() > 0) {
//            // Notify success
//            Intent data = new Intent();
//            data.putStringArrayListExtra(EXTRA_RESULT, mSelectedList);
//            setResult(RESULT_OK, data);
//        } else {
//            setResult(RESULT_CANCELED);
//        }
//        finish();
        mPickerFragment.submitResult();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update done button by select image data
     *
     * @param mSelectedList selected image data
     */
    public void updateDoneText(ArrayList<MediaBean> mSelectedList) {
        int size = mSelectedList != null ? mSelectedList.size() : 0;
        if (size <= 0) {
            mSubmitButton.setText(R.string.ivp_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setEnabled(true);
            mSubmitButton.setText(getString(R.string.ivp_action_button_string,
                    getString(R.string.ivp_action_done), size, mMaxSelectCount));
        }
    }

//    @Override
//    public void onSingleImageSelected(String path) {
////        Intent data = new Intent();
////        mSelectedList.add(path);
////        data.putStringArrayListExtra(EXTRA_RESULT, mSelectedList);
////        setResult(RESULT_OK, data);
////        finish();
//
//        mSelectedList.clear();
//        mSelectedList.add(path);
//        updateDoneText(mSelectedList);
//    }
//
//    @Override
//    public void onImageSelected(String path) {
//        if (!mSelectedList.contains(path)) {
//            mSelectedList.add(path);
//        }
//        updateDoneText(mSelectedList);
//    }
//
//    @Override
//    public void onImageUnselected(String path) {
//        if (mSelectedList.contains(path)) {
//            mSelectedList.remove(path);
//        }
//        updateDoneText(mSelectedList);
//    }

//    @Override
//    public void onCameraShot(File imageFile) {
//        if (imageFile != null) {
//            // notify system the image has change
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
//
//            mSelectedList = mPickerFragment.getSelectedList();
//            if (mSelectedList != null) {
//                mSelectedList = new ArrayList<>();
//            }
//            mSelectedList.add(imageFile.getAbsolutePath());
//
//            Intent data = new Intent();
//            data.putStringArrayListExtra(EXTRA_RESULT, mSelectedList);
//            setResult(RESULT_OK, data);
//            finish();
//        }
//    }
}
