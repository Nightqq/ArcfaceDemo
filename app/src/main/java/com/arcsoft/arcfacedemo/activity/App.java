package com.arcsoft.arcfacedemo.activity;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.arcsoft.arcfacedemo.dao.helper.DaoUtils;
import com.arcsoft.arcfacedemo.util.server.handler.CrashHandler;
import com.arcsoft.arcfacedemo.util.server.server.RemindService;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;

public class App extends Application {

    public static byte[] byteface;//当前准备识别的干警特征值
    public static String police_name = "";//当前准备识别的干警姓名
    public static String policeNum = "";//当前准备识别的干警刷卡卡号
    public static boolean iSFinish = true;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化ANR检测工具
        // BlockCanary.install(this, new AppBlockCanaryContext()).start();

        // MethodTimeManager.getInstance().setEnable(true);


        Utils.init(App.this);
        //IpPort.init(App.this);
        DaoUtils.init(App.this);
        CrashHandler.getInstance().init();
        Intent intent = new Intent(this, RemindService.class);
        startService(intent);
    }

    public void getcontest() {
        Context baseContext = getBaseContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //MethodTimeManager.getInstance().setEnable(false);
        PackageManager packageManager = this.getPackageManager();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage("com.arcsoft.arcfacedemo");
        if (launchIntentForPackage != null) {
            LogUtils.a("自启动开始");
            this.startActivity(launchIntentForPackage);
        } else {
            LogUtils.a("launchIntentForPackage空");
        }

        LogUtils.a("onTrimMemory");
    }

    public static void castMemory() {
        byteface = null;
        police_name = "";
    }

    @Override // 程序在内存清理的时候执行
    public void onTrimMemory(int level) {
       /* packageName = getPackageName();
        SharedPreferences qq = getApplicationContext().getSharedPreferences("qq", Context.MODE_PRIVATE);
        boolean startself = qq.getBoolean("start_set_2", false);
        if (startself) {
            AppCloseLister.startoneself(Utils.getContext());
        }*/

        super.onTrimMemory(level);
    }

}
