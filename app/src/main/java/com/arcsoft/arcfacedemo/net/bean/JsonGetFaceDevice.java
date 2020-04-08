package com.arcsoft.arcfacedemo.net.bean;

import java.util.List;

public class JsonGetFaceDevice {
    /**
     * success : true
     * source : [{"dev_id":1,"dev_code":"0000407","dev_name":"805测试终端","dev_ip":"192.168.0.144","dev_port":3639,"dev_serial":"241916908c18b281","valid":2},{"dev_id":2,"dev_code":"969685","dev_name":"凤凰城","dev_ip":"192.168.0.254","dev_port":3639,"dev_serial":"12121212121","valid":2}]
     */

    private boolean success;
    private List<SourceBean> source;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SourceBean> getSource() {
        return source;
    }

    public void setSource(List<SourceBean> source) {
        this.source = source;
    }

    public static class SourceBean {
        /**
         * dev_id : 1
         * dev_code : 0000407
         * dev_name : 805测试终端
         * dev_ip : 192.168.0.144
         * dev_port : 3639
         * dev_serial : 241916908c18b281
         * valid : 2
         */

        private int dev_id;
        private String dev_code;
        private String dev_name;
        private String dev_ip;
        private int dev_port;
        private String dev_serial;
        private int valid;

        public int getDev_id() {
            return dev_id;
        }

        public void setDev_id(int dev_id) {
            this.dev_id = dev_id;
        }

        public String getDev_code() {
            return dev_code;
        }

        public void setDev_code(String dev_code) {
            this.dev_code = dev_code;
        }

        public String getDev_name() {
            return dev_name;
        }

        public void setDev_name(String dev_name) {
            this.dev_name = dev_name;
        }

        public String getDev_ip() {
            return dev_ip;
        }

        public void setDev_ip(String dev_ip) {
            this.dev_ip = dev_ip;
        }

        public int getDev_port() {
            return dev_port;
        }

        public void setDev_port(int dev_port) {
            this.dev_port = dev_port;
        }

        public String getDev_serial() {
            return dev_serial;
        }

        public void setDev_serial(String dev_serial) {
            this.dev_serial = dev_serial;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }
    }
}
