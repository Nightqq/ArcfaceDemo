package com.arcsoft.arcfacedemo.util.server.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;

/**
 * Created by Administrator on 2018/6/12.
 */

public class StartselfBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConfigUtil.getstartself() && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            PackageManager pm = context.getPackageManager();    //包管理者
            //意图
            Intent it = pm.getLaunchIntentForPackage("com.arcsoft.arcfacedemo");   //值为应用的包名
            if (null != it) {
                context.startActivity(it);//启动意图
            }
        }
    }
}
