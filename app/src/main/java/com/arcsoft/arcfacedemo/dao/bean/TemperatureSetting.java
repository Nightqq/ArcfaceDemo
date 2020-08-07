package com.arcsoft.arcfacedemo.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 测温设置类
 */
@Entity
public class TemperatureSetting {
    @Id
    private Long id;
    //30-35修改温度
    private String wen3035 = 0.3+"";
    //35-40修改温度
    private String wen3540 = 0.5+"";
    //40+修改温度
    private String wen40 = 0.8+"";
    //下线
    private String wenxia = 35.2+"";
    //上限
    private String wenshang = 37.3+"";
    //测温日志开关
    private boolean cewenrizhi = true;
    @Generated(hash = 13785779)
    public TemperatureSetting(Long id, String wen3035, String wen3540, String wen40,
            String wenxia, String wenshang, boolean cewenrizhi) {
        this.id = id;
        this.wen3035 = wen3035;
        this.wen3540 = wen3540;
        this.wen40 = wen40;
        this.wenxia = wenxia;
        this.wenshang = wenshang;
        this.cewenrizhi = cewenrizhi;
    }
    @Generated(hash = 324269416)
    public TemperatureSetting() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getWen3035() {
        return this.wen3035;
    }
    public void setWen3035(String wen3035) {
        this.wen3035 = wen3035;
    }
    public String getWen3540() {
        return this.wen3540;
    }
    public void setWen3540(String wen3540) {
        this.wen3540 = wen3540;
    }
    public String getWen40() {
        return this.wen40;
    }
    public void setWen40(String wen40) {
        this.wen40 = wen40;
    }
    public String getWenxia() {
        return this.wenxia;
    }
    public void setWenxia(String wenxia) {
        this.wenxia = wenxia;
    }
    public String getWenshang() {
        return this.wenshang;
    }
    public void setWenshang(String wenshang) {
        this.wenshang = wenshang;
    }
    public boolean getCewenrizhi() {
        return this.cewenrizhi;
    }
    public void setCewenrizhi(boolean cewenrizhi) {
        this.cewenrizhi = cewenrizhi;
    }
   

}
