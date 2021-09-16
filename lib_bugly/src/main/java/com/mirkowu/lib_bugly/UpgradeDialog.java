package com.mirkowu.lib_bugly;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;
import com.mirkowu.lib_widget.dialog.BaseDialog;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Bugly 升级版本SDK
 */
public class UpgradeDialog extends BaseDialog implements DownloadListener, UpgradeStateListener, View.OnClickListener {
    protected static final int DEFAULT_WIDTH = 280; //默认宽度 dp

    TextView tvTitle;
    TextView tvContent;
    TextView tvProgress;
    TextView tvNegative;
    TextView tvPositive;
    ProgressBar mProgressBar;
    RelativeLayout llProgress;
    LinearLayout llButton;

    protected OnButtonClickListener mClickListener;
    protected UpgradeInfo mUpgradeInfo;
    protected boolean mIsForceUpgrade;

    public static void show(FragmentManager manager, UpgradeInfo upgradeInfo) {
        if (upgradeInfo == null) {
            LogUtil.e("UpgradeDialog upgradeInfo == null");
            return;
        }
        UpgradeDialog dialog = new UpgradeDialog();
        dialog.setUpgradeInfo(upgradeInfo);
        dialog.showAllowingStateLoss(manager);
    }

    public UpgradeDialog() {
        setWidth(DEFAULT_WIDTH);
        setTouchOutCancel(false);
        setDialogCancelable(false);
    }

    @Override
    public int getLayoutResId() {
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

        tvPositive.setOnClickListener(this);

        if (mUpgradeInfo == null) {
            dismissAllowingStateLoss();
            return;
        }

        tvTitle.setText(mUpgradeInfo.title);
        tvContent.setText(mUpgradeInfo.newFeature);
        mIsForceUpgrade = mUpgradeInfo.upgradeType == 2;

        initButtonText();


        BuglyManager.registerDownloadListener(this);
        BuglyManager.setUpgradeStateListener(this);
    }

    private void initButtonText() {
        if (mIsForceUpgrade = mUpgradeInfo.upgradeType == 2) { //强制
            tvNegative.setVisibility(View.GONE);
        } else {
            tvNegative.setVisibility(View.VISIBLE);
            tvNegative.setOnClickListener(this);
        }

        tvNegative.setVisibility(mIsForceUpgrade ? GONE : VISIBLE);
        tvNegative.setText(com.mirkowu.lib_upgrade.R.string.up_next_time);
        tvPositive.setVisibility(VISIBLE);
        tvPositive.setText(com.mirkowu.lib_upgrade.R.string.up_upgrade);

        llProgress.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
//        //宽度
//        Window window = getDialog().getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = (int) (ScreenUtils.getScreenWidth() * 0.75f);
//            window.setAttributes(params);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initButtonText();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvPositive) {
            if (mClickListener != null) {
                mClickListener.onButtonClick(this, true);
            }
            if (PermissionsUtil.hasPermissions(getContext(), PermissionsUtil.GROUP_STORAGE)) {
                startDownloadTask();
            } else {
                LogUtil.e("没有存储权限");
                if (mClickListener != null) {
                    mClickListener.onNeedPermission(this);
                } else {
                    LogUtil.e("下载到默认路径");
                    startDownloadTask();
                }
            }
        } else if (i == R.id.tvNegative) {
            if (mClickListener != null) {
                mClickListener.onButtonClick(this, true);
            }
            BuglyManager.cancelDownload();
            dismissAllowingStateLoss();
        }
    }


    private void startDownloadTask() {
        llProgress.setVisibility(VISIBLE);
        tvPositive.setVisibility(GONE);
        tvNegative.setVisibility(VISIBLE);

        if (mIsForceUpgrade) {
            tvNegative.setText(com.mirkowu.lib_upgrade.R.string.up_downloading);
            tvNegative.setEnabled(false);
        } else {
            tvNegative.setText(com.mirkowu.lib_upgrade.R.string.up_cancel);
            tvNegative.setEnabled(true);
        }

        BuglyManager.startDownloadTask();
    }


    @Override
    public void onReceive(DownloadTask downloadTask) {
        if (!isAdded() || isDetached()) return;
        int progress = (int) (downloadTask.getSavedLength() * 100f / downloadTask.getTotalLength());
        llProgress.setVisibility(VISIBLE);
        tvProgress.setText(String.format("%d%%", progress));
        mProgressBar.setProgress(progress);

        //重置状态
        if (tvPositive.getVisibility() == VISIBLE) {
            tvPositive.setVisibility(GONE);
            tvNegative.setVisibility(VISIBLE);

            if (mIsForceUpgrade) {
                tvNegative.setText(com.mirkowu.lib_upgrade.R.string.up_downloading);
                tvNegative.setEnabled(false);
            } else {
                tvNegative.setText(com.mirkowu.lib_upgrade.R.string.up_cancel);
                tvNegative.setEnabled(true);
            }
        }
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        if (!isAdded() || isDetached()) return;
        if (mIsForceUpgrade) {
            llButton.setVisibility(View.VISIBLE);
            tvNegative.setVisibility(View.GONE);
            tvPositive.setVisibility(View.VISIBLE);
            llProgress.setVisibility(View.GONE);

            tvNegative.setVisibility(GONE);
            tvNegative.setEnabled(true);
            tvPositive.setVisibility(VISIBLE);
            tvPositive.setText(com.mirkowu.lib_upgrade.R.string.up_click_install);
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onFailed(DownloadTask downloadTask, int i, String s) {
        ToastUtils.showShort(R.string.strNotificationDownloadError);
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        BuglyManager.unregisterDownloadListener();
        BuglyManager.removeUpgradeStateListener();
        BuglyManager.cancelDownload();
        super.onDestroyView();
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

    public UpgradeDialog setUpgradeInfo(UpgradeInfo upgradeInfo) {
        this.mUpgradeInfo = upgradeInfo;
        return this;
    }

    public UpgradeDialog setOnButtonClickListener(UpgradeDialog.OnButtonClickListener listener) {
        this.mClickListener = listener;
        return this;
    }

    public interface OnButtonClickListener {

        default void onNeedPermission(UpgradeDialog dialog) {
        }

        /**
         * 当窗口按钮被点击
         *
         * @param dialog
         * @param isPositiveClick true :PositiveButton点击, false :NegativeButton点击
         * @return 是否
         */
        default void onButtonClick(UpgradeDialog dialog, boolean isPositiveClick) {
        }
    }
}
