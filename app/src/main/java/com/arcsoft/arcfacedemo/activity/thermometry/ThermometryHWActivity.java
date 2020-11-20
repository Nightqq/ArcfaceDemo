package com.arcsoft.arcfacedemo.activity.thermometry;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.kingsun.KingsunSmartAPI;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.activity.setting.SettingActivity;
import com.arcsoft.arcfacedemo.dao.bean.CeWenInform;
import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.CeWenHelp;
import com.arcsoft.arcfacedemo.dao.helper.TemperatureSettingHelp;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.model.FacePreviewInfo;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraListener;
import com.arcsoft.arcfacedemo.util.face.FaceHelper;
import com.arcsoft.arcfacedemo.util.face.FaceListener;
import com.arcsoft.arcfacedemo.util.face.RequestFeatureStatus;
import com.arcsoft.arcfacedemo.util.image.ImageBase64Utils;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.AppExecutors;
import com.arcsoft.arcfacedemo.util.utils.CaptureUtil;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DrawHelper;
import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SPUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.TrackUtil;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.guide.guidecore.GuideInterface;
import com.guide.guidecore.UsbStatusInterface;
import com.guide.guidecore.utils.BaseDataTypeConvertUtils;
import com.guide.guidecore.view.IrSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class ThermometryHWActivity extends BaseActivity implements GuideInterface.ImageCallBackInterface, UsbStatusInterface, ViewTreeObserver.OnGlobalLayoutListener {


    @BindView(R.id.face_recognition_texturepreview)
    TextureView previewView;
    @BindView(R.id.face_recognition_facerectview)
    FaceRectView faceRectView;
    @BindView(R.id.face_recognition_subtitles)
    TextView faceRecognitionSubtitles;
    @BindView(R.id.activity_face_recognition_networkState)
    TextView activityFaceRecognitionNetworkState;

    @BindView(R.id.face_recognition_wendu)
    TextView faceRecognitionWendu;
    @BindView(R.id.action_thermometry_update)
    TextView actionThermometryUpdate;



    //  private List<CompareResult> compareResultList;
   /* private Handler handler = new Handler() {
    };*/

    /*  Runnable refresh = new Runnable() {
          @Override
          public void run() {
              LogUtils.a("日志", "refresh");
              compareNum = 0;
              switch (refresh_flag) {
                  case 0:
                      TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别失败");
                      faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                      faceRecognitionSubtitles.setText("识别失败");
                      faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                      api.controlLight("01");
                      break;
                  case 1:
                      faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                      faceRecognitionSubtitles.setText("通  过");
                      faceRecognitionSubtitles.setTextColor(Color.parseColor("#00ff00"));
                      api.controlLight("02");
                      break;
                  case 2:
                      faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                      faceRecognitionSubtitles.setText("温度异常");
                      faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                      api.controlLight("01");
                      break;
              }
          }
      };*/
    //定时显示一秒字幕再推出
   /* Runnable runnable_time_display = new Runnable() {
        @Override
        public void run() {
            LogUtils.a("日志", "runnable_time_display");
            //退出至等待页面
            compareNum = 0;
            compareSimilar = 0;
            recognition_state = 0;//结束
            faceRecognitionSubtitles.setVisibility(View.GONE);
        }
    };*/
    private String userName;
    private float wendu;
    private CaptureUtil captureUtil;
    private int forehead_x = 1;
    private int forehead_y = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometry_hy);
        ButterKnife.bind(this);
        initView();
        initHW();
        //本地人脸库初始化
        LogUtils.a("日志", "本地人脸库初始化");
        FaceServer.getInstance().init(this);
        LogUtils.a("日志", "测温硬件初始化");
        //SerialPortUtils.gethelp().openSerialPort();
        //开启定时重启广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 1);
        }
    }

    private MediaProjection mMediaProjection;
    MediaProjectionManager mMediaProjectionManager = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mResultCode = resultCode;
        //mResultData = data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            captureUtil = new CaptureUtil().setUpMediaProjection(mMediaProjection);
        }
    }

    //private ShowFaceInfoAdapter adapter;

    private void initView() {
        // visibilityLayout();
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        String date = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(System.currentTimeMillis()));
        TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
        if (terminalInformation.getUpdatedDate().equals(date)) {
            actionThermometryUpdate.setText("数据已更新");
            actionThermometryUpdate.setTextColor(Color.parseColor("#00ff00"));
        } else {
            actionThermometryUpdate.setText("数据未更新");
            actionThermometryUpdate.setTextColor(Color.parseColor("#ffff00"));
        }
        openBroadcast();//网络状态监听广播
        //RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person);
        // compareResultList = new ArrayList<>();
        // adapter = new ShowFaceInfoAdapter(compareResultList, this);
        // recyclerShowFaceInfo.setAdapter(adapter);
//        int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
        //recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
        // recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("");
    }

    private ThermometryHWActivity.networkBroadcast networkBroadcast;

    public void openBroadcast() {
        LogUtils.a("日志", "openBroadcast");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//连上与否
        networkBroadcast = new networkBroadcast();
        this.registerReceiver(networkBroadcast, intentFilter);
    }


    private class networkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.a("日志", "网络状态返回");
                        int netWorkState = NetWorkUtils.getNetworkState();
                        String message = "";
                        switch (netWorkState) {
                            case 0:
                                message = " 网络：异常";
                                activityFaceRecognitionNetworkState.setText(message);
                                activityFaceRecognitionNetworkState.setTextColor(Color.parseColor("#ff0000"));
                                activityFaceRecognitionNetworkState.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                message = "网络状态：wifi";
                                // activityFaceRecognitionNetworkState.setText(message);
                                //activityFaceRecognitionNetworkState.setTextColor(Color.parseColor("#00ff00"));
                                break;
                            case 2:
                                message = "手机2.3.4G网络";
                                break;
                            case 3:
                                message = " 网络：正常";
                                activityFaceRecognitionNetworkState.setVisibility(View.VISIBLE);
                                activityFaceRecognitionNetworkState.setText(message);
                                activityFaceRecognitionNetworkState.setTextColor(Color.parseColor("#00ff00"));
                                break;
                        }
                    }
                });
            }
        }
    }

    public void visibilityLayout() {
        //  TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("状态"+recognitionstate);
        //faceRecognitionImgview.setVisibility(View.VISIBLE);
        //faceRecognitionLinear.setVisibility(View.VISIBLE);
    }

    private KingsunSmartAPI api;
    private static float SIMILAR_THRESHOLD = 0.8F;
    private int fail_num = 5;


    // int wendu_num = 1;

    @Override
    protected void onStart() {
        super.onStart();
        if (api == null) {
            api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
            //api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
        }
        api.setStatusBar(true);
        SIMILAR_THRESHOLD = TerminalInformationHelp.getTerminalInformation().getRecognitionThreshold();
        fail_num = TerminalInformationHelp.getTerminalInformation().getRecognitionNum();

       /* SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer) {
                LogUtils.a("日志", "onDataReceive byte【】");
            }

            //流程4测温结果对比
            @Override
            public void onDataReceive(String buffer) {
                LogUtils.a("日志", "onDataReceive");
                if (buffer.equals("超距")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("超距");
                    displaysubtitles(4);
                    return;
                } else {
                    wendu = Float.parseFloat(buffer);
                    TemperatureSetting temperatureSetting = TemperatureSettingHelp.getTerminalInformation();
                    LogUtils.a("日志", "温度判断");
                    if (wendu < Float.parseFloat(temperatureSetting.getWenxia())) {//低于温度下
                        LogUtils.a("日志", "温度异常重测");
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请虫测");//文字转语音，多音字读重zhong
                        //重新人脸识别
                        displaysubtitles(3);
                        return;
                    } else if (wendu < Float.parseFloat(temperatureSetting.getWenshang())) {//正常温度
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("正常");
                        displaysubtitles(1);
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setTemperature(buffer);
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        //上传
                        AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
                            @Override
                            public void run() {
                                AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestHelper.getRequestHelper().uploadWenDu();
                                    }
                                });
                            }
                        }, 300, TimeUnit.MILLISECONDS);
                        return;
                    } else {//高于温度上限异常提示
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setTemperature(buffer);
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        //上传
                        AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
                            @Override
                            public void run() {
                                AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestHelper.getRequestHelper().uploadWenDu();
                                    }
                                });
                            }
                        }, 300, TimeUnit.MILLISECONDS);
                       *//* handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RequestHelper.getRequestHelper().uploadWenDu();
                            }
                        }, 300);*//*
                        displaysubtitles(2);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("异常");
                    }
                }
            }
        });*/
    }

    //比较相似度次数
    int compareNum = 0;
    //人脸识别识别温度结果0失败，1通过，2异常，
    int refresh_flag = 0;
    float compareSimilar = 0;

    //0失败，1成功,2异常
    //流程5更新页面显示数据
    public void displaysubtitles(int i) {
        LogUtils.a("日志", "displaysubtitles");
        compareNum = 0;
        refresh_flag = i;
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                recordTime();
                compareNum = 0;
                if (previewView != null) {
                    // previewView.clearAnimation();
                    previewView.refreshDrawableState();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        previewView.releasePointerCapture();
                    }
                }
                switch (i) {
                    case 0:
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别失败");
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("识别失败");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                    case 1:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("通  过");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#00ff00"));
                        faceRecognitionWendu.setVisibility(View.VISIBLE);
                        faceRecognitionWendu.setText(userName + "    " + wendu + "℃");
                        faceRecognitionWendu.setTextColor(Color.parseColor("#00ff00"));
                        api.controlLight("02");
                        break;
                    case 2:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("温度异常");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        faceRecognitionWendu.setVisibility(View.VISIBLE);
                        faceRecognitionWendu.setText(userName + "    " + wendu + "℃");
                        faceRecognitionWendu.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                    case 3:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("请重测");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        faceRecognitionWendu.setVisibility(View.VISIBLE);
                        faceRecognitionWendu.setText("请正对测温仪器");
                        faceRecognitionWendu.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                    case 4:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("超  距");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        faceRecognitionWendu.setVisibility(View.VISIBLE);
                        faceRecognitionWendu.setText(userName);
                        faceRecognitionWendu.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                }
            }
        });
        AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        compareNum = 0;
                        compareSimilar = 0;
                        recognition_state = 0;//结束
                        faceRecognitionSubtitles.setVisibility(View.GONE);
                        faceRecognitionWendu.setVisibility(View.GONE);
                    }
                });
            }
        }, 1000, TimeUnit.MILLISECONDS);

        String format = new DecimalFormat("0.000").format(compareSimilar);
        //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("相似度：" + format);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String format1 = simpleDateFormat.format(date);
        String time = simpleDateFormattime.format(date);
        String content = "日期：" + format1 + "时间：" + time + "卡号：" + App.police_name + "相似度：" + format + "结果：" + refresh_flag + "\n";
        FileUtils.getFileUtilsHelp().savaSimilarityLog(content);

       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordTime();
                compareNum = 0;
                switch (i) {
                    case 0:
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别失败");
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("识别失败");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                    case 1:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("通过");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#00ff00"));
                        api.controlLight("02");
                        break;
                    case 2:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("温度异常");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                    case 3:
                        faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                        faceRecognitionSubtitles.setText("超距");
                        faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                        api.controlLight("01");
                        break;
                }
            }
        });*/
        // handler.removeCallbacks(runnable_time_display);
        // handler.postDelayed(runnable_time_display, 1000);//延迟一秒执行
        // String format = new DecimalFormat("0.000").format(compareSimilar);
        //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("相似度：" + format);
     /*   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String format1 = simpleDateFormat.format(date);
        String time = simpleDateFormattime.format(date);
        String content = "日期：" + format1 + "时间：" + time + "卡号：" + "无" + "相似度：" + format + "结果：" + i + "\n";*/
        // FileUtils.getFileUtilsHelp().savaSimilarityLog(content);
    }

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    @Override
    public void onGlobalLayout() {
        LogUtils.e("日志", "onGlobalLayout");
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    private FaceEngine faceEngine;
    private int afCode = -1;
    private static final int MAX_DETECT_NUM = 10;

    /**
     * 初始化引擎
     */
    private void initEngine() {
        LogUtils.e("日志", "initEngine");
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
        LogUtils.e("日志", "initEngine结束");
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        LogUtils.a("日志", "unInitEngine");

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            LogUtils.a("unInitEngine: " + afCode);
        }
    }

    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private Camera.Size previewSize;
    private DrawHelper drawHelper;
    private FaceHelper faceHelper;
    private CameraHelper cameraHelper;
    private Integer rgbCameraID = 0;

    //初始状态0，开始识别中1,
    private int recognition_state = 0;

    private int trackId = 0;

    private void initCamera() {
        LogUtils.e("日志", "initCamera");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                LogUtils.e("日志", "onFail" + e.getMessage().toString());
                // LogUtils.a("onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                LogUtils.a("日志", "requestFaceFeature返回");
                //FR成功
                if (faceFeature != null) {
                    compareFace(faceFeature, requestId);
                  /*  LogUtils.a("日志", "onFaceFeatureInfoGetfaceFeature != null");
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        //LogUtils.a("活体检测通过，搜索特征");
                        compareFace(faceFeature, requestId);
                    }  //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        LogUtils.a("日志", " 活体检测未出结果，延迟");
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    LogUtils.a("日志", " if (rectXY)结束");*/
                } else {//FR 失败
                    LogUtils.a("日志", "faceFeature = null");
                    recognition_state = 0;//Fr失败
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };
        CameraListener cameraListener = new CameraListener() {


            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                LogUtils.e("日志", "onCameraOpened");
                previewSize = camera.getParameters().getPreviewSize();
                LogUtils.e("日志", "drawHelper");
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, false, false, false);
                faceHelper = new FaceHelper.Builder()
                        .faceEngine(faceEngine)
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .currentTrackId(ConfigUtil.getTrackId(ThermometryHWActivity.this.getApplicationContext()))
                        .build();
                LogUtils.e("日志", "onCameraOpened结束");
            }

            //int i=0;
            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);
                TrackUtil.keepMaxFacePreview(facePreviewInfoList);
                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    //LogUtils.a("日志", "开始绘制人脸框");
                    drawPreviewInfo(facePreviewInfoList);
                }
                clearLeftFace(facePreviewInfoList);

                //流程1开始人脸检测获取特征值
                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null && recognition_state == 0) {
                    /**
                     * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                     * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                     */
                    // for (int i = 0; i < facePreviewInfoList.size(); i++) {
                    //i++;
                    int width = facePreviewInfoList.get(facePreviewInfoList.size() - 1).getFaceInfo().getRect().width();
                   /* if (i%50==0){
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(width+"");
                    }*/
                    recognition_state = 1;//开始人脸识别
                    LogUtils.a("人脸ID", facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId() + "==" + trackId);
                    livenessMap.put(facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId(), facePreviewInfoList.get(facePreviewInfoList.size() - 1).getLivenessInfo().getLiveness());
                    Rect rect = facePreviewInfoList.get(facePreviewInfoList.size() - 1).getFaceInfo().getRect();

                  /*  double v = rect.centerX() - rect.height() * 0.2 ;
                    BigDecimal bd = new BigDecimal(v).setScale(0, BigDecimal.ROUND_HALF_UP);
                    forehead_x = Integer.parseInt(bd.toString());
                    forehead_y = rect.centerY();
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            thermometryHwForehead.layout(rect.left,rect.top,rect.right,rect.bottom);
                        }
                    });*/
                    // LogUtils.a("坐标", rect.centerX() + "===" + rect.centerY());
                    // LogUtils.a("坐标宽高", rect.top + "=" + rect.bottom + "=" + rect.left + "=" + rect.right);
                    if (rect.top < 0 || rect.bottom > 1000) {//人脸框顺旋转90度为实际
                        trackId = 0;
                    }
                    if (facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId() == trackId) {//人脸未离开
                        if (refresh_flag == 1) {//人脸识别成功再次识别
                            recognition_state = 0;
                            if (isFastrecognition(1200)) {
                                displaysubtitles(1);//已经通过再次通过
                            }
                            return;
                        } else {//温度测试未通过
                            if (isFastrecognition(1800)) {//上一次测温
                                rFaceFeature(facePreviewInfoList, nv21);
                            } else {
                                recognition_state = 0;
                                return;
                            }
                        }
                    } else {//新的人脸
                        rFaceFeature(facePreviewInfoList, nv21);
                    }
                    /*if (rect.centerX() < 800 && rect.centerY() > 250 && rect.centerY() < 730&&width>280) {//在指定框内切人脸框大于280
                        if (facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId() == trackId) {//人脸未离开
                            if (refresh_flag == 1) {//人脸识别成功再次识别
                                recognition_state = 0;
                                if (isFastrecognition(1200)) {
                                    displaysubtitles(1);//已经通过再次通过
                                }
                                return;
                            } else {//温度测试未通过
                                if (isFastrecognition(1800)) {//上一次测温
                                    rFaceFeature(facePreviewInfoList, nv21);
                                } else {
                                    recognition_state = 0;
                                    return;
                                }
                            }
                        } else {//新的人脸
                            rFaceFeature(facePreviewInfoList, nv21);
                        }
                    } else {//人脸不在框内
                        trackId = 0;
                        recognition_state = 0;
                    }*/
                }
            }

            private void rFaceFeature(List<FacePreviewInfo> facePreviewInfoList, byte[] nv21) {
                refresh_flag = -1;
                trackId = facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId();
                requestFeatureStatusMap.put(facePreviewInfoList.get(facePreviewInfoList.size() - 1).getTrackId(), RequestFeatureStatus.SEARCHING);
                LogUtils.a("日志", "requestFaceFeature");
                faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(facePreviewInfoList.size() - 1).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(0).getTrackId());
            }


            @Override
            public void onCameraClosed() {
                LogUtils.a("日志", "onCameraClosed");
            }

            @Override
            public void onCameraError(Exception e) {
                LogUtils.a("日志", "onCameraError");
                LogUtils.a("onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                LogUtils.a("日志", "onCameraConfigurationChanged");
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                LogUtils.a("onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraID != null ? rgbCameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness == null ? LivenessInfo.UNKNOWN : liveness,
                    name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
        }
        drawHelper.draw(faceRectView, drawInfoList);
    }

    //流程2进行人脸对比搜索
    private void compareFace(FaceFeature faceFt, Integer requestId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (compareNum >= fail_num) {
                    displaysubtitles(0);
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    return;
                }
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFt);
                if (compareResult == null) {
                    recognition_state = 0;//人脸相似度为空
                } else {
                    compareSimilar = compareSimilar > compareResult.getSimilar() ? compareSimilar : compareResult.getSimilar();
                    if (compareResult == null || compareResult.getUserName() == null) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        faceHelper.addName(requestId, "VISITOR " + requestId);
                        recognition_state = 0;//获取人脸相似度内容空
                        return;
                    }
                    LogUtils.a("日志", "相似度结果比较");
                    if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                        userName = compareResult.getUserName();
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(userName);
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                        faceHelper.addName(requestId, compareResult.getUserName());
                        //  wendu_num = 0;
                        //上传照片
                        takePictures();
                        //jumpcewen();//识别成功测温
                        try {
                            hWcewen();
                        } catch (NullPointerException e) {
                            recognition_state = 0;//测温启动中
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("稍等");
                        }
                    } else {
                        compareNum++;
                        recognition_state = 0;//人脸相似度太低
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        //faceHelper.addName(requestId, compareResult.getUserName());
                    }

                }
            }
        });

     /*   new Thread() {
            @Override
            public void run() {
                super.run();
                if (compareNum >= fail_num) {
                    displaysubtitles(0);
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    return;
                }
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFt);
                if (compareResult == null) {
                    recognition_state = 0;//人脸相似度为空
                } else {
                    compareSimilar = compareSimilar > compareResult.getSimilar() ? compareSimilar : compareResult.getSimilar();
                    if (compareResult == null || compareResult.getUserName() == null) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        faceHelper.addName(requestId, "VISITOR " + requestId);
                        recognition_state = 0;//获取人脸相似度内容空
                        return;
                    }
                    LogUtils.a("日志", "相似度结果比较");
                    if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                        String userName = compareResult.getUserName();
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(userName);
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                        faceHelper.addName(requestId, compareResult.getUserName());
                        //上传照片
                        takePictures();
                        jumpcewen();//识别成功测温
                    } else {
                        compareNum++;
                        recognition_state = 0;//人脸相似度太低
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        faceHelper.addName(requestId, compareResult.getUserName());
                    }
                }

            }
        }.start();*/


     /*   Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
                LogUtils.a("日志", "subscribe");
                if (compareNum >= fail_num) {
                    displaysubtitles(0);
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    return;
                }
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFt);
                if (compareResult == null) {
                    recognition_state = 0;//人脸相似度为空
                    emitter.onError(null);
                } else {
                    compareSimilar = compareSimilar > compareResult.getSimilar() ? compareSimilar : compareResult.getSimilar();
                    emitter.onNext(compareResult);
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                // .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                            recognition_state = 0;//获取人脸相似度内容空
                            return;
                        }
                        LogUtils.a("日志", "相似度结果比较");
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            String userName = compareResult.getUserName();
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(userName);
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUserName());
                            wendu_num = 0;
                            //上传照片
                            takePictures();
                            jumpcewen();//识别成功测温
                        } else {
                            compareNum++;
                            recognition_state = 0;//人脸相似度太低
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, compareResult.getUserName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        recognition_state = 0;//获取相似度异常
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }



    //流程3hw测温
    public void hWcewen() {
        // float centerTemp = Float.valueOf(mGuideInterface.getCenterTemp());
        float ambientTemp;//环境温度
        if (TextUtils.isEmpty(mAmbientTempEditText.getText())) {
            ambientTemp = GuideInterface.DEFAULT_AMBIENT_TEMP;
        } else {
            ambientTemp = Float.valueOf(mAmbientTempEditText.getText().toString());
        }
        float maxTemp = Float.parseFloat(maxTempStr);
       /* mHumanCenterTextView.setText(
                "体内中心温:" + "\r\n" +
                        mGuideInterface.getHumanTemp(centerTemp, ambientTemp) + "\r\n" +
                        "体内最高温:" + "\r\n" +
                        mGuideInterface.getHumanTemp(maxTemp, ambientTemp));*/
        String humanTemp = mGuideInterface.getHumanTemp(maxTemp, ambientTemp);
        wendu = Float.parseFloat(humanTemp);
        TemperatureSetting temperatureSetting = TemperatureSettingHelp.getTerminalInformation();
        LogUtils.a("红外温度", humanTemp);
        if (wendu < Float.parseFloat(temperatureSetting.getWenxia())) {//低于温度下
            LogUtils.a("日志", "温度异常重测");
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请虫测");//文字转语音，多音字读重zhong
            //重新人脸识别
            displaysubtitles(3);
            return;
        } else if (wendu < Float.parseFloat(temperatureSetting.getWenshang())) {//正常温度
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("正常");
            displaysubtitles(1);
            CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
            ceWenInform.setTemperature(humanTemp);
            CeWenHelp.saveCeWenInform(ceWenInform);
            //上传
            AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            RequestHelper.getRequestHelper().uploadWenDu();
                        }
                    });
                }
            }, 300, TimeUnit.MILLISECONDS);
          /*  List<String> list = new ArrayList<>();
            for (short i : mY16Frame) {
                String s = mGuideInterface.measureTemByY16(i);
                list.add(s);
            }
            LogUtils.a("坐标y16Frame",list.toString());*/
        } else {//高于温度上限异常提示
            CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
            ceWenInform.setTemperature(humanTemp);
            CeWenHelp.saveCeWenInform(ceWenInform);
            //上传
            AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            RequestHelper.getRequestHelper().uploadWenDu();
                        }
                    });
                }
            }, 300, TimeUnit.MILLISECONDS);
            displaysubtitles(2);
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("异常");
        }
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormattime.format(date);
        FileUtils.getFileUtilsHelp().savatemperatureLog("时间:" + time +
                "体内最高温:" + mGuideInterface.getHumanTemp(maxTemp, ambientTemp) +
                "焦温:" + mGuideInterface.getFoucsTemp() + "额头温度:" + humanTemp + "\n");
    }
    //  int i = 100;

    private void takePictures() {
        LogUtils.a("日志", "开始截图获取照片" + System.currentTimeMillis());
        //captureUtil = new CaptureUtil().setUpMediaProjection(mMediaProjection);
        AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = captureUtil.startCapture();
                if (bitmap != null) {
                    // i++;
                    //  LogUtils.a("日志", "开始截图获取照片返回" + System.currentTimeMillis());
                    LogUtils.a("日志", "照片返回存储");
                    CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                    ceWenInform.setPhoto(ImageBase64Utils.getBitmapByte(bitmap));
                    ceWenInform.setTime(System.currentTimeMillis() + "");
                    CeWenHelp.saveCeWenInform(ceWenInform);
                    // FileUtils.getFileUtilsHelp().saveMyBitmap(bitmap);
                } else {
                    LogUtils.a("日志", "存储无人脸");
                    CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                    ceWenInform.setnoPhoto();
                    ceWenInform.setTime(System.currentTimeMillis() + "");
                    CeWenHelp.saveCeWenInform(ceWenInform);
                    LogUtils.a("无人脸");
                }
            }
        }, 100, TimeUnit.MILLISECONDS);
     /*   handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = captureUtil.startCapture();
                    if (bitmap != null) {
                        // i++;
                        //  LogUtils.a("日志", "开始截图获取照片返回" + System.currentTimeMillis());
                        LogUtils.a("日志", "照片返回存储");
                        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        // Date date = new Date(System.currentTimeMillis());
                        //String format = simpleDateFormat.format(date);
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setPhoto(ImageBase64Utils.getBitmapByte(bitmap));
                        // LogUtils.a("上传照片", "存储" + i);
                        //ceWenInform.setTime(i + "");
                        ceWenInform.setTime(System.currentTimeMillis() + "");
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        //FileUtils.getFileUtilsHelp().saveMyBitmap(bitmap);
                    } else {
                        //LogUtils.a("上传照片", "存储无人脸" + i);
                        LogUtils.a("日志", "存储无人脸");
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setPhoto("无人脸");
                        ceWenInform.setTime(System.currentTimeMillis() + "");
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        LogUtils.a("无人脸");
                    }
                } catch (NullPointerException r) {
                    LogUtils.a("图片", r.getMessage().toString());
                    r.printStackTrace();
                }
            }
        }, 100);*/


    }

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
      /*  if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        }*/
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            recognition_state = 0;//没人人脸时
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            return;
        }
        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(integer);
                livenessMap.remove(integer);
            }
        }

    }

    private long lastRecognitionTime = System.currentTimeMillis();

    private void recordTime() {//记录人脸测温结果时间
        lastRecognitionTime = System.currentTimeMillis();
    }

    private boolean isFastrecognition(long intervaltime) {//距离上次测温时间差
        long time = System.currentTimeMillis();
        long timeD = time - lastRecognitionTime;
        if (timeD > intervaltime) {
            return true;
        } else {
            return false;
        }
    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    protected void onDestroy() {
        LogUtils.a("日志", "onDestroy");
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper != null) {
            synchronized (faceHelper) {
                unInitEngine();
            }
            faceHelper.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        if (networkBroadcast!=null){
            this.unregisterReceiver(networkBroadcast);
        }
        FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {

            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            LogUtils.a("日志", "onRequestPermissionsResult" + isAllGranted);
            if (isAllGranted) {
                if (cameraHelper != null) {
                    initEngine();
                    initCamera();
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void jumptoSetting(View view) {
        showTemp();
        if (isFastDoubleClick()) {
            showdkdialog();
        }
        //api.setStatusBar(true);
       /* RequestHelper.getRequestHelper().getPoliceFace(new RequestHelper.OpenDownloadListener() {
            @Override
            public void openDownload(String msgs) {
                if (msgs.equals("存储数据成功") || msgs.equals("下载失败")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(msgs);
                    return;
                }
            }
        });*/
    }

    private long lastClickTime = System.currentTimeMillis();

    private boolean isFastDoubleClick() {
        LogUtils.a("日志", "isFastDoubleClick");
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 300) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    private void showdkdialog() {

        LogUtils.a("日志", "showdkdialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        Button button = (Button) view.findViewById(R.id.dialog_password_confirm);
        Button button_cancel = (Button) view.findViewById(R.id.dialog_password_cancel);
        EditText editText = (EditText) view.findViewById(R.id.dialog_password_edittext);

        dialog.setCanceledOnTouchOutside(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                LogUtils.a(text);
                if (text.equals("njzx8421")) {//1管理员
                    api.setStatusBar(true);
                    Intent intent = new Intent(ThermometryHWActivity.this, SettingActivity.class);
                    intent.putExtra("mode", 1);
                    startActivity(intent);
                } else if (text.equals("njzx")) {//2干警
                    api.setStatusBar(true);
                    Intent intent = new Intent(ThermometryHWActivity.this, SettingActivity.class);
                    intent.putExtra("mode", 2);
                    startActivity(intent);
                } else {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("密码错误");
                }
                dialog.dismiss();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 8 && editable.toString().equals("njzx8421")) {
                    button.callOnClick();
                } else if (editable.length() == 6 && editable.toString().equals("njzx")) {
                    button.callOnClick();
                }
            }
        });
        dialog.show();

        AppExecutors.getInstance().scheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 20 * 1000, TimeUnit.MILLISECONDS);
       /* Runnable dialogrunnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        handler.postDelayed(dialogrunnable, 20000);*/
    }

    public void jumpTorestart(View view) {
        //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("每天定时更新开启");
        restartApp();
    }

    public static final String TAG_EXIT = "exit";
    public static final String TAG_RESTART = "restart";

    //结束程序
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.a("日志", "onNewIntent");
        if (intent != null) {
            String isExit = intent.getStringExtra(TAG_EXIT);
            if (isExit != null && isExit.equals(TAG_EXIT)) {//退出
                int currentVersion = Build.VERSION.SDK_INT;
                if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                }
            } else if (isExit != null && isExit.equals(TAG_RESTART)) {//重启
                restartApp();
            }
        }
    }


    private void restartApp() {
        Intent intent2 = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
    }


    //******************************************************红外*********************************************************

    private GuideInterface mGuideInterface;
    private Bitmap mIrBitmap;
    private short[] mY16Frame;
    private short[] mSyncY16Frame;

    private static final int SRC_WIDTH = 90;
    private static final int SRC_HEIGHT = 120;
    private int paletteIndex = -1;
    private boolean isFliptY = true;

    protected FrameLayout mIrSurfaceViewLayout;
    protected IrSurfaceView mIrSurfaceView;

    private RelativeLayout.LayoutParams irSurfaceViewLayoutParams;
    private RelativeLayout.LayoutParams displayViewLayoutParams;
    private RelativeLayout.LayoutParams humanDiaplayLayoutParams;

    private static final String TAG = "guidecore";

    private SettingView mSettingView;
    private TextView mCenterTextView;
    private TextView mHumanCenterTextView;
    private TextView mFocusTextView;
    private EditText mDistanceEditText;
    private TextView mEnvtempTextView;
    private EditText mAmbientTempEditText;
    private EditText mNearKFEditText;
    private EditText mNearBEditText;
    private EditText mFarKFEditText;
    private EditText mFarBEditText;
    private EditText mAjustT1EditText;
    private EditText mAjustT2EditText;
    private EditText mBrightEditText;
    private EditText mContrastEditText;
    private TextView mSNTextView;
    private TextView mSDKVersionTextView;
    private TextView mFirmwareVersionTextView;
    private TextView mUpgradePathTextView;
    private FrameLayout mDisplayFrameLayout;
    private FrameLayout mHumanDisplayFrameLayout;
    private LinearLayout mExpertLayout;
    private boolean isDispLayTemp;
    private boolean isDispLayHumanTemp;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Timer mHumanTimer;
    private TimerTask mHumanTimerTask;
    private String tempPath;
    private boolean isSDKDebugStart = false;
    private static final int EXPERT_MODE_HIT_COUNT = 5;
    private static final long EXPERT_MODE_HIT_DURATION = 2 * 1000;
    private static long EXPERT_HITS[] = new long[EXPERT_MODE_HIT_COUNT];

    private Button mRecordDebugData;

    private ImageView mHighCrossView;
    private int count;
    private int FRAME = 25;
    private int maxIndex = 0;
    private int rawWidth;
    private int rawHeight;
    private int highCrossWidth = 40;
    private int highCrossHeight = 40;

    /*
     图像旋转参数，取值默认是1 , 取值范围为0，1，2，3
      1：手机配件正面对人，设备（手机或者平板）的插入接口位于下方
      2：手机配件正面对人，设备（手机或者平板）的插入接口位于右方
      3：手机配件正面对人，设备（手机或者平板）的插入接口位于上方
      4：手机配件正面对人，设备（手机或者平板）的插入接口位于左方
     */
    private int rotateType = 1;//旋转参数
    private float mScale = 3f;//放大系数
    private int irSurfaceViewWidth;
    private int irSurfaceViewHeight;

    private int width;
    private int height;
    private String maxTempStr = "0";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mCenterTextView.setText("体表中心温:" + "\r\n" +
                            mGuideInterface.getCenterTemp() + "\r\n" +
                            "体表最高温:" + "\r\n" +
                            maxTempStr);

                    mFocusTextView.setText("焦温:" + mGuideInterface.getFoucsTemp());

                    mEnvtempTextView.setText("环境温：" + mGuideInterface.getEnvTemp() + "\n" +
                            "冷热机状态：" + (mGuideInterface.getColdHotState() ? "冷机" : "热机") + "\n" +
                            "测温状态:" + (mGuideInterface.isCalTempOk() ? "稳定" : "不稳定"));
                    break;
                case 1:
                    float centerTemp = Float.valueOf(mGuideInterface.getCenterTemp());
                    float ambientTemp;
                    if (TextUtils.isEmpty(mAmbientTempEditText.getText())) {
                        ambientTemp = GuideInterface.DEFAULT_AMBIENT_TEMP;
                    } else {
                        ambientTemp = Float.valueOf(mAmbientTempEditText.getText().toString());
                    }

                    float maxTemp = Float.parseFloat(maxTempStr);
                    mHumanCenterTextView.setText(
                            "体内中心温:" + "\r\n" +
                                    mGuideInterface.getHumanTemp(centerTemp, ambientTemp) + "\r\n" +
                                    "体内最高温:" + "\r\n" +
                                    mGuideInterface.getHumanTemp(maxTemp, ambientTemp));
                    break;
                case 3:
                    Toast.makeText(ThermometryHWActivity.this, "0.5米参数保存成功", Toast.LENGTH_LONG).show();
                    break;

                case 4:
                    Toast.makeText(ThermometryHWActivity.this, "1.2米参数保存成功", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private void initHW() {
        verifyStoragePermissions();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initHWView();
        mGuideInterface = new GuideInterface();
        int paletteIndex = Integer.valueOf(SPUtils.getPalette(ThermometryHWActivity.this));
        mScale = Float.valueOf(SPUtils.getScale(ThermometryHWActivity.this));
        // rotateType = Integer.valueOf(SPUtils.getRotate(ThermometryHWActivity.this));
        mGuideInterface.guideCoreInit(this, paletteIndex, mScale, rotateType);
        mGuideInterface.setDistance(1.0f);
        String imageAlgoSwitch = SPUtils.getImageAlgo(ThermometryHWActivity.this);
        mGuideInterface.controlImageOptimizer(TextUtils.equals(imageAlgoSwitch, "开"));

        mSDKVersionTextView.setText("SDK Version: " + mGuideInterface.getVersion());

        //原始红外视频的分辨率是90*120
        mY16Frame = new short[SRC_WIDTH * SRC_HEIGHT];
        mSyncY16Frame = new short[SRC_WIDTH * SRC_HEIGHT];

        createFile();
    }

    private void initHWView() {
        mCenterTextView = findViewById(R.id.temp_display);
        mHumanCenterTextView = findViewById(R.id.human_temp_display);
        mFocusTextView = findViewById(R.id.focus_temp_display);
        mDistanceEditText = findViewById(R.id.distance_et);
        mEnvtempTextView = findViewById(R.id.envtemp_textview);
        mAmbientTempEditText = findViewById(R.id.ambient_temp_et);
        mNearKFEditText = findViewById(R.id.adjust_temp_5_kf_et);
        mNearBEditText = findViewById(R.id.adjust_temp_5_b_et);
        mFarKFEditText = findViewById(R.id.adjust_temp_12_kf_et);
        mFarBEditText = findViewById(R.id.adjust_temp_12_b_et);
        mAjustT1EditText = findViewById(R.id.adjust_T1_et);
        mAjustT2EditText = findViewById(R.id.adjust_T2_et);
        mBrightEditText = findViewById(R.id.bright_et);
        mContrastEditText = findViewById(R.id.contrast_et);
        mSNTextView = findViewById(R.id.sn_tv);
        mSDKVersionTextView = findViewById(R.id.sdk_version_tv);
        mFirmwareVersionTextView = findViewById(R.id.firmware_version_tv);
        //mUpgradePathTextView = findViewById(R.id.upgrade_path_tv);
        mDisplayFrameLayout = findViewById(R.id.temp_display_layout);
        mHumanDisplayFrameLayout = findViewById(R.id.human_temp_display_layout);
        mExpertLayout = findViewById(R.id.expert_ll);
        mDisplayFrameLayout.setVisibility(View.GONE);
        mHumanDisplayFrameLayout.setVisibility(View.GONE);

        mIrSurfaceViewLayout = findViewById(R.id.final_ir_layout);

        mIrSurfaceView = new IrSurfaceView(this);
        FrameLayout.LayoutParams ifrSurfaceViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mIrSurfaceView.setLayoutParams(ifrSurfaceViewLayoutParams);

        mIrSurfaceView.setMatrix(dip2px(180) / 360, 0, 0);
        mIrSurfaceViewLayout.addView(mIrSurfaceView);

        width = (int) getResources().getDimension(R.dimen.ir_width);
        height = (int) getResources().getDimension(R.dimen.ir_height);

        highCrossWidth = (int) getResources().getDimension(R.dimen.high_cross_width);
        highCrossHeight = (int) getResources().getDimension(R.dimen.high_cross_height);

        mIrSurfaceViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                irSurfaceViewLayoutParams = (RelativeLayout.LayoutParams) mIrSurfaceViewLayout.getLayoutParams();
                displayViewLayoutParams = (RelativeLayout.LayoutParams) mDisplayFrameLayout.getLayoutParams();
                humanDiaplayLayoutParams = (RelativeLayout.LayoutParams) mHumanDisplayFrameLayout.getLayoutParams();

                switch (rotateType) {
                    case 1:
                        irSurfaceViewWidth = width;
                        irSurfaceViewHeight = height;
                        break;
                    case 2:
                        irSurfaceViewWidth = height;
                        irSurfaceViewHeight = width;
                        break;

                    case 3:
                        irSurfaceViewWidth = width;
                        irSurfaceViewHeight = height;
                        break;

                    case 0:
                        irSurfaceViewWidth = height;
                        irSurfaceViewHeight = width;
                        break;
                }


                irSurfaceViewLayoutParams.width = irSurfaceViewWidth;
                irSurfaceViewLayoutParams.height = irSurfaceViewHeight;
                mIrSurfaceViewLayout.setLayoutParams(irSurfaceViewLayoutParams);

                displayViewLayoutParams.width = irSurfaceViewWidth;
                displayViewLayoutParams.height = irSurfaceViewHeight;
                mDisplayFrameLayout.setLayoutParams(displayViewLayoutParams);

                humanDiaplayLayoutParams.width = irSurfaceViewWidth;
                humanDiaplayLayoutParams.height = irSurfaceViewHeight;
                mHumanDisplayFrameLayout.setLayoutParams(humanDiaplayLayoutParams);


            }
        });


        mRecordDebugData = findViewById(R.id.record);
        mRecordDebugData.setText("录制数据");

        mHighCrossView = new ImageView(this);
        mHighCrossView.setImageResource(R.drawable.forehead);
        mHighCrossView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private float dip2px(int dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        String autoShutterSwitch = SPUtils.getSwitch(ThermometryHWActivity.this);
        long period = Long.valueOf(SPUtils.getPeriod(ThermometryHWActivity.this));
        long delay = Long.valueOf(SPUtils.getDelay(ThermometryHWActivity.this));

        mGuideInterface.registUsbPermissions();
        mGuideInterface.registUsbStatus(this);
        mGuideInterface.setAutoShutter(TextUtils.equals(autoShutterSwitch, "开"), period, delay);
        mGuideInterface.startGetImage(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //退出的时候停止解析线程
        mGuideInterface.stopGetImage();
        mGuideInterface.unRegistUsbPermissions();
        mGuideInterface.unRigistUsbStatus();
    }

    /*原始红外视频的分辨率是90*120,而返回的Bitmap是在底层做了双线性插值，
     放大了3倍，所以bitmap 的分辨率是270*360，以保证在手机上展示的清晰度*/
    boolean show = true;

    @Override
    public void callBackOneFrameBitmap(Bitmap bitmap, final short[] y16Frame) {
        mIrBitmap = bitmap;
       /* if (mIrSurfaceView.getShowAjustView()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (show) {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("打开完毕");
                        show = false;
                    }
                    int c1Y16Index = mIrSurfaceView.getC1Y16Index(rotateType);
                    short c1Y16 = y16Frame[c1Y16Index];
                    Log.d(TAG, "c1Y16 = " + c1Y16);
                    String c1Temp = mGuideInterface.measureTemByY16(c1Y16);
                    int c2Y16Index = mIrSurfaceView.getC2Y16Index(rotateType);
                    short c2Y16 = y16Frame[c2Y16Index];
                    Log.d(TAG, "c2Y16 = " + c2Y16);
                    String c2Temp = mGuideInterface.measureTemByY16(c2Y16);
                    mIrSurfaceView.setC1Text(c1Temp);
                    mIrSurfaceView.setC2Text(c2Temp);
                }
            });
        }*/
        mIrSurfaceView.doDraw(mIrBitmap, mGuideInterface.getShutterStatus());
        //单个点通过Y16计算出温度的方法如下:
        mY16Frame = y16Frame;
        count++;
        if (count % FRAME == 0) {
            Log.v("GUIDE", "======================");
            final short maxY16 = getMaxY16(y16Frame);
            //Log.v("GUIDE"," maxY16="+maxY16);
            maxTempStr = mGuideInterface.measureTemByY16(maxY16);
            //Log.v("GUIDE"," maxTempStr="+maxTempStr);
            if (rotateType == 1 || rotateType == 3) {
                //宽是90，高是120
                rawWidth = SRC_WIDTH;
                rawHeight = SRC_HEIGHT;
            } else {
                //宽是120，高是90
                rawWidth = SRC_HEIGHT;
                rawHeight = SRC_WIDTH;
            }

            int centerIndex = rawWidth * (rawHeight / 2) + rawWidth / 2;
            long measureTimeStart = System.currentTimeMillis();
            String centerTempStr = mGuideInterface.measureTemByY16(y16Frame[centerIndex]);
            long measureTimeEnd = System.currentTimeMillis();
            //Log.d("GUIDE", "measureTemByY16 耗时" + (measureTimeEnd - measureTimeStart) + "ms");
            String centerTmepStrSDK = mGuideInterface.getCenterTemp();
            //Log.v("GUIDE"," centerTempStr="+centerTempStr +" centerTmepStrSDK="+centerTmepStrSDK);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFirmwareVersionTextView.setText("固件版本号： " + mGuideInterface.getFirmwareVersion());
                    mDisplayFrameLayout.removeView(mHighCrossView);
                    float scale = irSurfaceViewWidth / rawWidth;
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(highCrossWidth, highCrossHeight);

                    /*lp.leftMargin = (int) ((maxIndex % rawWidth) * scale - highCrossWidth / 2);
                    lp.topMargin = (int) ((maxIndex / rawWidth) * scale - highCrossHeight / 2);*/
                    int foreheadIndex = rawWidth * forehead_x + forehead_y;
                    lp.leftMargin = (int) ((foreheadIndex % rawWidth) * scale - highCrossWidth / 2);
                    lp.topMargin = (int) ((foreheadIndex / rawWidth) * scale - highCrossHeight / 2);
                    mDisplayFrameLayout.addView(mHighCrossView, lp);
                    LogUtils.a("绘制额头");
                }
            });

            count = 0;
        }


    }

    /*
      从y16数组中获取最大的Y16的值;
     */
    private short getMaxY16(short[] y16Arr) {

        short maxY16 = Short.MIN_VALUE;

        int length = y16Arr.length;

        for (int i = 0; i < length; i++) {

            if (maxY16 < y16Arr[i]) {
                maxY16 = y16Arr[i];
                maxIndex = i;
            }
        }

        return maxY16;

    }

    @Override
    public void usbConnect() {
        Toast.makeText(this, "usbConnect", Toast.LENGTH_LONG).show();
    }

    @Override
    public void usbDisConnect() {
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("USB断开");
        finish();
    }

    private String getAllTemp() {

        System.arraycopy(mY16Frame, 0, mSyncY16Frame, 0, mY16Frame.length);
        StringBuffer result = new StringBuffer();
        if (mGuideInterface != null) {
            float[] tempArray = new float[mY16Frame.length];
            mGuideInterface.getTempMatrix(tempArray, mY16Frame, SRC_WIDTH, SRC_HEIGHT);
            int length = mY16Frame.length - 1;
            for (int i = 0; i < length; i++) {
                String temp = BaseDataTypeConvertUtils.Companion.float2StrWithOneDecimal(tempArray[i]);
                result.append(temp + ",");
            }
            String tempLast = BaseDataTypeConvertUtils.Companion.float2StrWithOneDecimal(tempArray[mY16Frame.length - 1]);
            result.append(tempLast);
        }
        return result.toString();
    }

    public boolean writeFile(String filePath, String content) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, false);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }


    private String getCurrentTime() {

        Date date = new Date();

        String time = date.toLocaleString();

        Log.i("md", "时间time为： " + time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return dateFormat.format(date);

    }

    private void createFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        tempPath = sdcard.getAbsolutePath() + File.separator + "Guide" + File.separator;
        File file = new File(tempPath);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    public void verifyStoragePermissions() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanFile(Context context, String filePath) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveJpeg(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取升级目录
     */
    public void onUpgradePathClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    // TODO 不是primary的卷标，也返回一样的
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isDebugRecordDataing = false;
    private String recordDebugDataPath = null;


    /**
     * 体表温度
     */
    public void onTempBtnClick(View view) {
        isDispLayTemp = !isDispLayTemp;
        if (isDispLayTemp) {
            mDisplayFrameLayout.setVisibility(View.VISIBLE);
            //mEnvtempTextView.setVisibility(View.VISIBLE);
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        } else {
            mDisplayFrameLayout.setVisibility(View.GONE);
            mEnvtempTextView.setVisibility(View.GONE);

            mTimerTask.cancel();
            mTimerTask = null;
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void showTemp() {
        // mDisplayFrameLayout.setVisibility(View.VISIBLE);

        //mEnvtempTextView.setVisibility(View.VISIBLE);
      /*  mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);*/

        mHumanDisplayFrameLayout.setVisibility(View.VISIBLE);
        mHumanTimer = new Timer();
        mHumanTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
            }
        };
        mHumanTimer.schedule(mHumanTimerTask, 0, 1000);
    }

    /**
     * 快门
     */
    public void onShutterBtnClick(View view) {
        mGuideInterface.shutter();
    }

    /**
     * 色带
     */
    public void onPaletteBtnClick(View view) {
        paletteIndex++;
        if (paletteIndex == 11) {
            paletteIndex = 0;
        }
        mGuideInterface.changePalette(paletteIndex);
    }

    /**
     * 镜像
     */
    public void onFlipBtnClick(View view) {
        isFliptY = !isFliptY;
        mGuideInterface.setFilpY(isFliptY);
    }

    /**
     * 体表转体内温度
     */
    public void onHumanTempBtnClick(View view) {
        if (!TextUtils.isEmpty(mAmbientTempEditText.getText())) {
            String ambientTempStr = mAmbientTempEditText.getText().toString();
            float ambientTemp = Float.valueOf(ambientTempStr);
            if (ambientTemp < 10 || ambientTemp > 32) {
                Toast.makeText(this, "环境温度输入不合法", Toast.LENGTH_LONG).show();
                return;
            }
        }

        isDispLayHumanTemp = !isDispLayHumanTemp;
        if (isDispLayHumanTemp) {
            if (TextUtils.isEmpty(mAmbientTempEditText.getText())) {
                Toast.makeText(this, "正在使用内置算法得到的环境温度", Toast.LENGTH_LONG).show();
            }
            mHumanDisplayFrameLayout.setVisibility(View.VISIBLE);

            mHumanTimer = new Timer();
            mHumanTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            };
            mHumanTimer.schedule(mHumanTimerTask, 0, 1000);
        } else {
            mHumanDisplayFrameLayout.setVisibility(View.GONE);

            mHumanTimerTask.cancel();
            mHumanTimerTask = null;
            mHumanTimer.cancel();
            mHumanTimer = null;
        }
    }

    /**
     * 温度抓拍
     */
    public void onTempSnapBtnClick(final View view) {
        view.setClickable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                String tempData = getAllTemp();
                Log.d("zhaowei", "温度抓拍耗时 " + (System.currentTimeMillis() - start));
                String fileNamePath = tempPath + getCurrentTime() + ".dat";
                writeFile(fileNamePath, tempData);
                scanFile(ThermometryHWActivity.this, fileNamePath);

                String jpegFilePath = tempPath + getCurrentTime() + ".jpg";
                saveJpeg(mIrBitmap, jpegFilePath);
                scanFile(ThermometryHWActivity.this, jpegFilePath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThermometryHWActivity.this, "抓拍完成", Toast.LENGTH_SHORT).show();
                        view.setClickable(true);
                    }
                });
            }
        }).start();
    }

    /**
     * 设置距离
     */
    public void onDistanceBtnClick(View view) {
        if (TextUtils.isEmpty(mDistanceEditText.getText())) {
            mDistanceEditText.setText("");
            return;
        }
        String distanceStr = mDistanceEditText.getText().toString();
        float distance = Float.valueOf(distanceStr);
        if (distance < 0.5f || distance > 1.2f) {
            mDistanceEditText.setText("");
            return;
        }
        mGuideInterface.setDistance(distance);
    }


    /**
     * 设置亮度
     */
    public void onBrightBtnClick(View view) {
        if (TextUtils.isEmpty(mBrightEditText.getText())) {
            mBrightEditText.setText("");
            return;
        }

        int bright = -1;
        try {
            bright = Integer.valueOf(mBrightEditText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bright < 0 || bright > 100) {
            mBrightEditText.setText("");
            return;
        }
        mGuideInterface.setBright(bright);
    }

    /**
     * 设置对比度
     */
    public void onContrastBtnClick(View view) {
        if (TextUtils.isEmpty(mContrastEditText.getText())) {
            mContrastEditText.setText("");
            return;
        }

        int contrast = -1;
        try {
            contrast = Integer.valueOf(mContrastEditText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contrast < 0 || contrast > 100) {
            mContrastEditText.setText("");
            return;
        }
        mGuideInterface.setContrast(contrast);
    }

    /**
     * 获取SN
     */
    public void onSnBtnClick(View view) {
        mSNTextView.setText(mGuideInterface.getSN());
    }

    public void stopXOrder(View view) {
        mGuideInterface.sendStopXOrder();
    }


    /*
     * 发送reset指令
     */
    public void resetOrder(View view) {
        mGuideInterface.sendResetOrder();
    }

    private Timer nucTimer = null;
    private TimerTask nucTask = null;

    private void doNucLoopTest() {
        nucTimer = new Timer();
        nucTask = new TimerTask() {
            @Override
            public void run() {
                mGuideInterface.nucTest();
            }
        };
        nucTimer.schedule(nucTask, 0, 10 * 1000);
    }

    public void nucTest(View view) {
        Object object = view.getTag();
        if (object == null) {
            //第一次点击
            ((TextView) view).setText("正在nuc测试...");
            view.setTag(true);
            doNucLoopTest();
        } else {
            boolean isTest = (boolean) object;
            if (isTest) {
                ((TextView) view).setText("nuc test");
                view.setTag(false);
                if (nucTimer != null) {
                    nucTimer.cancel();
                    nucTimer = null;
                }
                if (nucTask != null) {
                    nucTask.cancel();
                    nucTask = null;
                }
            } else {
                ((TextView) view).setText("正在nuc测试...");
                view.setTag(true);
                doNucLoopTest();
            }
        }
    }

    /**
     * 升级
     */
    public void onUpgradeBtnClick(final View view) {
        view.setClickable(false);
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                if(mGuideInterface != null) {
                    String path = mUpgradePathTextView.getText().toString();
                    Log.d(TAG, "firmwareUpgrade start");
                    final FirmwareUpgradeResultCode code = mGuideInterface.firmwareUpgrade(path);
                    Log.d(TAG, "firmwareUpgrade: " + code.getMsg());
                    if(code == FirmwareUpgradeResultCode.SUCCESS) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //重置mcu程序;
                                if(mGuideInterface != null){
                                    mGuideInterface.sendResetOrder();
                                }
                                Toast.makeText(SwipingCardTemperatureActivity.this, code.getMsg(), Toast.LENGTH_SHORT).show();
                                view.setClickable(true);
                            }
                        } , 60 * 1000);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "FU " + code.getMsg());
                                Toast.makeText(SwipingCardTemperatureActivity.this, code.getMsg(), Toast.LENGTH_SHORT).show();
                                view.setClickable(true);
                            }
                        });
                    }
                }
            }
        }).start();*/
    }

    //***************************************专家模式**********************************************

    /**
     * 专家模式
     */
    public void onExpertModeClick(View view) {
        System.arraycopy(EXPERT_HITS, 1, EXPERT_HITS, 0, EXPERT_HITS.length - 1);
        EXPERT_HITS[EXPERT_HITS.length - 1] = System.currentTimeMillis();
        if (EXPERT_HITS[0] >= (System.currentTimeMillis() - EXPERT_MODE_HIT_DURATION)) {
            if (mExpertLayout.getVisibility() == View.GONE) {
                mExpertLayout.setVisibility(View.VISIBLE);
                /*if (mIrSurfaceView != null) {
                    mIrSurfaceView.setShowAjustView(true);
                }*/
            } else {
                mExpertLayout.setVisibility(View.GONE);
                if (mIrSurfaceView != null) {
                   // mIrSurfaceView.setShowAjustView(false);
                }
            }
            EXPERT_HITS = new long[EXPERT_MODE_HIT_COUNT];
        }
    }

    /**
     * 0.5米校温
     */
    public void onAjustTemp5Click(View view) {
        Toast.makeText(this, "请站在0.5米处校准温度", Toast.LENGTH_LONG).show();
        //初始化校温参数
        mNearKFEditText.setText(mGuideInterface.getNearKf() + "");
        mNearBEditText.setText(mGuideInterface.getNearB() + "");
        mGuideInterface.setDistance(0.5f);
    }

    /**
     * 1.2米校温
     */
    public void onAjustTemp12Click(View view) {
        Toast.makeText(this, "请站在1.2米处校准温度", Toast.LENGTH_LONG).show();
        mFarKFEditText.setText(mGuideInterface.getFarKf() + "");
        mFarBEditText.setText(mGuideInterface.getFarB() + "");
        mGuideInterface.setDistance(1.2f);
    }

    /**
     * 0.5米校温参数保存
     */
    public void onAjustTempSave5Click(View view) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(mNearKFEditText.getText())) {
                            short nearKf = Short.valueOf(mNearKFEditText.getText().toString());
                            mGuideInterface.setNearKf(nearKf);
                        }
                        if (!TextUtils.isEmpty(mNearBEditText.getText())) {
                            short nearB = Short.valueOf(mNearBEditText.getText().toString());
                            mGuideInterface.setNearB(nearB);
                        }
                        mHandler.sendEmptyMessage(3);
                    }
                }
        ).start();
    }

    /**
     * 1.2米校温参数保存
     */
    public void onAjustTempSave12Click(View view) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(mFarKFEditText.getText())) {
                            short farKf = Short.valueOf(mFarKFEditText.getText().toString());
                            mGuideInterface.setFarKf(farKf);
                        }
                        if (!TextUtils.isEmpty(mFarBEditText.getText())) {
                            short farB = Short.valueOf(mFarBEditText.getText().toString());
                            mGuideInterface.setFarB(farB);
                        }
                        mHandler.sendEmptyMessage(4);
                    }
                }
        ).start();
    }

    /**
     * SDK内部调试开关
     */
    public void onSDKDebugClick(View view) {
        if (!isSDKDebugStart) {
            ((TextView) view).setText("正在录制...");
            String path = tempPath + "mt_data.txt";
            mGuideInterface.startSDKDebug(tempPath, path);
            isSDKDebugStart = true;
        } else {
            ((TextView) view).setText("SDK开发调试开关");
            mGuideInterface.stopSDKDebug();
            isSDKDebugStart = false;
        }
    }

    /**
     * 录制数据
     */
    public void onRecordBtnClick(View view) {
        recordDebugDataPath = tempPath + "record_" + getCurrentTime() + ".txt";
        if (!isDebugRecordDataing) {
            File file = new File(recordDebugDataPath);
            if (file.exists()) {
                file.delete();
            }

            mRecordDebugData.setText("正在录制...");

            mGuideInterface.startRecordDebugData(recordDebugDataPath);
        } else {
            mGuideInterface.stopRecordDebugData();
            mRecordDebugData.setText("录制数据");
        }
        isDebugRecordDataing = !isDebugRecordDataing;
    }

    public void onAjustT1Click(View view) {
        showEditTextDialog("请输入黑体1的真实温度", mAjustT1EditText);
    }

    public void onAjustT2Click(View view) {
        showEditTextDialog("请输入黑体2的真实温度", mAjustT1EditText);
    }

    public void onAjustT1PositionClick(View view) {
        if (mIrSurfaceView != null) {
           // mIrSurfaceView.setMoveIndex(0);
            Toast.makeText(ThermometryHWActivity.this, "请开始设置", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAjustT2PositionClick(View view) {
        if (mIrSurfaceView != null) {
          //  mIrSurfaceView.setMoveIndex(1);
            Toast.makeText(ThermometryHWActivity.this, "请开始设置", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAjustDistance5Click(View view) {
        Toast.makeText(this, "请站在0.5米处校准温度", Toast.LENGTH_SHORT).show();
        mGuideInterface.setDistance(0.5f);
    }

    public void onAjustAuto5Click(View view) {
        Toast.makeText(this, "启动0.5米自动校温", Toast.LENGTH_SHORT).show();
    }

    public void onAjustDistance12Click(View view) {
        Toast.makeText(this, "请站在1.2米处校准温度", Toast.LENGTH_SHORT).show();
        mGuideInterface.setDistance(1.2f);
    }

    public void onAjustAuto12Click(View view) {
        Toast.makeText(this, "启动1.2米自动校温", Toast.LENGTH_SHORT).show();
    }

    //***************************************专家模式**********************************************

    /**
     * 初始化设置入口
     */
    public void onSettingBtnClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void onSettingPaletteClick(View view) {
        String[] items = getResources().getStringArray(R.array.palette);
        showSingleChoiceDialog(items, mSettingView.getPaletteView());
    }


    public void onSettingScaleClick(View view) {
        String[] items = getResources().getStringArray(R.array.scale);
        showSingleChoiceDialog(items, mSettingView.getScaleView());
    }


    public void onSettingRotateClick(View view) {
        String[] items = getResources().getStringArray(R.array.rotate);
        showSingleChoiceDialog(items, mSettingView.getRotateView());
    }

    public void onSettingImageAlgoClick(View view) {
        String[] items = getResources().getStringArray(R.array.switch_array);
        showSingleChoiceDialog(items, mSettingView.getImageAlgoView());
    }

    public void onSettingAutoShutterClick(View view) {
        String[] items = getResources().getStringArray(R.array.switch_array);
        showSingleChoiceDialog(items, mSettingView.getAutoShutterSwitchView());
    }


    public void onSettingPeriodClick(View view) {
        showEditTextDialog("请输入时间间隔（毫秒）", mSettingView.getPeriodView());
    }


    public void onSettingDelayClick(View view) {
        showEditTextDialog("请输入延时（毫秒）", mSettingView.getDelayView());
    }

    private void showSingleChoiceDialog(final String[] items, final TextView view) {
        int checkedIndex = 0;
        for (int i = 0; i < items.length; i++) {
            String value = view.getText().toString();
            if (TextUtils.equals(items[i], value)) {
                checkedIndex = i;
                break;
            }
        }

    }

    private void showEditTextDialog(final String hint, final TextView view) {

    }


}
