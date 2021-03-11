package com.mirkowu.mvm.mvc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mirkowu.lib_mvm.util.LogUtil;
import com.mirkowu.lib_mvm.util.RxLife;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.network.RxObserver;

import io.reactivex.rxjava3.functions.Action;

/**
 * MVC 不太建议，只做参考
 */
public class MVCActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVCActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    TextView tvTime;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_m_v_c;
    }

    @Override
    protected void initialize() {
        tvTime = findViewById(R.id.tvTime);
        loadData();
    }

    public void getTimeClick(View view) {
        loadData();
    }

    private void loadData() {
        new BizModel().loadData()
                .to(RxLife.bindLifecycle(this))
                .subscribe(new RxObserver<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        tvTime.setText(data.toString());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                });
    }

    @Override
    protected void createMediator() {
//        super.createMediator();
    }
}