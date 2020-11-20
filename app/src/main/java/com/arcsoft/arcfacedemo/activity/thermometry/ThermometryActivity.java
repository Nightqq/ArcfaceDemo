package com.arcsoft.arcfacedemo.activity.thermometry;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.kingsun.KingsunSmartAPI;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
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
import com.arcsoft.arcfacedemo.util.communi.SerialPortUtils;
import com.arcsoft.arcfacedemo.util.face.FaceHelper;
import com.arcsoft.arcfacedemo.util.face.FaceListener;
import com.arcsoft.arcfacedemo.util.face.RequestFeatureStatus;
import com.arcsoft.arcfacedemo.util.image.ImageBase64Utils;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.AppExecutors;
import com.arcsoft.arcfacedemo.util.utils.CaptureUtil;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DrawHelper;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class ThermometryActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometry);
        ButterKnife.bind(this);
        initView();
        //本地人脸库初始化
        LogUtils.a("日志", "本地人脸库初始化");
        FaceServer.getInstance().init(this);
        LogUtils.a("日志", "测温硬件初始化");
        SerialPortUtils.gethelp().openSerialPort();
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
        }else {
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

    private ThermometryActivity.networkBroadcast networkBroadcast;

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
        api.setStatusBar(false);
        SIMILAR_THRESHOLD = TerminalInformationHelp.getTerminalInformation().getRecognitionThreshold();
        fail_num = TerminalInformationHelp.getTerminalInformation().getRecognitionNum();

        SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
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
                       /* handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RequestHelper.getRequestHelper().uploadWenDu();
                            }
                        }, 300);*/
                        displaysubtitles(2);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("异常");
                    }
                }
            }
        });
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
                        .currentTrackId(ConfigUtil.getTrackId(ThermometryActivity.this.getApplicationContext()))
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
                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0){
                    //
                    Rect rect1 = drawHelper.adjustRect(facePreviewInfoList.get(facePreviewInfoList.size() - 1).getFaceInfo().getRect());
                    LogUtils.a("center坐标", rect1.centerX() + "===" + rect1.centerY());
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

                    LogUtils.a("坐标宽高", rect.top + "=" + rect.bottom + "=" + rect.left + "=" + rect.right);
                    if (rect.top < 0 || rect.bottom > 1000) {//人脸框顺旋转90度为实际
                        trackId = 0;
                    }
                    LogUtils.a("人脸框坐标",rect.centerX()+"==="+ rect.centerY()+"==="+ rect.width()+"==="+ rect.height());
                    LogUtils.a("人脸框边框",rect.top+"==="+ rect.bottom+"==="+ rect.left+"==="+ rect.right);
                    if (rect.centerX() < 800 && rect.centerY() > 250 && rect.centerY() < 730&&width>280) {//在指定框内切人脸框大于280
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
                    }
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
                        jumpcewen();//识别成功测温
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

    //流程3测温
    public void jumpcewen() {
        LogUtils.a("日志", "测温串口发送");
        //SerialPort.gethelp().sendmoni();
        // LogUtils.a("发送测温命令", SwitchUtils.byte2HexStr(cewen));
        byte[] cewen = SwitchUtils.hexStringToByte("F04F01EFEE");
        SerialPortUtils.gethelp().sendSerialPort(cewen);//发送测温命令
       /* new Thread() {
            @Override
            public void run() {
                super.run();
                //wendu_num++;
                byte[] cewen = SwitchUtils.hexStringToByte("F04F01EFEE");
                SerialPortUtils.gethelp().sendSerialPort(cewen);//发送测温命令
            }
        }.start();*/
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
                    Intent intent = new Intent(ThermometryActivity.this, SettingActivity.class);
                    intent.putExtra("mode", 1);
                    startActivity(intent);
                } else if (text.equals("njzx")) {//2干警
                    api.setStatusBar(true);
                    Intent intent = new Intent(ThermometryActivity.this, SettingActivity.class);
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
       // TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("每天定时更新开启");
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



    private  void restartApp() {
        Intent intent2 = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
    }

}
