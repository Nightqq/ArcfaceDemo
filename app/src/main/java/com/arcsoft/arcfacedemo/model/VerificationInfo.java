package com.arcsoft.arcfacedemo.model;

import com.alibaba.fastjson.annotation.JSONField;

public class VerificationInfo{
    @JSONField(name = "equipmentID")
    private int equipmentID;

    @JSONField(name = "equipmentVerificationID")
    private String equipmentVerificationID;

    @JSONField(name = "personcode")
    private String personcode;

    @JSONField(name = "verificationType")
    private int verificationType;

    @JSONField(name = "checkTime")
    private long checkTime;

    @JSONField(name = "imageName")
    private String imageName;

    @JSONField(name = "recognitionName")
    private String recognitionName;

    public int getEquipmentID() {
        return equipmentID;
    }

    public String getEquipmentVerificationID() {
        return equipmentVerificationID;
    }

    public String getPersoncode() {
        return personcode;
    }

    public int getVerificationType() {
        return verificationType;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public String getImageName() {
        return imageName;
    }

    public String getRecognitionName() {
        return recognitionName;
    }

    public void setEquipmentID(int equipmentID) {
        this.equipmentID = equipmentID;
    }

    public void setEquipmentVerificationID(String equipmentVerificationID) {
        this.equipmentVerificationID = equipmentVerificationID;
    }

    public void setPersoncode(String personcode) {
        this.personcode = personcode;
    }

    public void setVerificationType(int verificationType) {
        this.verificationType = verificationType;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setRecognitionName(String recognitionName) {
        this.recognitionName = recognitionName;
    }

}
