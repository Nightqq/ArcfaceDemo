package com.arcsoft.arcfacedemo.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.arcsoft.arcfacedemo.util.utils.LogUtils;

public class UploadDataService extends Service {
    private UploadInfoUtil uploadInfoUtil;

    public UploadDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        uploadInfoUtil = new UploadInfoUtil(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            uploadInfo();//上传信息
        } catch (NullPointerException e) {
            LogUtils.a("上传信息NullPointerException");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void uploadInfo() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
