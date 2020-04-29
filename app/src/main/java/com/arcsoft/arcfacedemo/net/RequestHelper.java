package com.arcsoft.arcfacedemo.net;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
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
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.OkhttpUtil;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
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
    public void getPoliceFace(Handler handler,String cardNumber, OpenDownloadListener openCallPoliceListener) {
        Map<String, String> params = new HashMap<>();
        params.put("emp_id", cardNumber);
        params.put("userid", "1");
        LogUtils.a("开始请求数据时间");
        final boolean[] isoutime = {false};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isoutime[0] =true;
                openCallPoliceListener.openDownload("服务器连接超时");
            }
        };
        handler.postDelayed(runnable,1000);
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getPoliceFaceUrl(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                if ( isoutime[0]){
                    return null;
                }else {
                    handler.removeCallbacks(runnable);
                }
                try {
                    String string = response.body().string().replaceAll(" ", "").replaceAll("\r|\n", "");
                    LogUtils.a("请求返回数据response时间", string);
                    JsonPoliceFace jsonPoliceFace = JSON.parseObject(string, JsonPoliceFace.class);
                    if (jsonPoliceFace.isSuccess()) {
                        PoliceFace sourceBean = jsonPoliceFace.getSource().getData().get(0);
                        if (sourceBean.getEMP_FEATURE() != null && sourceBean.getEMP_FEATURE().length() > 0) {
                            App.byteface = SwitchUtils.base64tobyte(sourceBean.getEMP_FEATURE());
                            App.police_name = sourceBean.getEMP_NAME();
                            if (sourceBean.getEMP_TYPE() == 1 || sourceBean.getEMP_TYPE() == 2 || sourceBean.getEMP_TYPE() == 5 || sourceBean.getEMP_TYPE() == 6) {
                                PoliceFaceHelp.savePoliceInfoToDB(sourceBean);
                            }
                        }
                        openCallPoliceListener.openDownload("下载成功");
                    } else {
                        JsonSuccessReturn jsonSuccessReturn = JSON.parseObject(string, JsonSuccessReturn.class);
                        openCallPoliceListener.openDownload(jsonSuccessReturn.getSource());
                    }
                } catch (Exception e) {
                    openCallPoliceListener.openDownload("异常错误");
                    LogUtils.e(e.getMessage());
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                if ( isoutime[0]){
                    return ;
                }else {
                    handler.removeCallbacks(runnable);
                }
                LogUtils.a("请求返回数据", e.getMessage().toString());
                if (e instanceof SocketTimeoutException) {//判断超时异常
                    openCallPoliceListener.openDownload("服务器连接超时");
                }
                if (e instanceof ConnectException) {
                    openCallPoliceListener.openDownload("请求失败");
                    //判断连接异常，我这里是报Failed to connect to 10.7.5.144
                }

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
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getPoliceFaceUrl(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                try {

                    String s = response.body().string().replaceAll(" ", "").replaceAll("\r|\n", "");
                    int length = s.length() / 1500 / 100;
                    openDownloadListener.openDownload("下载完成存储数据中预计耗时" + length + "秒");
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

    public void uploadPolicephoto(JsonPolicePhoto jsonPolicePhoto) {
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
                    LogUtils.a("上传照片返回" + response.body().string());
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

    public void registdevice(TerminalInformation terminalInformation, OpenDownloadListener openDownloadListener) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_code", terminalInformation.getTerminalNum());
        params.put("dev_name", terminalInformation.getTerminalName());
        params.put("dev_ip", terminalInformation.getDeviceIP());
        params.put("dev_port", terminalInformation.getDevicePost());
        params.put("dev_serial", terminalInformation.getSerial());
        params.put("valid", "2");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().addFaceDevice(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("注册设备返回：" + string);
                    JsonSuccessReturn jsonSuccessReturn = JSON.parseObject(string, JsonSuccessReturn.class);
                    if (jsonSuccessReturn != null && jsonSuccessReturn.isSuccess()) {
                        terminalInformation.setIsregister(true);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        openDownloadListener.openDownload("1");
                    } else if (!jsonSuccessReturn.isSuccess()) {
                        openDownloadListener.openDownload(jsonSuccessReturn.getSource());
                    } else {
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

    public void getdevice() {
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getFaceDevice(), new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("注册设备返回：" + string);
                    JsonGetFaceDevice jsonGetFaceDevice = JSON.parseObject(string, JsonGetFaceDevice.class);
                    if (jsonGetFaceDevice != null && jsonGetFaceDevice.isSuccess()) {
                        List<JsonGetFaceDevice.SourceBean> source = jsonGetFaceDevice.getSource();
                        if (source != null && source.size() > 0) {
                            for (JsonGetFaceDevice.SourceBean sourceBean : source) {
                                if (sourceBean.getDev_serial().equals(DeviceUtils.getAndroidID())) {
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

    public void updateFaceDevice(TerminalInformation terminalInformation, OpenDownloadListener openDownloadListener) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_code", terminalInformation.getTerminalNum());
        params.put("dev_name", terminalInformation.getTerminalName());
        params.put("dev_ip", terminalInformation.getDeviceIP());
        params.put("dev_port", terminalInformation.getDevicePost());
        params.put("dev_serial", terminalInformation.getSerial());

        OkhttpUtil.okHttpPost(UrlConfig.getinstance().updateFaceDevice(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("注册设备返回：" + string);
                    JsonSuccessReturn jsonSuccessReturn = JSON.parseObject(string, JsonSuccessReturn.class);
                    if (jsonSuccessReturn != null && jsonSuccessReturn.isSuccess()) {
                        terminalInformation.setIsregister(true);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        openDownloadListener.openDownload("1");
                    } else if (!jsonSuccessReturn.isSuccess()) {
                        openDownloadListener.openDownload(jsonSuccessReturn.getSource());
                    } else {
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

    public void getLastVersion() {
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getLastVersion(), new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("注册设备返回：" + string);
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

    public void downloadApk() {
        String ROOT_PATH = Utils.getContext().getFilesDir().getAbsolutePath();
    }


    public static long downLoadApk() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(UrlConfig.getinstance().downloadApk()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalFilesDir(Utils.getContext(), Environment.DIRECTORY_DOWNLOADS, ConfigUtil.Apk_name);//下载保存的文件名称
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置 Notification 信息
        request.setTitle(ConfigUtil.Apk_name);//弹窗显示名称
        request.setDescription("下载完成后请点击打开");
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setMimeType("application/vnd.android.package-archive");
        // 实例化DownloadManager 对象
        DownloadManager downloadManager = (DownloadManager) Utils.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        final long refrence = downloadManager.enqueue(request);
        return refrence;
    }


    //下载监听
    private OpenDownloadListener openDownloadListener;

    public interface OpenDownloadListener {
        void openDownload(String message);
    }

}
