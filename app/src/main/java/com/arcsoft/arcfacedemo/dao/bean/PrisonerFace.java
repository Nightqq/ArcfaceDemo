package com.arcsoft.arcfacedemo.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 罪犯数据类
 * */
@Entity
public class PrisonerFace {
    @Id
    private String emp_id;
    private String emp_name;
    private String emp_feature;

    private int call_roll_result=0;
    private String photo="";

    @Generated(hash = 1264830973)
    public PrisonerFace(String emp_id, String emp_name, String emp_feature,
            int call_roll_result, String photo) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.emp_feature = emp_feature;
        this.call_roll_result = call_roll_result;
        this.photo = photo;
    }
    @Generated(hash = 1746399204)
    public PrisonerFace() {
    }

    public PrisonerFace(String emp_id, String emp_name, String emp_feature) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.emp_feature = emp_feature;
        this.call_roll_result = 0;
        this.photo = "";
    }


    public String getEmp_id() {
        return this.emp_id;
    }
    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }
    public String getEmp_name() {
        return this.emp_name;
    }
    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }
    public String getEmp_feature() {
        return this.emp_feature;
    }
    public void setEmp_feature(String emp_feature) {
        this.emp_feature = emp_feature;
    }
    public int getCall_roll_result() {
        return this.call_roll_result;
    }
    public void setCall_roll_result(int call_roll_result) {
        this.call_roll_result = call_roll_result;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
