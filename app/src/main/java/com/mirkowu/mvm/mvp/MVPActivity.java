package com.mirkowu.mvm.mvp;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_widget.dialog.PromptDialog;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.databinding.ActivityMVPBinding;

public class MVPActivity extends BaseActivity<MVPMediator> implements IMVPView {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVPActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    //    TextView tvTime;
    private ActivityMVPBinding binding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_m_v_p;
    }

    @Override
    protected void bindContentView() {
        binding = ActivityMVPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initialize() {
//        tvTime = findViewById(R.id.tvTime);

        mMediator.getData();

    }

    public void getTimeClick(View view) {
        //请求数据
        mMediator.getData();
    }

    @Override
    public void onLoadDataSuccess(Object o) {
        binding.tvTime.setText(o.toString());
    }

    @Override
    public void onLoadDataError(Throwable t) {

    }
}