package com.mirkowu.lib_bugly;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.mirkowu.lib_util.utilcode.util.ScreenUtils;
import com.mirkowu.lib_widget.dialog.BaseDialog;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

/**
 * Bugly 升级版本SDK
 */
public class UpgradeDialog extends BaseDialog implements DownloadListener, UpgradeStateListener {
    TextView tvTitle;
    TextView tvContent;
    TextView tvProgress;
    TextView tvNegative;
    TextView tvPositive;
    ProgressBar mProgressBar;
    RelativeLayout llProgress;
    LinearLayout llButton;

    private UpgradeInfo upgradeInfo;
    private boolean isForceUpgrade;

    public static void show(FragmentManager manager, UpgradeInfo upgradeInfo) {

        UpgradeDialog dialog = new UpgradeDialog();
        dialog.upgradeInfo = upgradeInfo;
        dialog.show(manager);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.up_dialog_upgrade;
    }

    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {
        tvTitle = viewHolder.getView(R.id.tvTitle);
        tvContent = viewHolder.getView(R.id.tvContent);
        tvProgress = viewHolder.getView(R.id.tvProgress);
        mProgressBar = viewHolder.getView(R.id.mProgressBar);
        llProgress = viewHolder.getView(R.id.llProgress);
        tvNegative = viewHolder.getView(R.id.tvNegative);
        tvPositive = viewHolder.getView(R.id.tvPositive);
        llButton = viewHolder.getView(R.id.llButton);


        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });

        if (upgradeInfo == null) {
            dismissAllowingStateLoss();
            return;
        }

        tvTitle.setText(upgradeInfo.title);
        tvContent.setText(upgradeInfo.newFeature);

        if (isForceUpgrade = upgradeInfo.upgradeType == 2) { //强制
            tvNegative.setVisibility(View.GONE);
        } else {
            tvNegative.setVisibility(View.VISIBLE);
            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        setTouchOutCancel(false);
        setDialogCancelable(false);

        BuglyManager.registerDownloadListener(this);
        BuglyManager.setUpgradeStateListener(this);
    }

    private void startTask() {
        llButton.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);
//        tvNegative.setText(R.string.up_cancel);
//        tvNegative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BuglyManager.cancelDownload();
//                dismiss();
//            }
//        });
        BuglyManager.startDownloadTask();

        setTouchOutCancel(false);
        setDialogCancelable(false);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //宽度
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (ScreenUtils.getScreenWidth() * 0.75f);
            window.setAttributes(params);
        }
    }

    @Override
    public void onReceive(DownloadTask downloadTask) {
        int progress = (int) (downloadTask.getSavedLength() * 100f / downloadTask.getTotalLength());
        tvProgress.setText(String.format("%d%%", progress));
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {

        if (!isForceUpgrade) {
            dismissAllowingStateLoss();
        } else {
            llButton.setVisibility(View.VISIBLE);
            tvNegative.setVisibility(View.GONE);
            tvPositive.setVisibility(View.VISIBLE);
            llProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailed(DownloadTask downloadTask, int i, String s) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BuglyManager.unregisterDownloadListener();
        BuglyManager.cancelDownload();
    }

    @Override
    public void onUpgradeFailed(boolean b) {

    }

    @Override
    public void onUpgradeSuccess(boolean b) {
    }

    @Override
    public void onUpgradeNoVersion(boolean b) {

    }

    @Override
    public void onUpgrading(boolean b) {

    }

    @Override
    public void onDownloadCompleted(boolean b) {
    }
}
