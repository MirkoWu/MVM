package com.mirkowu.mvm.mvp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;

public class MVPActivity extends BaseActivity<MVPMediator> implements IMVPView {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVPActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    TextView tvTime;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_m_v_p;
    }


    @Override
    protected void initialize() {
        tvTime = findViewById(R.id.tvTime);

        mediator.getData();
    }

    public void getTimeClick(View view) {
        //请求数据
        mediator.getData();
    }

    @Override
    public void onLoadDataSuccess(Object o) {
        tvTime.setText(o.toString());
    }

    @Override
    public void onLoadDataError(Throwable t) {

    }
}