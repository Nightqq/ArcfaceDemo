package com.arcsoft.arcfacedemo.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class FaceRecordInfo {
    @JSONField(name = "verificationList")
    private List<VerificationInfo> verificationList;

    public List<VerificationInfo> getVerificationList() {
        return verificationList;
    }

    public void setVerificationList(List<VerificationInfo> verificationList) {
        this.verificationList = verificationList;
    }
}
