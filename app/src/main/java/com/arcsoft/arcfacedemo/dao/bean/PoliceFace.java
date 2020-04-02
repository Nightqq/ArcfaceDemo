package com.arcsoft.arcfacedemo.dao.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class PoliceFace {

    @Id
    private String EMP_ID;//主键卡号


    private String EMP_NAME="";
    private int EMP_TYPE=1;
    private String EMP_FEATURE="";//base64加密的特征值
    private int VALID=1;
    private String SFZ="";
    private String TQSJ="";
    private String TQZP="";

    @Generated(hash = 1725337394)
    public PoliceFace(String EMP_ID, String EMP_NAME, int EMP_TYPE,
            String EMP_FEATURE, int VALID, String SFZ, String TQSJ, String TQZP) {
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.EMP_TYPE = EMP_TYPE;
        this.EMP_FEATURE = EMP_FEATURE;
        this.VALID = VALID;
        this.SFZ = SFZ;
        this.TQSJ = TQSJ;
        this.TQZP = TQZP;
    }
    @Generated(hash = 1378037418)
    public PoliceFace() {
    }




    public String getEMP_ID() {
        return this.EMP_ID;
    }
    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }
    public String getEMP_NAME() {
        return this.EMP_NAME;
    }
    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }
    public int getEMP_TYPE() {
        return this.EMP_TYPE;
    }
    public void setEMP_TYPE(int EMP_TYPE) {
        this.EMP_TYPE = EMP_TYPE;
    }
    public String getEMP_FEATURE() {
        return this.EMP_FEATURE;
    }
    public void setEMP_FEATURE(String EMP_FEATURE) {
        this.EMP_FEATURE = EMP_FEATURE;
    }
    public int getVALID() {
        return this.VALID;
    }
    public void setVALID(int VALID) {
        this.VALID = VALID;
    }
    public String getSFZ() {
        return this.SFZ;
    }
    public void setSFZ(String SFZ) {
        this.SFZ = SFZ;
    }
    public String getTQSJ() {
        return this.TQSJ;
    }
    public void setTQSJ(String TQSJ) {
        this.TQSJ = TQSJ;
    }
    public String getTQZP() {
        return this.TQZP;
    }
    public void setTQZP(String TQZP) {
        this.TQZP = TQZP;
    }

    @Override
    public String toString() {
        return "PoliceFace{" +
                "EMP_ID='" + EMP_ID + '\'' +
                ", EMP_NAME='" + EMP_NAME + '\'' +
                ", EMP_TYPE=" + EMP_TYPE +
                ", EMP_FEATURE='" + EMP_FEATURE + '\'' +
                ", VALID=" + VALID +
                ", SFZ='" + SFZ + '\'' +
                ", TQSJ='" + TQSJ + '\'' +
                ", TQZP='" + TQZP + '\'' +
                '}';
    }
}
