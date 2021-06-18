package com.mirkowu.lib_upgrade;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mirkowu.lib_util.FileUtil;
import com.mirkowu.lib_util.HtmlUtil;
import com.mirkowu.lib_util.IntentUtil;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.ScreenUtils;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;
import com.mirkowu.lib_widget.dialog.BaseDialog;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AppUpgradeDialog extends BaseDialog implements View.OnClickListener {


    public static void show(FragmentManager manager, IUpgradeInfo upgradeInfo) {
        FileDownloader.setup(Utils.getApp());

        AppUpgradeDialog dialog = new AppUpgradeDialog();
        dialog.upgradeInfo = upgradeInfo;
        dialog.show(manager);
    }


    protected ImageView ivIcon;
    protected TextView tvTitle;
    protected TextView tvContent;
    protected TextView tvPositive;
    protected TextView tvNegative;

    RelativeLayout llProgress;
    ProgressBar mProgressBar;
    TextView tvProgress;


    //    protected OnButtonClickListener listener;
    private BaseDownloadTask downloadTask;
    public IUpgradeInfo upgradeInfo;
    private File apkFile;

    @Override
    protected int getLayoutResId() {
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
        HtmlUtil.setTextViewHtml(tvContent, upgradeInfo.getContent()); //支持html

        tvNegative.setVisibility(upgradeInfo.isForceUpgrade() == 1 ? GONE : VISIBLE);
        tvPositive.setVisibility(VISIBLE);
        tvPositive.setText(R.string.up_upgrade);
        tvNegative.setText(R.string.up_next_time);

        setDialogCancelable(false);
        setTouchOutCancel(false);
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
    public void onClick(View v) {
//        if (listener != null) {
        int i = v.getId();
        if (i == R.id.tvPositive) {
            if (apkFile != null) {
                IntentUtil.startInstall(getContext(), apkFile);
            } else {
                downloadApk();
            }
        } else if (i == R.id.tvNegative) {
            if (downloadTask != null) {
                downloadTask.pause();
                FileDownloader.getImpl().clear(downloadTask.getId(), downloadTask.getTargetFilePath());
            }
            dismiss();
        }
//        }
    }

//    public AppUpgradeDialog setOnButtonClickListener(OnButtonClickListener listener) {
//        this.listener = listener;
//        return this;
//    }
//
//    public interface OnButtonClickListener {
//
//        /**
//         * 当窗口按钮被点击
//         *
//         * @param dialog
//         * @param isPositiveClick true :PositiveButton点击, false :NegativeButton点击
//         */
//        void onButtonClick(AppUpgradeDialog dialog, boolean isPositiveClick);
//    }

    public void downloadApk() {
        llProgress.setVisibility(VISIBLE);
        tvPositive.setVisibility(GONE);
        tvNegative.setVisibility(VISIBLE);
        if (upgradeInfo.isForceUpgrade() != 1) {
            tvNegative.setText(R.string.up_cancel);
            tvNegative.setEnabled(true);
        } else {
            tvNegative.setText(R.string.up_downloading);
            tvNegative.setEnabled(false);
        }

        if (TextUtils.isEmpty(upgradeInfo.getApkUrl())) {
            ToastUtils.showShort(R.string.up_upgrade_failure);
            dismissAllowingStateLoss();
            return;
        }
        apkFile = null;
        String path = FileUtil.getAppCachePath(getContext()) + File.separator
                + "download" + File.separator + upgradeInfo.getVersionName() + ".apk";
        downloadTask = FileDownloader.getImpl()
                .create(upgradeInfo.getApkUrl())
                .setPath(path)
                .setForceReDownload(true)
                .setCallbackProgressTimes(100)
                .setMinIntervalUpdateSpeed(100)
                .setListener(new FileDownloadSampleListener() {
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int progress = (int) ((soFarBytes * 100.0) / totalBytes);
                        tvProgress.setText(String.format("%d%%", progress));
                        mProgressBar.setProgress(progress);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (!isAdded() || isDetached()) return;

                        tvProgress.setText(String.format("%d%%", 100));
                        tvNegative.setVisibility(GONE);
                        tvNegative.setEnabled(true);
                        tvPositive.setVisibility(VISIBLE);
                        tvPositive.setText(R.string.up_click_install);

                        apkFile = new File(path);
                        IntentUtil.startInstall(getContext(), apkFile);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        FileDownloader.getImpl().clear(task.getId(), path);
                        ToastUtils.showShort(R.string.up_download_failure);
                        LogUtil.e("下载失败:" + e);
                        dismissAllowingStateLoss();
                        e.printStackTrace();
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
