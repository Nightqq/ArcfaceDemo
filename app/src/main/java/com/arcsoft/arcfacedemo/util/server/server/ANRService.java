package com.arcsoft.arcfacedemo.util.server.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arcsoft.arcfacedemo.util.utils.LogUtils;

public class ANRService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("gac","ANRService");
        exception();
    }
    private int lasttick,mTick;//两次计数器的值
    private Handler mHandler = new Handler();
    private boolean flag = true;
    private void exception(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
                    lasttick = mTick;
                    mHandler.post(tickerRunnable);//向主线程发送消息 计数器值+1
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mTick == lasttick){
                        flag = false;
                        LogUtils.e("gac","anr happned in here");
                        handleAnrError();
                    }
                }
            }
        }).start();
    }

    //发生anr的时候，在此处写逻辑
    private void handleAnrError(){

    }
    private final Runnable tickerRunnable = new Runnable() {
        @Override public void run() {
            mTick = (mTick + 1) % 10;
        }
    };
}
