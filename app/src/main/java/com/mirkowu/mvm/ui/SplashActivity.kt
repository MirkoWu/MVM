package com.mirkowu.mvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_widget.TimerTextView
import com.mirkowu.mvm.MainActivity
import com.mirkowu.mvm.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val timer = findViewById<TimerTextView>(R.id.mTimer)
//        timer.timerDuration = 3
//        timer.timerHintText = "跳过"
//        timer.timerFormatText = "跳过%d"
//        timer.timerFinishText = "跳过"
        timer.setEnableWhenCount(true)
        timer.setOnTimerListener { skip() }
        timer.click {
            timer.cancel();
            skip()
        }
        timer.start()


        //Handler().postDelayed({  }, 1000)
    }

    private fun skip() {
        MainActivity.start(this)
        finish()
    }
}