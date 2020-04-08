package com.arcsoft.arcfacedemo.net.bean;

public class JsonPolicePhoto {


    /**
     * emp_id : 000001
     * dev_ip : 192.168.1.103
     * compare_time : 2020-04-03T15:05:27.948Z
     * emp_photo : baseString
     * compare_type : F
     */

    private String emp_id;

    public JsonPolicePhoto(String emp_id, String dev_ip, String compare_time, String emp_photo, String compare_type) {
        this.emp_id = emp_id;
        this.dev_ip = dev_ip;
        this.compare_time = compare_time;
        this.emp_photo = emp_photo;
        this.compare_type = compare_type;
    }

    private String dev_ip;
    private String compare_time;
    private String emp_photo;
    private String compare_type;

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getDev_ip() {
        return dev_ip;
    }

    public void setDev_ip(String dev_ip) {
        this.dev_ip = dev_ip;
    }

    public String getCompare_time() {
        return compare_time;
    }

    public void setCompare_time(String compare_time) {
        this.compare_time = compare_time;
    }

    public String getEmp_photo() {
        return emp_photo;
    }

    public void setEmp_photo(String emp_photo) {
        this.emp_photo = emp_photo;
    }

    public String getCompare_type() {
        return compare_type;
    }

    public void setCompare_type(String compare_type) {
        this.compare_type = compare_type;
    }
}
