package com.arcsoft.arcfacedemo.net.bean;

import com.arcsoft.arcfacedemo.dao.bean.PrisonerFace;

import java.util.List;

public class JsonCallRoll {

    /**
     * code : 001
     * call_roll_mode : 0
     * data : [{"emp_name":"张三","emp_id":"2859CD","emp_feature":"AAD6RAA8"},{"emp_name":"李四","emp_id":"2859CD","emp_feature":"AADIOW8"}]
     */

    private String code;
    private int call_roll_mode;
    private List<PrisonerFace> prisonerFace;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCall_roll_mode() {
        return call_roll_mode;
    }

    public void setCall_roll_mode(int call_roll_mode) {
        this.call_roll_mode = call_roll_mode;
    }

    public List<PrisonerFace> getPrisonerFaceList() {
        return prisonerFace;
    }

    public void setPrisonerFaceList(List<PrisonerFace> prisonerFace) {
        this.prisonerFace = prisonerFace;
    }


}
