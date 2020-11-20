package com.arcsoft.arcfacedemo.activity.thermometry;

import android.Manifest;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.activity.setting.SettingActivity;
import com.arcsoft.arcfacedemo.dao.bean.CeWenInform;
import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;
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
import com.arcsoft.arcfacedemo.util.utils.CaptureUtil;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DrawHelper;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
import com.arcsoft.arcfacedemo.widget.ShowFaceInfoAdapter;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Thermometry2Activity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {


    private static final int MAX_DETECT_NUM = 2;
    public static final String TAG_EXIT = "exit";
    public static final String TAG_RESTART = "restart";
    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */

    private static final int WAIT_LIVENESS_INTERVAL = 50;
    @BindView(R.id.face_recognition_texturepreview)
    TextureView previewView;
    @BindView(R.id.face_recognition_facerectview)
    FaceRectView faceRectView;
    @BindView(R.id.face_recognition_tectview)
    TextView faceRecognitionTectview;

    @BindView(R.id.face_recognition_imgview)
    ImageView faceRecognitionImgview;
    @BindView(R.id.face_recognition_Linear)
    LinearLayout faceRecognitionLinear;
    @BindView(R.id.face_contrast_rotate)
    ImageView faceContrastRotate;
    @BindView(R.id.face_recognition_FrameLayout)
    FrameLayout faceRecognitionFrameLayout;
    @BindView(R.id.face_recognition_subtitles)
    TextView faceRecognitionSubtitles;
    @BindView(R.id.activity_face_recognition_networkState)
    TextView activityFaceRecognitionNetworkState;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    /**
     * 优先打开的摄像头，本界面主要用于单目RGB摄像头设备，因此默认打开前置
     */
    private Integer rgbCameraID = 0;
    private FaceEngine faceEngine;
    private FaceHelper faceHelper;
    private List<CompareResult> compareResultList;
    private ShowFaceInfoAdapter adapter;


    private int afCode = -1;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private KingsunSmartAPI api;

    private static float SIMILAR_THRESHOLD = 0.82F;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    private Handler handler = new Handler() {
    };

    private int fail_num = 5;

    //人脸识别识别退出
    int refresh_flag = 0;
    Runnable refresh = new Runnable() {
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
            }
        }
    };
    //定时清理摄像机换成猜测第一次刷卡问题摄像机卡顿
    Runnable clear = new Runnable() {
        @Override
        public void run() {
            LogUtils.a("日志", "clear");
           /* if (requestFeatureStatusMap != null) {
                requestFeatureStatusMap.clear();//清除预览人脸
            }
            if (livenessMap != null) {
                livenessMap.clear();
            }
            if (compareResultList != null) {
                compareResultList.clear();
            }*/
            // clearLeftFace(null);
            // handler.postDelayed(clear, 1000 * 60 * 3);
        }
    };

    //定时显示一秒字幕再推出
    Runnable runnable_time_display = new Runnable() {
        @Override
        public void run() {
            LogUtils.a("日志", "runnable_time_display");
            //退出至等待页面
            App.castMemory();//刷卡人脸置空
            compareNum = 0;
            compareSimilar = 0;
            faceRecognitionSubtitles.setVisibility(View.GONE);
            startFacerecognition();
        }
    };
    //延迟测温
    Runnable runnable_cewen = new Runnable() {
        @Override
        public void run() {
            LogUtils.a("日志", "定时延迟测温");
            jumpcewen();
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //LogUtils.a("更新页面时间");
            //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("更新页面");
            LogUtils.a("日志", "runnable");
            visibilityLayout();
            faceRecognitionTectview.setText("手动测温");
            faceRecognitionTectview.setVisibility(View.GONE);
        }
    };

    Runnable refresh_network_status = new Runnable() {
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
            if (istest) {
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(message);
            }
        }
    };
    private Thermometry2Activity.networkBroadcast networkBroadcast;

    private MediaProjection mMediaProjection;

    //测温
    public void jumpcewen() {
        wendu_num++;
        recognitionstate = 1;
        LogUtils.a("日志", "测温串口发送");
        byte[] cewen = SwitchUtils.hexStringToByte("F04F01EFEE");
        LogUtils.a("发送测温命令", SwitchUtils.byte2HexStr(cewen));
        SerialPortUtils.gethelp().sendSerialPort(cewen);//发送测温命令
    }

    public void jumpTocewen(View view) {
        jumpcewen();//手动测温
    }

    public byte getbyte(String string) {
        return (byte) Integer.parseInt(string);
    }

    private class networkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                handler.post(refresh_network_status);
            }
        }
    }


    //等待识别中0，正在识别中1,识别结束2，人脸搜索中3
    public int recognitionstate = 0;


    //是否测试模式标志
    public boolean istest = false;
    MediaProjectionManager mMediaProjectionManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        ButterKnife.bind(this);
        LogUtils.a("日志", "initView()");
        initView();
        //本地人脸库初始化
        LogUtils.a("日志", "本地人脸库初始化");
        FaceServer.getInstance().init(this);
        LogUtils.a("日志", "测温硬件初始化");
        SerialPortUtils.gethelp().openSerialPort();
        //jumpshuaka();
        //开启定时重启广播
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 1);
        }

        LogUtils.a("日志", "onCreate结束");

    }

    public void visibilityLayout() {
        LogUtils.a("日志", "visibilityLayout");
        //  TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("状态"+recognitionstate);
        faceRecognitionImgview.setVisibility(View.VISIBLE);
        faceRecognitionLinear.setVisibility(View.VISIBLE);
        faceRecognitionFrameLayout.setVisibility(View.GONE);
        faceContrastRotate.setVisibility(View.GONE);

    }


    //0失败，1成功,2异常
    public void displaysubtitles(int i) {
        LogUtils.a("日志", "displaysubtitles");
        compareNum = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                }
            }
        });
        handler.postDelayed(runnable_time_display, 1000);//延迟一秒执行
     /*   String format = new DecimalFormat("0.000").format(compareSimilar);
        //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("相似度：" + format);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String format1 = simpleDateFormat.format(date);
        String time = simpleDateFormattime.format(date);
        String content = "日期：" + format1 + "时间：" + time + "卡号：" + "无" + "相似度：" + format + "结果：" + i + "\n";
        FileUtils.getFileUtilsHelp().savaSimilarityLog(content);*/
    }


    boolean wendu_flag = true;
    int wendu_num = 1;


    @Override
    protected void onStart() {
        super.onStart();
        if (api == null) {
            api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
            api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
        }
        SIMILAR_THRESHOLD = TerminalInformationHelp.getTerminalInformation().getRecognitionThreshold();
        fail_num = TerminalInformationHelp.getTerminalInformation().getRecognitionNum();
        //api.setStatusBar(false);
        startFacerecognition();
        // handler.postDelayed(clear, 1000 * 60 * 3);
        SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer) {
                LogUtils.a("日志", "onDataReceive byte【】");
            }

            @Override
            public void onDataReceive(String buffer) {
                LogUtils.a("日志", "onDataReceive");

                if (buffer.equals("超距")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("超距");
                } else {
                    float wendu = Float.parseFloat(buffer);
                    TemperatureSetting temperatureSetting = TemperatureSettingHelp.getTerminalInformation();
                    LogUtils.a("日志", "温度判断");
                    if (wendu < Float.parseFloat(temperatureSetting.getWenxia())) {//低于温度下限测三次
                        if (wendu_num >= 5) {
                            LogUtils.a("日志", "温度异常重测");
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(wendu + "请虫测");//文字转语音，多音字读重zhong
                            wendu_num = 0;
                            //重新人脸识别
                            displaysubtitles(2);
                            name = "";
                            return;
                        }
                        handler.postDelayed(runnable_cewen, 340);//延迟测温
                        return;
                    }
                    if (wendu < Float.parseFloat(temperatureSetting.getWenshang())) {//正常温度
                        if (wendu_flag) {
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(wendu + "");
                        }
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setTemperature(buffer);
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        //上传
                        RequestHelper.getRequestHelper().uploadWenDu();
                        displaysubtitles(1);
                        return;
                    } else {//高于温度上限异常提示
                        CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                        ceWenInform.setTemperature(buffer);
                        CeWenHelp.saveCeWenInform(ceWenInform);
                        //上传
                        RequestHelper.getRequestHelper().uploadWenDu();
                        displaysubtitles(2);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(wendu + "异常");
                    }
                }
                recognitionstate = 0;
                api.controlLight("01");
            }
        });

    }

    public void startFacerecognition() {
        LogUtils.a("日志", "startFacerecognition");
       /* requestFeatureStatusMap.clear();//清除预览人脸
        livenessMap.clear();
        compareResultList.clear();*/
        clearLeftFace(null);
        recognitionstate = 0;
        //handler.post(runnable);//刷卡进入识别状态
    }

    private void initView() {
        visibilityLayout();
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person);
        compareResultList = new ArrayList<>();
        adapter = new ShowFaceInfoAdapter(compareResultList, this);
        recyclerShowFaceInfo.setAdapter(adapter);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
        recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
        recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());
        // openBroadcast();//网络状态监听广播
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("");
    }


    public void openBroadcast() {
        LogUtils.a("日志", "openBroadcast");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//连上与否
        networkBroadcast = new Thermometry2Activity.networkBroadcast();
        this.registerReceiver(networkBroadcast, intentFilter);
    }


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


    private static boolean rectXY = true;

    public static void setRectXY(boolean flag) {
        rectXY = flag;
    }

    public static boolean getetRectXY() {
        return rectXY;
    }

    private void initCamera() {
        LogUtils.e("日志", "initCamera");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                // LogUtils.a("onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                LogUtils.a("日志", "requestFaceFeature返回");
                //FR成功
                if (faceFeature != null) {
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    //活体检测通过，搜索特征
                    LogUtils.a("日志", "onFaceFeatureInfoGetfaceFeature != null");
                    // handler.removeCallbacks(clear);
                    //LogUtils.a("日志", "removeCallbacks(clear)");
                    //handler.postDelayed(clear, 1000 * 60 * 3);
                    // LogUtils.a("日志", "开启定时(clear)");

                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        //LogUtils.a("活体检测通过，搜索特征");
                        compareFace(faceFeature, requestId);
                    }  //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }

                   /* if (rectXY){
                        LogUtils.a("日志","开始对比");
                        compareFace(faceFeature, requestId);
                    }else {

                        LogUtils.a("日志"," put(requestId, RequestFeatureStatus.FAILED)");
                       *//* requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        LogUtils.a("日志","requestFeatureStatusMap.clear();");
                        requestFeatureStatusMap.clear();
                        LogUtils.a("日志"," livenessMap.clear();");
                        livenessMap.clear();*//*
                        clearLeftFace(null);
                    }*/
                    LogUtils.a("日志", " if (rectXY)结束");
                    /*  if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                     *//*LogUtils.a("活体检测通过，搜索特征");
                        compareFace(faceFeature, requestId);*//*
                    }//活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        LogUtils.a("活体检测未通过");
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        LogUtils.a("日志","活体检测失败");
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }*/
                }
                //FR 失败
                else {
                    LogUtils.a("日志", "faceFeature = null");
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
                        .currentTrackId(ConfigUtil.getTrackId(Thermometry2Activity.this.getApplicationContext()))
                        .build();
                LogUtils.e("日志", "onCameraOpened结束");
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                LogUtils.a("日志", "onPreview");
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                //float similarity = FaceUtils.getFaceUtils().similarity(App.byteface, nv21, camera.getParameters().getPreviewSize());
                // LogUtils.a("人脸相似度："+similarity);
              /*  if (nv21.length != 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(nv21, 0, nv21.length);
                    int w = bitmap.getWidth(); // 得到图片的宽，高
                    int h = bitmap.getHeight();
                    Bitmap.createBitmap(bitmap,w/2,0,w/2,h/2);
                    Bitmap.createScaledBitmap(bitmap,w,h, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] datas = baos.toByteArray();
                }*/
                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);
                /*if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    LogUtils.a("日志", "开始绘制人脸框");
                    drawPreviewInfo(facePreviewInfoList);
                }*/

                clearLeftFace(facePreviewInfoList);
                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        Rect rect = facePreviewInfoList.get(i).getFaceInfo().getRect();
                        livenessMap.put(facePreviewInfoList.get(i).getTrackId(), facePreviewInfoList.get(i).getLivenessInfo().getLiveness());
                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        LogUtils.a("坐标", rect.centerX() + "===" + rect.centerY());
                        if (rect.centerX() < 800 && rect.centerY() > 100 && rect.centerY() < 650) {
                            if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                    || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                                //LogUtils.a("requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null");
                                requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                                LogUtils.a("日志", "requestFaceFeature");
                                faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                            }
                        }
                    }
                } else if (previewSize == null) {
                    LogUtils.a("日志", "previewSize == null");
                }
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

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
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

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        LogUtils.a("日志", "drawPreviewInfo");
        List<DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness == null ? LivenessInfo.UNKNOWN : liveness,
                    name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
        }
        // drawHelper.draw(faceRectView, drawInfoList);
        LogUtils.a("日志", "drawPreviewInfo结束");
    }

    @Override
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

    //比较相似度次数
    int compareNum = 0;
    float compareSimilar = 0;
    String name = "";//上一次识别人
    long time = 0;

    private void compareFace(FaceFeature faceFt, Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
                if (recognitionstate != 0) {
                    return;
                }
                if (compareNum >= fail_num) {
                    displaysubtitles(0);
                    return;
                }
                // LogUtils.a("开始人脸识别");
                recognitionstate = 3;
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFt);
                LogUtils.a("日志", "对比获取相似度");
                recognitionstate = 1;

                if (compareResult == null) {
                    recognitionstate = 0;
                    emitter.onError(null);
                } else {
                    // LogUtils.a(compareResult.getUserName(), compareResult.getSimilar());
                    compareSimilar = compareSimilar > compareResult.getSimilar() ? compareSimilar : compareResult.getSimilar();
                    emitter.onNext(compareResult);
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null || recognitionstate != 1) {
                            recognitionstate = 0;
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            return;
                        }
                        LogUtils.a("日志", "相似度结果比较");
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            String userName = compareResult.getUserName();
                            long timeing = System.currentTimeMillis();
                            long timeD = timeing - time;
                            if (userName.equals(name) && timeD >= 0 && timeD < 3000) {
                                LogUtils.a("日志", "识别人名重复3秒内");
                                //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请勿虫复识别");
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                recognitionstate = 0;
                                return;
                            }
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(userName);
                            name = userName;
                            time = timeing;

                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                recognitionstate = 0;
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                LogUtils.a("日志", "多人脸？");
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                    adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                                adapter.notifyItemInserted(compareResultList.size() - 1);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUserName());
                            // LogUtils.a("识别成功时间")
                            wendu_num = 0;
                            jumpcewen();//测温
                            //上传照片
                            takePictures();
                        } else {
                            compareNum++;
                            recognitionstate = 0;
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        recognitionstate = 0;
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void takePictures() {
        LogUtils.a("日志", "开始截图获取照片");
        handler.post(new Runnable() {
            @Override
            public void run() {
                CaptureUtil captureUtil = new CaptureUtil().setUpMediaProjection( mMediaProjection);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = captureUtil.startCapture();
                            if (bitmap != null) {
                                LogUtils.a("日志", "照片返回存储");
                             /*   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                Date date = new Date(System.currentTimeMillis());
                                String format = simpleDateFormat.format(date);*/
                                CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                                ceWenInform.setPhoto(ImageBase64Utils.getBitmapByte(bitmap));
                                ceWenInform.setTime(System.currentTimeMillis() + "");
                                CeWenHelp.saveCeWenInform(ceWenInform);
                                //FileUtils.getFileUtilsHelp().saveMyBitmap(bitmap);
                            } else {
                                LogUtils.a("日志", "存储无人脸");
                                CeWenInform ceWenInform = CeWenHelp.getCeWenInform();
                                ceWenInform.setnoPhoto();
                                ceWenInform.setTime(System.currentTimeMillis() + "");
                                CeWenHelp.saveCeWenInform(ceWenInform);
                                LogUtils.a("无人脸");
                            }
                        } catch (NullPointerException r) {
                            LogUtils.a("图片", r.getMessage().toString());
                            r.printStackTrace();
                        }
                    }
                }, 300);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mResultCode = resultCode;
        //mResultData = data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        }
    }


    @Override
    protected void onDestroy() {
        LogUtils.a("日志", "onDestroy");
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        if (networkBroadcast != null) {
            unregisterReceiver(networkBroadcast);
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
        //  FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    public void jumptoSetting(View view) {
        if (isFastDoubleClick()) {
            showdkdialog();
        }
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

    private boolean isswipingcardClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (timeD >= 1000) {
            return true;
        } else {
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
                    Intent intent = new Intent(Thermometry2Activity.this, SettingActivity.class);
                    intent.putExtra("mode", 1);
                    startActivity(intent);
                } else if (text.equals("njzx")) {//2干警
                    api.setStatusBar(true);
                    Intent intent = new Intent(Thermometry2Activity.this, SettingActivity.class);
                    intent.putExtra("mode", 2);
                    startActivity(intent);
                } else if (text.equals("wendu")) {
                    wendu_flag = !wendu_flag;
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

        Runnable dialogrunnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        handler.postDelayed(dialogrunnable, 20000);
    }

    //结束程序
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.a("日志", "onNewIntent");
        if (intent != null) {
            String isExit = intent.getStringExtra(TAG_EXIT);
            if (isExit != null && isExit.equals(TAG_EXIT)) {//退出
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                }
            } else if (isExit != null && isExit.equals(TAG_RESTART)) {//重启
                Intent intent2 = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                System.exit(0);
            }
        }
    }

}
