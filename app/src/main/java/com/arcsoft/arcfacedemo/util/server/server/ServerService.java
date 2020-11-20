package com.arcsoft.arcfacedemo.util.server.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.arcsoft.arcfacedemo.util.server.handler.CallRollHandler;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.server.handler.ConnectHandler;
import com.arcsoft.arcfacedemo.util.server.handler.PersonAddHandler;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.filter.HttpCacheFilter;

import org.apache.httpcore.util.NetUtils;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;

public class ServerService extends Service {
    public String ip = NetWorkUtils.getIP();
    public static int port = 3639;
    public static String Call_roll_post = "Call/Roll";
    public static String person_add_post = "/person/add";

    //更新干警数据
    public static String updatePoliceFeature="/CALL/api/RollCallController/updatePoliceFeature";
    //添加外来人员
    public static String addOutsidersFeature="/CALL/api/RollCallController/addOutsidersFeature";
    private Server server;


    public void onCreate() {
        server = AndServer.serverBuilder()
                .inetAddress(NetWorkUtils.getLocalIPAddress())  //服务器要监听的网络地址
                .port(port) //服务器要监听的端口
                .timeout(10, TimeUnit.SECONDS) //Socket超时时间
                .registerHandler(Call_roll_post, new CallRollHandler()) //注册点名接口
                //.registerHandler(person_add_post, new PersonAddHandler()) //注册一个人员添加接口
                .filter(new HttpCacheFilter()) //开启缓存支持
                .listener(new Server.ServerListener() {  //服务器监听接口
                    @Override
                    public void onStarted() {
                        String hostAddress = server.getInetAddress().getHostAddress();
                        LogUtils.a(TAG, "onStarted : " + hostAddress);
                        ServerPresenter.onServerStarted(ServerService.this, hostAddress);
                    }

                    @Override
                    public void onStopped() {
                        LogUtils.a(TAG, "onStopped");
                        ServerPresenter.onServerStopped(ServerService.this);
                    }

                    @Override
                    public void onError(Exception e) {
                        LogUtils.a(TAG, "onError : " + e.getMessage());
                        ServerPresenter.onServerError(ServerService.this, e.getMessage());
                    }
                })
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.a("onStartCommand");
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }
    private void startServer() {
        LogUtils.a("startServer");
        //如果Server已启动则不再重复启动，此时只是向外发布已启动的状态
        if (server.isRunning()) {
            LogUtils.a("Server已启动");
            InetAddress inetAddress = server.getInetAddress();
            if (inetAddress != null) {
                String hostAddress = inetAddress.getHostAddress();
                if (!TextUtils.isEmpty(hostAddress)) {
                    ServerPresenter.onServerStarted(ServerService.this, hostAddress);
                }
            }
        } else {
            server.startup();
        }
    }
    private void stopServer() {
        if (server != null && server.isRunning()) {
            server.shutdown();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
