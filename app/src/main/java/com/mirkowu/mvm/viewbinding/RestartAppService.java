package com.mirkowu.mvm.viewbinding;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.ShellUtils;

public class RestartAppService extends Service {
    /**关闭应用后多久重新启动*/
    private static  long stopDelayed=2000;
    private Handler handler;
    private String PackageName;
    public RestartAppService() {
        handler=new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        LogUtil.e("pm clear  onStartCommand ");
        stopDelayed=intent.getLongExtra("Delayed",2000);
        PackageName=intent.getStringExtra("PackageName");
        handler.postDelayed(()->{
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
            startActivity(LaunchIntent);
            RestartAppService.this.stopSelf();
        },stopDelayed);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 重启整个APP
     * @param context
     * @param Delayed 延迟多少毫秒
     */
    public static void restartAPP(Context context, long Delayed){
//        LogUtil.e("ShellUtils = "+ ShellUtils.execCmd("pm clear com.mirkowu.mvm",false).toString());

        /**开启一个新的服务，用来重启本APP*/
        Intent intent1=new Intent(context,RestartAppService.class);
        intent1.putExtra("PackageName",context.getPackageName());
        intent1.putExtra("Delayed",Delayed);
        context.startService(intent1);
        LogUtil.e("pm clear  restartAPP ");
        /**杀死整个进程**/
        android.os.Process.killProcess(android.os.Process.myPid());

    }
    /***重启整个APP*/
    public static void restartAPP(Context context){
//        restartAPP(context,2000);

        Intent intent2 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        //与平时Activity页面跳转一样可传递序列化数据,在Launch页面内获得
        intent2.putExtra("REBOOT","reboot");
        PendingIntent restartIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // 延时1秒重启
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
//        android.os.Process.killProcess(android.os.Process.myPid());
        LogUtil.e("ShellUtils = "+ ShellUtils.execCmd("pm clear com.mirkowu.mvm",false).toString());
    }
}
