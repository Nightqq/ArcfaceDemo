package com.arcsoft.arcfacedemo.dao.bean;

import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CeWenInform {

    @Id
    private String ip= NetWorkUtils.getIP();
    private String name="张三";
    private String emp_id="2859CD";
    private String temperature="36.5";
    private String photo="#adkjflakf";
    private String state="未知";
    private String time="65464646464";
    @Generated(hash = 1313765761)
    public CeWenInform(String ip, String name, String emp_id, String temperature,
            String photo, String state, String time) {
        this.ip = ip;
        this.name = name;
        this.emp_id = emp_id;
        this.temperature = temperature;
        this.photo = photo;
        this.state = state;
        this.time = time;
    }
    @Generated(hash = 2145803294)
    public CeWenInform() {
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmp_id() {
        return this.emp_id;
    }
    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }
    public String getTemperature() {
        return this.temperature;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
