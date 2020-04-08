package com.arcsoft.arcfacedemo.net;

import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.PoliceFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.net.bean.JsonGetFaceDevice;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPolicePhoto;
import com.arcsoft.arcfacedemo.net.bean.JsonSuccessReturn;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.CallBackUtil;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.OkhttpUtil;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


public class RequestHelper {

    private static RequestHelper requestHelper;

    private RequestHelper() {
    }

    public static RequestHelper getRequestHelper() {
        if (requestHelper == null) {
            requestHelper = new RequestHelper();
        }
        return requestHelper;
    }

    //根据卡号获取人脸
    public void getPoliceFace(String cardNumber,OpenDownloadListener openCallPoliceListener) {
        Map<String, String> params = new HashMap<>();
        params.put("emp_id", cardNumber);
        params.put("userid", "1");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getPoliceFaceUrl(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                LogUtils.a("请求返回数据", "onParseResponse");
                try {
                    String string = response.body().string().replaceAll(" ", "").replaceAll("\r|\n","");
                    LogUtils.a("请求返回数据response", string);
                    JsonPoliceFace jsonPoliceFace = JSON.parseObject(string, JsonPoliceFace.class);
                    PoliceFace sourceBean = jsonPoliceFace.getSource().getData().get(0);
                    App.byteface= SwitchUtils.base64tobyte(sourceBean.getEMP_FEATURE());
                    App.police_name=sourceBean.getEMP_NAME();
                    openCallPoliceListener.openDownload("下载成功");
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
                return null;
            }
            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("请求返回数据", "onFailure");
                openCallPoliceListener.openDownload("onFailure");
            }

            @Override
            public void onResponse(Object response) {
                LogUtils.a("请求返回数据", "onResponse");
            }
        });
    }

    public void getAllPoliceFace(OpenDownloadListener openDownloadListener) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "1");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getPoliceFaceUrl(),params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                try {

                    String s = response.body().string().replaceAll(" ", "").replaceAll("\r|\n","");
                    int length = s.length()/1500/100;
                    openDownloadListener.openDownload("下载完成存储数据中预计耗时"+length+"秒");
                    LogUtils.a("数据下载onParseResponse,原始数据大小" + s.length());
                    JsonPoliceFace jsonPoliceFace = JSON.parseObject(s, JsonPoliceFace.class);
                    //LogUtils.a("数据下载数据内容" + jsonPoliceFace.toString());
                    List<PoliceFace> policeFaceList = jsonPoliceFace.getSource().getData();
                    PoliceFaceHelp.deleteAllPoliceInfoAllList();
                    PoliceFaceHelp.savePoliceInfoAllListToDB(policeFaceList, new PoliceFaceHelp.OpenSaveListener() {
                        @Override
                        public void opensave(String message) {
                            openDownloadListener.openDownload(message);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("请求返回数据", "onFailure");
                openDownloadListener.openDownload("下载失败");
            }

            @Override
            public void onResponse(Object response) {

                LogUtils.a("请求返回数据", "onResponse");
            }

        });
    }

    public void uploadPolicephoto(JsonPolicePhoto jsonPolicePhoto){
        Map<String, String> params = new HashMap<>();
        params.put("emp_id", jsonPolicePhoto.getEmp_id());
        params.put("dev_ip", jsonPolicePhoto.getDev_ip());
        params.put("compare_time", jsonPolicePhoto.getCompare_time());
        params.put("emp_photo", jsonPolicePhoto.getEmp_photo());
        params.put("compare_type", jsonPolicePhoto.getCompare_type());
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().uploauploadPolicephotoUrl(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                   LogUtils.a("上传照片返回"+ response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    public void registdevice(TerminalInformation terminalInformation,OpenDownloadListener openDownloadListener){
        Map<String, String> params = new HashMap<>();
        params.put("dev_code", terminalInformation.getTerminalNum());
        params.put("dev_name",terminalInformation.getTerminalName());
        params.put("dev_ip", terminalInformation.getServerIP());
        params.put("dev_port", terminalInformation.getServerPost());
        params.put("dev_serial",terminalInformation.getSerial() );
        params.put("valid", "2");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().addFaceDevice(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("注册设备返回："+string);
                    JsonSuccessReturn jsonSuccessReturn = JSON.parseObject(string, JsonSuccessReturn.class);
                    if (jsonSuccessReturn!=null&&jsonSuccessReturn.isSuccess()){
                        terminalInformation.setIsregister(true);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        openDownloadListener.openDownload("1");
                    }else if (!jsonSuccessReturn.isSuccess()){
                        openDownloadListener.openDownload(jsonSuccessReturn.getSource());
                    }else {
                        openDownloadListener.openDownload("0");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }
    public void getdevice(){
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getFaceDevice(), new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String  string = response.body().string();
                    LogUtils.a("注册设备返回："+string);
                    JsonGetFaceDevice jsonGetFaceDevice = JSON.parseObject(string, JsonGetFaceDevice.class);
                    if (jsonGetFaceDevice!=null&&jsonGetFaceDevice.isSuccess()){
                        List<JsonGetFaceDevice.SourceBean> source = jsonGetFaceDevice.getSource();
                        if (source!=null&&source.size()>0){
                            for (JsonGetFaceDevice.SourceBean sourceBean : source) {
                                if (sourceBean.getDev_serial().equals(DeviceUtils.getAndroidID())){
                                   // TerminalInformationHelp.getTerminalInformation()
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }



    //下载监听
    private OpenDownloadListener openDownloadListener;

    public interface OpenDownloadListener {
        void openDownload(String message);
    }

}
