package com.mirkowu.mvm.mvc;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.RxLife;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;
import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.network.RxObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * MVC 不太建议，只做参考
 */
public class MVCActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVCActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected BaseMediator initMediator() {
        return null;
    }

    TextView tvTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_m_v_c;
    }

    @Override
    protected void initialize() {
        tvTime = findViewById(R.id.tvTime);
        RecyclerView rvMvc = findViewById(R.id.rv_mvc);
        TestAdapter adapter = new TestAdapter();
        rvMvc.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMvc.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                ToastUtils.showShort("Item点击");
            }
        });
        adapter.setOnItemChildClickListener(new BaseRVAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(View view, Object item, int position) {
                ToastUtils.showShort("Item child点击");

            }
        });
        adapter.setOnItemChildLongClickListener(new BaseRVAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(View view, Object item, int position) {
                ToastUtils.showShort("Item child long 点击");

                return true;
            }
        });
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter.setData(list);

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
                    public void onFailure(@NonNull ErrorType errorType, int code, String msg) {

                    }


                });
    }


}