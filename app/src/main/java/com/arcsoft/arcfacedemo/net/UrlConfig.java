package com.arcsoft.arcfacedemo.net;

import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;

public class UrlConfig {
    private static UrlConfig urlConfig;
    public static UrlConfig getinstance() {
        if (urlConfig == null) {
            urlConfig = new UrlConfig();
        }
        return urlConfig;
    }



    public String getServerIP(){
        TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
        String serverIP = terminalInformation.getServerIP();
        String serverPost = terminalInformation.getServerPost();
        if (serverIP!=null){
            if (serverPost!=null&&serverPost.length()>0){
                return serverIP+":"+serverPost;
            }else {
                return serverIP;
            }
        }else {
            return "192.168.0.10";
        }
    }

    public String getPoliceFaceUrl(){//查询人员特征值
        return "http://192.168.0.10/CALL/api/RollCallController/queryEmpFeature";
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
    public String getLastVersion(){//获取最新版本信息
        return  "http://"+getServerIP()+"/CALL/api/DictionaryController/getLastVersion";
    }
    public String downloadApk(){
        return "http://"+getServerIP()+"/download/"+ ConfigUtil.Apk_name;
    }

    public String requestADong(){
        return "http://"+getServerIP()+"/CallRoll";
    }

    public String updateCriminalTemperature(){//上传温度--东
        //return "http://192.168.0.89:8001/CallRoll/";
        return "http://"+getServerIP()+"/CallRoll/";
    }



}
