package com.arcsoft.arcfacedemo.net;

public class UrlConfig {
    private static UrlConfig urlConfig;
    public static UrlConfig getinstance() {
        if (urlConfig == null) {
            urlConfig = new UrlConfig();
        }
        return urlConfig;
    }

    public String getServerIP(){
        return "192.168.0.10";
    }

    public String getPoliceFaceUrl(){//查询人员特征值
        return "http://"+getServerIP()+"/CALL/api/RollCallController/queryEmpFeature";
    }

    public String getPoliceFaceListUrl(){//多条件查询人员特征值
        return "http://"+getServerIP()+"/CALL/api/RollCallController/getEmpFeature";
    }

    public String uploauploadPolicephotoUrl(){//上传干警识别照片
        return "http://"+getServerIP()+"/CALL/api/RollCallController/addEmpCompare";
    }
    public String addFaceDevice(){//增加人像比对机器
        return  "http://"+getServerIP()+"/CALL/api/DictionaryController/addFaceDevice";
    }
    public String updateFaceDevice(){//修改人像比对机器
        return  "http://"+getServerIP()+"/CALL/api/DictionaryController/updateFaceDevice";
    }
    public String delFaceDevice(){//删除人像比对机器
        return  "http://"+getServerIP()+"/CALL/api/DictionaryController/delFaceDevice";
    }
    public String getFaceDevice(){//获取人像比对机器
        return  "http://"+getServerIP()+"/CALL/api/DictionaryController/getFaceDevice";
    }
}
