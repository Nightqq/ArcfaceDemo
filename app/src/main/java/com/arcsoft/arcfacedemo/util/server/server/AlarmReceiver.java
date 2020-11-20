package com.arcsoft.arcfacedemo.util.server.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //重启应用
        //LogUtils.a("每天定时清理缓存开启");
        FileUtils.getFileUtilsHelp().saveupdatehelp(" 定时重启开始");
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("每天定时更新开启");
      /*  Intent intent1 = new Intent(context.getApplicationContext(), ThermometryActivity.class);
        intent1.putExtra(ThermometryActivity.TAG_EXIT, ThermometryActivity.TAG_RESTART);
        context.getApplicationContext().startActivity(intent1);*/
       /* Intent intent2 = Utils.getContext().getPackageManager()
                .getLaunchIntentForPackage(Utils.getContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(Utils.getContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);*/
        restartApp();
    }
    private void restartApp(){
        /**开启一个新的服务，用来重启本APP*/
        Intent intent1=new Intent(Utils.getContext(),killSelfService.class);
        intent1.putExtra("PackageName",Utils.getContext().getPackageName());
        intent1.putExtra("Delayed",200);
        Utils.getContext().startService(intent1);
        /**杀死整个进程**/
        android.os.Process.killProcess(android.os.Process.myPid());
    }


 /*   private void restartApp(){
        Intent intent = new Intent(Utils.getContext(), LogoActivity.class);
        AlarmManager mAlarmManager = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
        //重启应用，得使用PendingIntent
        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                Utils.getContext().getApplicationContext(), 0, intent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android6.0以上，包含6.0
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); //解决Android6.0省电机制带来的不准时问题
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Android4.4到Android6.0之间，包含4.4
            mAlarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 解决set()在api19上不准时问题
        } else {
            mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        }
    }*/

}
