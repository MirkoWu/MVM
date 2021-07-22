//package com.mirkowu.mvm;
//
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.SpannableStringBuilder;
//import android.text.TextPaint;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.Gravity;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//
//import com.mirkowu.lib_widget.dialog.CommonHintDialog;
//import com.mirkowu.planlife.api.HostUrl;
//import com.mirkowu.planlife.ui.WebActivity;
//
///**
// * @author: mirko
// * @date: 20-4-1
// */
//public class ServicePrivacyDialog extends CommonHintDialog {
//
//    public static CommonHintDialog newInstance() {
//        return new ServicePrivacyDialog()
//                .setTitle("服务协议和隐私政策")
//                .setNegativeButton("不同意")
//                .setPositiveButton("同意")
//                .setDialogCancelable(false);
//    }
//
//    private SpannableString link1;
//    private SpannableString link2;
//    private boolean isRefuseOnce = false;
//
//    @Override
//    protected void initialize() {
//        super.initialize();
//        link1 = new SpannableString("《计划人生用户服务协议》");
//        ClickableSpan link1ClickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                WebActivity.start(getContext(), "计划人生用户服务协议", HostUrl.SERVICE_POLICY);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            }
//
//
//        };
//
//        link1.setSpan(link1ClickableSpan, 0, link1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        link2 = new SpannableString("《计划人生用户隐私政策》");
//        ClickableSpan link2ClickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                WebActivity.start(getContext(), "计划人生用户隐私政策", HostUrl.PRIVACY_POLICY);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            }
//        };
//
//        link2.setSpan(link2ClickableSpan, 0, link2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        SpannableStringBuilder style = new SpannableStringBuilder();
//        style.append("为了更好地保护您的隐私和账户安全，计划人生依据国家相关法律规定制定了");
//        style.append(link1);
//        style.append("和");
//        style.append(link2);
//        style.append("。");
//        style.append("\n");
//        style.append("\n");
//        style.append("请您务必审慎阅读、充分理解各条款。如您同意，请点击“同意”开始接受我们的服务。");
//        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
//        tvContent.setText(style);
//        tvContent.setVisibility(View.VISIBLE);
//        tvContent.setGravity(Gravity.LEFT);
//        tvContent.setLineSpacing(8f, 1f);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (listener != null) {
//            int i = v.getId();
//            if (i == R.id.tvPositive) {
//                dismissAllowingStateLoss();
//                listener.onButtonClick(this, true);
//            } else if (i == R.id.tvNegative) {
//                if (!isRefuseOnce) {
//                    isRefuseOnce = true;
//                    updateUI();
//                } else {
//                    dismissAllowingStateLoss();
//                    listener.onButtonClick(this, false);
//                }
//            }
//        }
//
//    }
//
//    private void updateUI() {
//        SpannableStringBuilder style = new SpannableStringBuilder();
//        style.append("计划人生将严格按照");
//        style.append(link1);
//        style.append("和");
//        style.append(link2);
//        style.append("为您提供服务。");
//        style.append("\n");
//        style.append("\n");
//        style.append("如您同意，请点击“同意”开始接受我们的服务。");
//        style.append("\n");
//        style.append("\n");
//        style.append("如您仍不同意以上协议和政策，我们将无法继续为您提供服务。");
//        tvContent.setText(style);
//        tvNegative.setText("不同意并退出");
//    }
//}
