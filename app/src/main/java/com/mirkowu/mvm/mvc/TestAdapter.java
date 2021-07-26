package com.mirkowu.mvm.mvc;

import androidx.annotation.NonNull;

import com.mirkowu.lib_widget.adapter.BaseRVHolder;
import com.mirkowu.lib_widget.adapter.SimpleRVAdapter;
import com.mirkowu.mvm.R;

public class TestAdapter extends SimpleRVAdapter<String> {
    public TestAdapter() {
        super(R.layout.item_binding_list);
    }

    @Override
    public void onBindHolder(@NonNull BaseRVHolder holder, String item, int position) {
        holder.setText(R.id.tv_title, "标题" + position)
                .setText(R.id.tv_content, "内容" + position)
                .addOnClickListener(R.id.tv_title, R.id.tv_content)
                .addOnLongClickListener(R.id.tv_title, R.id.tv_content);

    }
}
