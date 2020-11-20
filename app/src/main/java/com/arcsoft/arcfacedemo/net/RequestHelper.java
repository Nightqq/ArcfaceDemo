package com.arcsoft.arcfacedemo.net;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.dao.bean.CeWenInform;
import com.arcsoft.arcfacedemo.dao.bean.PrisonerFace;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.CeWenHelp;
import com.arcsoft.arcfacedemo.dao.helper.PoliceFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.PrisonerFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.net.bean.JsonGetFaceDevice;
import com.arcsoft.arcfacedemo.net.bean.JsonOneFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace2;
import com.arcsoft.arcfacedemo.net.bean.JsonPolicePhoto;
import com.arcsoft.arcfacedemo.net.bean.JsonSuccessReturn;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.AppExecutors;
import com.arcsoft.arcfacedemo.util.utils.CallBackUtil;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;
import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.OkhttpUtil;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public void getPoliceFace(String cardNumber, OpenDownloadListener openCallPoliceListener) {
        Map<String, String> params = new HashMap<>();
        params.put("code", "007");
        params.put("ip", NetWorkUtils.getIP());
        params.put("emp_id", cardNumber);
        params.put("msg", "获取人脸数据");
        LogUtils.a("开始请求数据时间");
        //AppExecutors.getInstance().scheduledExecutor().schedule()

        OkhttpUtil.okHttpPost(UrlConfig.getinstance().updateCriminalTemperature(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string().replaceAll(" ", "").replaceAll("\r|\n", "");
                    LogUtils.a("请求返回数据response时间", string);
                    JsonOneFace jsonPoliceFace = JSON.parseObject(string, JsonOneFace.class);
                    if (jsonPoliceFace.isIs_long_effective()) {
                        PoliceFaceHelp.savePoliceInfoToDB(jsonPoliceFace);
                    }
                    App.byteface = SwitchUtils.base64tobyte(jsonPoliceFace.getPhoto());
                    App.policeNum = jsonPoliceFace.getEmp_id();
                    App.police_name = jsonPoliceFace.getEmp_name();
                    openCallPoliceListener.openDownload("下载成功");
                } catch (Exception e) {
                    openCallPoliceListener.openDownload("异常错误");
                    LogUtils.e("异常错误", e.getMessage());
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("请求返回数据", e.getMessage().toString());
                if (e instanceof SocketTimeoutException) {//判断超时异常
                    openCallPoliceListener.openDownload("服务器连接超时");
                } else if (e instanceof ConnectException) {
                    openCallPoliceListener.openDownload("连接异常");
                    //判断连接异常，我这里是报Failed to connect to 10.7.5.144
                } else {
                    openCallPoliceListener.openDownload("下载失败");
                }

            }

            @Override
            public void onResponse(Object response) {
                LogUtils.a("请求返回数据", "onResponse");
            }
        });
    }

    public void getAllPoliceFace2(OpenDownloadListener openDownloadListener) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "1");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().getPoliceFaceUrl(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                try {
                    String s = response.body().string().replaceAll(" ", "").replaceAll("\r|\n", "");
                    LogUtils.a(s);
                    //int length = s.length() / 1500 / 100;
                    // openDownloadListener.openDownload("下载完成存储数据中预计耗时" + length + "秒");
                    LogUtils.a("数据下载onParseResponse,原始数据大小" + s.length());
                    JsonPoliceFace2 jsonPoliceFace = JSON.parseObject(s, JsonPoliceFace2.class);
                    //LogUtils.a("数据下载数据内容" + jsonPoliceFace.toString());
                    List<JsonPoliceFace2.SourceBean.DataBean> data = jsonPoliceFace.getSource().getData();
                    PoliceFaceHelp.deleteAllPoliceInfoAllList();
                    PoliceFaceHelp.savePoliceInfoAllListToDB2(data, new PoliceFaceHelp.OpenSaveListener() {
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

    //获取所有特征值数据
    public void getAllPoliceFace(OpenDownloadListener openDownloadListener) {
        FileUtils.getFileUtilsHelp().saveupdatehelp(" 开始请求数据");
        Map<String, String> params = new HashMap<>();
        params.put("code", "005");
        params.put("ip", NetWorkUtils.getIP());
        params.put("msg", "获取人脸数据");
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().updateCriminalTemperature(), params, new CallBackUtil() {
            @Override
            public JsonPoliceFace onParseResponse(Call call, Response response) {
                FileUtils.getFileUtilsHelp().saveupdatehelp(" 请求数据返回");
                try {
                    TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
                    terminalInformation.setUpdatedDate(new SimpleDateFormat("yyyy年MM月dd日").format(new Date(System.currentTimeMillis())));
                    TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                    String s = response.body().string().replaceAll(" ", "").replaceAll("\r|\n", "");
                    //LogUtils.a(s);
                    // int length = s.length() / 1500 / 100;
                    // openDownloadListener.openDownload("下载完成存储数据中预计耗时" + length + "秒");
                    //LogUtils.a("数据下载onParseResponse,原始数据大小" + s.length());
                    JsonPoliceFace jsonPoliceFace = JSON.parseObject(s, JsonPoliceFace.class);
                    //LogUtils.a("数据下载数据内容" + jsonPoliceFace.toString());
                    List<JsonPoliceFace.DataBean> data = jsonPoliceFace.getData();
                    PoliceFaceHelp.deleteAllPoliceInfoAllList();
                    PoliceFaceHelp.savePoliceInfoAllListToDB(data, new PoliceFaceHelp.OpenSaveListener() {
                        @Override
                        public void opensave(String message) {

                            openDownloadListener.openDownload(message);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    FileUtils.getFileUtilsHelp().saveupdatehelp(" 数据格式异常");
                    openDownloadListener.openDownload("数据格式异常");
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("请求返回数据", "onFailure");
                FileUtils.getFileUtilsHelp().saveupdatehelp(" 请求数据失败");
                openDownloadListener.openDownload("下载失败");
            }

            @Override
            public void onResponse(Object response) {

                LogUtils.a("请求返回数据", "onResponse");
            }

        });
    }

    //点名结束返回
    public void uploadPrisonerCallRoll() {
        Map<String, Object> params = new HashMap<>();
        params.put("code", "002");
        params.put("ip", NetWorkUtils.getIP());
        List list = new ArrayList();
        List<PrisonerFace> prisonerFaceListFromDB = PrisonerFaceHelp.getPrisonerFaceListFromDB();
        for (PrisonerFace prisonerFace : prisonerFaceListFromDB) {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("emp_name", prisonerFace.getEmp_name());
            stringMap.put("emp_id", prisonerFace.getEmp_id());
            stringMap.put("call_roll_ result", prisonerFace.getCall_roll_result());
            stringMap.put("photo", prisonerFace.getPhoto());
            list.add(stringMap);
        }
        params.put("data", list);
        // LogUtils.a(JSON.toJSONString(params));
        OkhttpUtil.okHttpPostJson(UrlConfig.getinstance().requestADong(), JSON.toJSONString(params), new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    LogUtils.a("点名结束返回" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("点名结束返回onFailure" + e.getMessage().toString());
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    //报警
    public void callPolice() {
        Map<String, String> params = new HashMap<>();
        params.put("code", "003");
        params.put("ip", NetWorkUtils.getIP());

        params.put("msg", "紧急报警");
        // LogUtils.a(JSON.toJSONString(params));
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().requestADong(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    LogUtils.a("报警返回" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("报警返回onFailure" + e.getMessage().toString());
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    public void uploadPolicephoto(JsonPolicePhoto jsonPolicePhoto) {
        Map<String, String> params = new HashMap<>();
        params.put("emp_id", jsonPolicePhoto.getEmp_id());
        params.put("dev_ip", NetWorkUtils.getIP());
        params.put("compare_time", jsonPolicePhoto.getCompare_time());
        params.put("emp_photo", jsonPolicePhoto.getEmp_photo());
        params.put("compare_type", jsonPolicePhoto.getCompare_type());
        LogUtils.a("上传照片", params.toString());
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
                LogUtils.a("上传照片返回onFailure" + e.getMessage().toString());
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    public void uploadWenDu() {
        LogUtils.a("日志", "上传温度数据");
        Map<String, String> params = new HashMap<>();
        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
        params.put("code", "006");
        params.put("ip", NetWorkUtils.getIP());
        params.put("name", ceWenInform.getName());
        params.put("emp_id", ceWenInform.getEmp_id());
        params.put("temperature", ceWenInform.getTemperature());
        params.put("photo", ceWenInform.getPhoto());
        LogUtils.a("人脸大小：", ceWenInform.getPhoto().length());
        params.put("state", ceWenInform.getState());
        params.put("time", ceWenInform.getTime());
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().updateCriminalTemperature(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    LogUtils.a("日志", "温度上传返回");
                    //LogUtils.a("上传温度返回" + response.body().string());
                } catch (Exception e) {
                    LogUtils.a("日志", "上传返回异常");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.a("日志", "上传失败");
                LogUtils.a("上传温度返回onFailure" + e.getMessage().toString());
                //失败存本地
            }

            @Override
            public void onResponse(Object response) {
                LogUtils.a("日志", "上传onresponse");
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
                        openDownloadListener.openDownload(jsonSuccessReturn.getMsg());
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


    public void updateFace(String id, String photo, boolean speak) {
        Map<String, String> params = new HashMap<>();
        params.put("code", "009");
        params.put("ip", NetWorkUtils.getIP());
        params.put("emp_id", id);
        params.put("photo", photo);
        OkhttpUtil.okHttpPost(UrlConfig.getinstance().updateCriminalTemperature(), params, new CallBackUtil() {
            @Override
            public Object onParseResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    LogUtils.a("更新特征值返回：" + string);
                    JsonSuccessReturn jsonSuccessReturn = JSON.parseObject(string, JsonSuccessReturn.class);
                    if (speak) {
                        String msg = jsonSuccessReturn.getMsg();
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(msg);
                        LogUtils.a("更新特征值返回：" + msg);
                    }
                } catch (Exception e) {
                    if (speak) {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("服务器异常");
                    }

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                if (speak) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("失败");
                }
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
                        openDownloadListener.openDownload(jsonSuccessReturn.getMsg());
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
