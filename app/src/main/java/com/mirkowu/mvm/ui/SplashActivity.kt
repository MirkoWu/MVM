package com.mirkowu.mvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_widget.TimerTextView
import com.mirkowu.mvm.MainActivity
import com.mirkowu.mvm.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //解决系统bug，检测启动页是否在栈底，防止重复启动界面
        if (!isTaskRoot()) {
            finish();
            return;
        }
        LogUtil.d("点击 跳过 SplashActivity onCreate");
        setContentView(R.layout.activity_splash)

        val timer = findViewById<TimerTextView>(R.id.mTimer)
        timer.timerDuration = 3
//        timer.timerHintText = "跳过"
//        timer.timerFormatText = "跳过%d"
//        timer.timerFinishText = "跳过"
        timer.setEnableWhenCount(true)
        timer.setOnTimerListener { skip() }
        timer.click {
            LogUtil.d("点击 跳过")
            timer.cancel();
            skip()
        }
        timer.start()

        //WebView初始化多进程
       // WebViewUtil.init(application, true);

    }

    private fun skip() {
        MainActivity.start(this)
        finish()
//        overridePendingTransition(0,0)
    }
}