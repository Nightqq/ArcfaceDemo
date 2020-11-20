package com.arcsoft.arcfacedemo.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;

public class IpPort {

    //本地"192.168.0.214",8804,8082
    public static String local_ip = NetWorkUtils.getIP();
    public static int local_port = 8804;
    public static int local_Audio_port = 8082;
    //远端"192.168.0.219",8805,8083
    public static String distal_ip = "192.168.0.219";
    public static int distal_port = 8805;
    public static int distal_Audio_port = 8083;

    public static void init(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifiInfo.isConnected()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            local_ip = intToIp(ipAddress);
            if (local_ip.equals("192.168.0.160")) {
                local_port = 8804;
                local_Audio_port = 8082;
                distal_ip = "192.168.0.219";
                distal_port = 8805;
                distal_Audio_port = 8083;
            } else if (local_ip.equals("192.168.0.219")) {
                local_port = 8805;
                local_Audio_port = 8083;
                //远端"192.168.0.219",8805,10001
                distal_ip = "192.168.0.160";
                distal_port = 8804;
                distal_Audio_port = 8082;
            }


        }
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

   /* //本地"192.168.0.214",8804,8082
    public static String local_ip="192.168.0.219";
    public static int local_port=8805;
    public static int local_Audio_port=8083;
    //远端"192.168.0.219",8805,8083
    public static String distal_ip="192.168.0.160";
    public static int distal_port=8804;
    public static int distal_Audio_port=8082;*/
}
