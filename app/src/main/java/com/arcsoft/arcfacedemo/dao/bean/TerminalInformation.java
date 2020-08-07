package com.arcsoft.arcfacedemo.dao.bean;


import com.alibaba.fastjson.annotation.JSONField;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 终端信息类
 */
@Entity
public class TerminalInformation {


    @Id
    private Long id;
    //本机编号
    private String TerminalNum = "ZX0000";
    //设备名称
    private String TerminalName = "前端设备";
    //设备串口号
    private String Serial = DeviceUtils.getAndroidID();
    //服务器IP地址
    private String ServerIP = "192.168.0.10";
    //本地ip
    private String DeviceIP = NetWorkUtils.getIP();
    //本地端口
    private String DevicePost = "3639";
    //服务器端口
    private String ServerPost = "";
    //是否注册标准
    private boolean isregister = false;

    //识别阈值
    private float RecognitionThreshold = (float) 0.8;
    //识别超时时间
    private long OutTime = 3000;
    //识别失败次数
    private int RecognitionNum = 3;
    //设置密码
    private String SettingPassword = "123456";

    @Generated(hash = 226124461)
    public TerminalInformation(Long id, String TerminalNum, String TerminalName, String Serial,
            String ServerIP, String DeviceIP, String DevicePost, String ServerPost, boolean isregister,
            float RecognitionThreshold, long OutTime, int RecognitionNum, String SettingPassword) {
        this.id = id;
        this.TerminalNum = TerminalNum;
        this.TerminalName = TerminalName;
        this.Serial = Serial;
        this.ServerIP = ServerIP;
        this.DeviceIP = DeviceIP;
        this.DevicePost = DevicePost;
        this.ServerPost = ServerPost;
        this.isregister = isregister;
        this.RecognitionThreshold = RecognitionThreshold;
        this.OutTime = OutTime;
        this.RecognitionNum = RecognitionNum;
        this.SettingPassword = SettingPassword;
    }

    @Generated(hash = 1102275499)
    public TerminalInformation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerminalNum() {
        return this.TerminalNum;
    }

    public void setTerminalNum(String TerminalNum) {
        this.TerminalNum = TerminalNum;
    }

    public String getServerIP() {
        return this.ServerIP;
    }

    public void setServerIP(String ServerIP) {
        this.ServerIP = ServerIP;
    }

    public String getServerPost() {
        return this.ServerPost;
    }

    public void setServerPost(String ServerPost) {
        this.ServerPost = ServerPost;
    }

    public float getRecognitionThreshold() {
        return this.RecognitionThreshold;
    }

    public void setRecognitionThreshold(float RecognitionThreshold) {
        this.RecognitionThreshold = RecognitionThreshold;
    }

    public long getOutTime() {
        return this.OutTime;
    }

    public void setOutTime(long OutTime) {
        this.OutTime = OutTime;
    }

    public int getRecognitionNum() {
        return this.RecognitionNum;
    }

    public void setRecognitionNum(int RecognitionNum) {
        this.RecognitionNum = RecognitionNum;
    }

    public String getSettingPassword() {
        return this.SettingPassword;
    }

    public void setSettingPassword(String SettingPassword) {
        this.SettingPassword = SettingPassword;
    }

    public String getTerminalName() {
        return this.TerminalName;
    }

    public void setTerminalName(String TerminalName) {
        this.TerminalName = TerminalName;
    }

    public String getSerial() {
        return this.Serial;
    }

    public void setSerial(String Serial) {
        this.Serial = Serial;
    }

    public boolean getIsregister() {
        return this.isregister;
    }

    public void setIsregister(boolean isregister) {
        this.isregister = isregister;
    }

    public String getDeviceIP() {
        return this.DeviceIP;
    }

    public void setDeviceIP(String DeviceIP) {
        this.DeviceIP = DeviceIP;
    }

    public String getDevicePost() {
        return this.DevicePost;
    }

    public void setDevicePost(String DevicePost) {
        this.DevicePost = DevicePost;
    }
}
