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
    //X中心坐标
    private String forehead_X =280+"";
    //X系数
    private String forehead_xx = 0.75+"";
    //Y中心坐标
    private String forehead_Y = 410+"";
    //Y系数
    private String forehead_Yx = 0.63+"";
    //下限
    private String wenxia = 35.2+"";
    //上限
    private String wenshang = 37.3+"";
    //测温日志开关
    private boolean cewenrizhi = true;
    @Generated(hash = 619599025)
    public TemperatureSetting(Long id, String forehead_X, String forehead_xx,
            String forehead_Y, String forehead_Yx, String wenxia, String wenshang,
            boolean cewenrizhi) {
        this.id = id;
        this.forehead_X = forehead_X;
        this.forehead_xx = forehead_xx;
        this.forehead_Y = forehead_Y;
        this.forehead_Yx = forehead_Yx;
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
    public String getForehead_X() {
        return this.forehead_X;
    }
    public void setForehead_X(String forehead_X) {
        this.forehead_X = forehead_X;
    }
    public String getForehead_xx() {
        return this.forehead_xx;
    }
    public void setForehead_xx(String forehead_xx) {
        this.forehead_xx = forehead_xx;
    }
    public String getForehead_Y() {
        return this.forehead_Y;
    }
    public void setForehead_Y(String forehead_Y) {
        this.forehead_Y = forehead_Y;
    }
    public String getForehead_Yx() {
        return this.forehead_Yx;
    }
    public void setForehead_Yx(String forehead_Yx) {
        this.forehead_Yx = forehead_Yx;
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
