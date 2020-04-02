package com.arcsoft.arcfacedemo.net;

public class UrlConfig {
    private static UrlConfig urlConfig;
    public static UrlConfig getinstance() {
        if (urlConfig == null) {
            urlConfig = new UrlConfig();
        }
        return urlConfig;
    }
    public String getPoliceFaceUrl(){
        return "http://192.168.0.10/CALL/api/RollCallController/queryEmpFeature";
    }

    public String getPoliceFaceListUrl(){
        return "http://192.168.0.10/CALL/api/RollCallController/getEmpFeature";
    }

    public String uploauploadPolicephotoUrl(){//上传干警识别照片
        return "http://192.168.0.10/CALL/api/RollCallController/addEmpCompare";
    }

}
