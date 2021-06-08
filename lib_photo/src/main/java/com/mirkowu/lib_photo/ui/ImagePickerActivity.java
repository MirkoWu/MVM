package com.mirkowu.lib_photo.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConfig;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.mediaLoader.ResultModel;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

import java.util.ArrayList;

/**
 * Multi image selector
 */
public class ImagePickerActivity extends AppCompatActivity {
    private Button mSubmitButton;
    private ImagePickerFragment mPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ivp_activity_image_picker);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.ivp_toolbar_color));

        initView();

        mPickerFragment = ImagePickerFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_container, mPickerFragment)
                .commit();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSubmitButton = findViewById(R.id.btnCommit);
        mSubmitButton.setVisibility(View.VISIBLE);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResult();
            }
        });
    }

    /**
     * 提交结果
     */
    public void submitResult() {
        if (mPickerFragment != null) {
            mPickerFragment.submitResult();
        }
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
     * 更新标题数量
     */
    public void updateDoneText() {
        PickerConfig config = ImagePicker.getInstance().getPickerConfig();
        ArrayList<MediaBean> selectList = ResultModel.getList();

        int size = selectList != null ? selectList.size() : 0;
        if (size <= 0) {
            mSubmitButton.setText(R.string.ivp_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setEnabled(true);
            mSubmitButton.setText(getString(R.string.ivp_action_button_string,
                    getString(R.string.ivp_action_done), size, config.getMaxPickCount()));
        }
    }
}
