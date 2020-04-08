package com.arcsoft.arcfacedemo.net.bean;

public class JsonSuccessReturn {
    /**
     * success : false
     * source : 前端机器编号或ip或串口号已存在!
     */

    private boolean success;
    private String source;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
