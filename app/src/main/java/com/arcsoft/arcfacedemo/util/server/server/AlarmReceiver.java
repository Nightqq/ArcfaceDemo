package com.arcsoft.arcfacedemo.util.server.server;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.arcsoft.arcfacedemo.activity.arcface.FaceRecognitionActivity;
import com.arcsoft.arcfacedemo.activity.arcface.LogoActivity;
import com.arcsoft.arcfacedemo.activity.thermometry.ThermometryActivity;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //重启应用
        LogUtils.a("每天定时清理缓存开启");
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("每天定时清理缓存开启");
        Intent intent1 = new Intent(context.getApplicationContext(), ThermometryActivity.class);
        intent1.putExtra(ThermometryActivity.TAG_EXIT, ThermometryActivity.TAG_RESTART);
        context.getApplicationContext().startActivity(intent1);
    }
   /* private void restartApp(){
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
