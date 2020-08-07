package com.arcsoft.arcfacedemo.activity;

import android.app.Application;
import android.content.Intent;

import com.arcsoft.arcfacedemo.dao.helper.DaoUtils;
import com.arcsoft.arcfacedemo.util.server.handler.CrashHandler;
import com.arcsoft.arcfacedemo.util.server.server.RemindService;
import com.arcsoft.arcfacedemo.util.utils.Utils;

public class App extends Application{

    public static byte[] byteface;//当前准备识别的干警特征值
    public static String police_name="";//当前准备识别的干警姓名


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(App.this);
        //IpPort.init(App.this);
        DaoUtils.init(App.this);
      //  CrashHandler.getInstance().init();
        Intent intent = new Intent(this, RemindService.class);
        startService(intent);
    }


    public static void castMemory(){
        byteface=null;
        police_name="";
    }
}
