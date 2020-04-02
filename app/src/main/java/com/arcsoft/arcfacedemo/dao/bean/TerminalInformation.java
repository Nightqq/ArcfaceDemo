package com.arcsoft.arcfacedemo.dao.bean;


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
    //服务器IP地址
    private String ServerIP = "192.168.0.10";
    //服务器端口
    private String ServerPost = "";
    //识别阈值
    private float RecognitionThreshold = (float) 0.8;
    //识别超时时间
    private long OutTime = 5000;
    //识别失败次数
    private int RecognitionNum = 5;
    //设置密码
    private String SettingPassword = "123456";
    @Generated(hash = 1766289712)
    public TerminalInformation(Long id, String TerminalNum, String ServerIP,
            String ServerPost, float RecognitionThreshold, long OutTime,
            int RecognitionNum, String SettingPassword) {
        this.id = id;
        this.TerminalNum = TerminalNum;
        this.ServerIP = ServerIP;
        this.ServerPost = ServerPost;
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
}
