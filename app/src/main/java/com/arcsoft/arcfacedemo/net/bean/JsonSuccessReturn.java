package com.arcsoft.arcfacedemo.net.bean;

public class JsonSuccessReturn {


    /**
     * success : true
     * msg : 更新成功
     */

    private boolean success;
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
