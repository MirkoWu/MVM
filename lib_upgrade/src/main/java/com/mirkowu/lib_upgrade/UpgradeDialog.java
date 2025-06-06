package com.mirkowu.lib_upgrade;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mirkowu.lib_util.FileUtils;
import com.mirkowu.lib_util.HtmlUtils;
import com.mirkowu.lib_util.IntentUtils;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.permission.PermissionUtils;
import com.mirkowu.lib_util.permission.Permissions;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;
import com.mirkowu.lib_widget.dialog.BaseDialog;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * 网络Url下载更新版本
 */
public class UpgradeDialog extends BaseDialog implements View.OnClickListener {
    private static final int DEFAULT_WIDTH = 280; //默认宽度 dp


    public static void show(FragmentManager manager, @NonNull IUpgradeInfo upgradeInfo) {
        FileDownloader.setup(Utils.getApp());

        UpgradeDialog dialog = new UpgradeDialog();
        dialog.upgradeInfo = upgradeInfo;
        dialog.showAllowingStateLoss(manager);
    }


    protected ImageView ivIcon;
    protected TextView tvTitle;
    protected TextView tvContent;
    protected TextView tvPositive;
    protected TextView tvNegative;

    protected RelativeLayout llProgress;
    protected ProgressBar mProgressBar;
    protected TextView tvProgress;


    protected OnButtonClickListener listener;
    protected BaseDownloadTask downloadTask;
    public IUpgradeInfo upgradeInfo;
    protected File apkFile;

    public UpgradeDialog() {
        setWidth(DEFAULT_WIDTH);
        setTouchOutCancel(false);
        setDialogCancelable(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.up_dialog_upgrade;
    }

    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {
        ivIcon = viewHolder.getView(R.id.ivIcon);
        tvTitle = viewHolder.getView(R.id.tvTitle);
        tvContent = viewHolder.getView(R.id.tvContent);
        tvPositive = viewHolder.getView(R.id.tvPositive);
        tvNegative = viewHolder.getView(R.id.tvNegative);

        llProgress = viewHolder.getView(R.id.llProgress);
        mProgressBar = viewHolder.getView(R.id.mProgressBar);
        tvProgress = viewHolder.getView(R.id.tvProgress);

        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);
        ivIcon.setVisibility(GONE);

        if (TextUtils.isEmpty(upgradeInfo.getTitle())) {
            tvTitle.setText(R.string.up_check_new_version);
        } else {
            tvTitle.setText(upgradeInfo.getTitle());
        }
        HtmlUtils.setTextViewHtml(tvContent, upgradeInfo.getContent()); //支持html

        tvNegative.setVisibility(upgradeInfo.isForceUpgrade() ? GONE : VISIBLE);
        tvNegative.setText(R.string.up_next_time);
        tvPositive.setVisibility(VISIBLE);
        tvPositive.setText(R.string.up_upgrade);
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
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvPositive) {
            if (listener != null) {
                listener.onButtonClick(this, true);
            }
            if (apkFile != null) {
                IntentUtils.startInstall(getContext(), apkFile);
            } else {
                if (PermissionUtils.hasPermissions(getContext(), Permissions.getGROUP_STORAGE())) {
                    startDownloadTask();
                } else {
                    LogUtil.e("没有存储权限");
                    if (listener != null) {
                        listener.onNeedPermission(this);
                    } else {
                        LogUtil.e("下载到默认路径");
                        startDownloadTask();
                    }
                }
            }
        } else if (i == R.id.tvNegative) {
            if (listener != null) {
                listener.onButtonClick(this, true);
            }
            if (downloadTask != null) {
                downloadTask.pause();
                FileDownloader.getImpl().clear(downloadTask.getId(), downloadTask.getTargetFilePath());
            }

            dismissAllowingStateLoss();
        }
    }

    public UpgradeDialog setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
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

    public void startDownloadTask() {
        llProgress.setVisibility(VISIBLE);
        tvPositive.setVisibility(GONE);
        tvNegative.setVisibility(VISIBLE);
        if (upgradeInfo.isForceUpgrade()) {
            tvNegative.setText(R.string.up_downloading);
            tvNegative.setEnabled(false);
        } else {
            tvNegative.setText(R.string.up_cancel);
            tvNegative.setEnabled(true);
        }

        if (TextUtils.isEmpty(upgradeInfo.getApkUrl())) {
            ToastUtils.showShort(R.string.up_upgrade_failed);
            dismissAllowingStateLoss();
            return;
        }
        apkFile = null;

        String path = upgradeInfo.getSavePath();
        if (TextUtils.isEmpty(path)) {
            path = FileUtils.getAppCachePath(getContext()) + File.separator
                    + "download" + File.separator + upgradeInfo.getVersionName() + ".apk";
        }
        startTask(path);
    }

    protected void startTask(String path) {
        downloadTask = FileDownloader.getImpl()
                .create(upgradeInfo.getApkUrl())
                .setPath(path)
                .setForceReDownload(true)
                .setCallbackProgressTimes(100)
                .setMinIntervalUpdateSpeed(100)
                .setListener(new FileDownloadSampleListener() {
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (!isAdded() || isDetached()) return;
                        int progress = (int) ((soFarBytes * 100.0) / totalBytes);
                        tvProgress.setText(String.format("%d%%", progress));
                        mProgressBar.setProgress(progress);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtil.e("下载成功，path =:" + path);
                        if (!isAdded() || isDetached()) return;

                        tvProgress.setText(String.format("%d%%", 100));
                        tvNegative.setVisibility(GONE);
                        tvNegative.setEnabled(true);
                        tvPositive.setVisibility(VISIBLE);
                        tvPositive.setText(R.string.up_click_install);

                        apkFile = new File(path);
                        IntentUtils.startInstall(getContext(), apkFile);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.e("下载失败:" + e.toString());
                        FileDownloader.getImpl().clear(task.getId(), path);
                        ToastUtils.showShort(R.string.up_download_failure);
                        dismissAllowingStateLoss();
                    }
                });


        downloadTask.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (downloadTask != null) {
            downloadTask.pause();
        }
    }
}
