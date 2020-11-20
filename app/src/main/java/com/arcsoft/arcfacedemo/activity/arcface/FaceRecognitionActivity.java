package com.arcsoft.arcfacedemo.activity.arcface;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.hardware.Camera;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.activity.setting.SettingActivity;
import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;
import com.arcsoft.arcfacedemo.dao.helper.PoliceFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.model.FacePreviewInfo;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.net.bean.JsonPolicePhoto;
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
import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SoundPlayer;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class FaceRecognitionActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {


    private static final int MAX_DETECT_NUM = 10;
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
    // private ShowFaceInfoAdapter adapter;


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
    private long out_time = 4000;
    private int fail_num = 3;
    //定时退出
    Runnable runnable_time_out = new Runnable() {
        @Override
        public void run() {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("未检测到人脸，请重新刷卡");
            displaysubtitles(2);
        }
    };
    //人脸识别识别退出
    Runnable runnable_recognition_fail = new Runnable() {
        @Override
        public void run() {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别失败");
            displaysubtitles(0);
        }
    };
    //定时清理摄像机换成猜测第一次刷卡问题摄像机卡顿
    Runnable clear = new Runnable() {
        @Override
        public void run() {
            if (requestFeatureStatusMap != null) {
                requestFeatureStatusMap.clear();//清除预览人脸
            }
            if (livenessMap != null) {
                livenessMap.clear();
            }
            if (compareResultList != null) {
                compareResultList.clear();
            }
            handler.postDelayed(clear, 1000 * 60 * 2);
        }
    };

    //定时显示一秒字幕再推出
    Runnable runnable_time_display = new Runnable() {
        @Override
        public void run() {
            //退出至等待页面
            recognitionstate = 0;
            visibilityLayout();//更新进入等待页面
            App.castMemory();//刷卡人脸置空
            compareNum = 0;
            compareSimilar = 0;
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //LogUtils.a("更新页面时间");
            //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("更新页面");
            visibilityLayout();
            activityFaceRecognitionNetworkState.setVisibility(View.GONE);
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(App.police_name);
            faceRecognitionTectview.setText(App.police_name + "识别中");
        }
    };

    Runnable refresh_network_status = new Runnable() {
        @Override
        public void run() {
            int netWorkState = NetWorkUtils.getNetworkState();
            String message = "";
            switch (netWorkState) {
                case 0:
                    message = "网络状态：异常";
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
                    message = "网络状态：正常";
                    activityFaceRecognitionNetworkState.setVisibility(View.GONE);
                    // activityFaceRecognitionNetworkState.setText(message);
                    //activityFaceRecognitionNetworkState.setTextColor(Color.parseColor("#00ff00"));
                    break;
            }
            if (istest) {
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(message);
            }
        }
    };
    private FaceRecognitionActivity.networkBroadcast networkBroadcast;

    private MediaProjection mMediaProjection;



    public byte getbyte(String string){
        return (byte)Integer.parseInt(string);
    }

    public void jumpTocewen(View view) {
    }


    private class networkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                handler.post(refresh_network_status);
            }
        }
    }


    //等待中0（可刷卡）,识别中1（不接受刷卡）,识别结束2（不接受刷卡），
    public int recognitionstate = 0;


    //是否测试模式标志
    public boolean istest = false;
    MediaProjectionManager mMediaProjectionManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        ButterKnife.bind(this);
        initView();
        //本地人脸库初始化
        FaceServer.getInstance().init(this);
        SerialPortUtils.gethelp().openSerialPort();
        //jumpshuaka();
        //初始化音效
        SoundPlayer.init(this);
        //开启定时重启广播

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 1);
        }
    }
    public void visibilityLayout() {
        //  TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("状态"+recognitionstate);
        if (recognitionstate == 1) {//进入识别页面
            handler.removeCallbacks(clear);
            faceRecognitionImgview.setVisibility(View.VISIBLE);
            faceRecognitionLinear.setVisibility(View.VISIBLE);
            faceRecognitionFrameLayout.setVisibility(View.GONE);
            faceContrastRotate.setVisibility(View.GONE);
        } else if (recognitionstate == 0) {//进入等待页面
            faceRecognitionSubtitles.setVisibility(View.GONE);
            faceRecognitionImgview.setVisibility(View.GONE);
            faceRecognitionLinear.setVisibility(View.GONE);
            faceRecognitionFrameLayout.setVisibility(View.VISIBLE);
            faceContrastRotate.setVisibility(View.VISIBLE);
            handler.postDelayed(clear, 1000 * 60 * 2);

        }
    }
    //0失败，1成功，2超时
    public void displaysubtitles(int i) {
        recognitionstate = 2;
        handler.removeCallbacks(runnable_time_out);

        String format = new DecimalFormat("0.000").format(compareSimilar);
        //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("相似度：" + format);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String format1 = simpleDateFormat.format(date);
        String time = simpleDateFormattime.format(date);
        String content = "日期：" + format1 + "时间：" + time + "卡号：" + policeNum + "相似度：" + format + "结果：" + i + "\n";
        FileUtils.getFileUtilsHelp().savaSimilarityLog(content);

        handler.postDelayed(runnable_time_display, 1000);//延迟一秒执行
        switch (i) {
            case 0:
                faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                faceRecognitionSubtitles.setText("识别失败");
                faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                api.controlLight("01");
                break;
            case 1:
                faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                faceRecognitionSubtitles.setText("识别成功");
                faceRecognitionSubtitles.setTextColor(Color.parseColor("#00ff00"));
                api.controlLight("02");
                break;
            case 2:
                faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                faceRecognitionSubtitles.setText("识别超时");
                faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                api.controlLight("01");
                break;
        }
    }

    private String policeNum;

    @SuppressLint("WrongConstant")
    @Override
    protected void onStart() {
        super.onStart();
        //旋转动画
        LogUtils.a("getbdy开始");
     /*   Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        faceContrastRotate.startAnimation(animation);*/
        if (api == null) {
            api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
            api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
        }
        SIMILAR_THRESHOLD = TerminalInformationHelp.getTerminalInformation().getRecognitionThreshold();
        out_time = TerminalInformationHelp.getTerminalInformation().getOutTime();
        fail_num = TerminalInformationHelp.getTerminalInformation().getRecognitionNum();
        //api.setStatusBar(false);
        SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer) {
                if (recognitionstate == 0 && isswipingcardClick()) {
                    //播放声音
                    SoundPlayer.play(1);
                    policeNum = SwitchUtils.byte2HexStr(buffer).replaceAll(" ", "");
                    LogUtils.a("收到卡号时间：" + policeNum);
                    //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(policeNum);
                    RequestHelper.getRequestHelper().getPoliceFace( policeNum, new RequestHelper.OpenDownloadListener() {
                        @Override
                        public void openDownload(String message) {
                            if (message.equals("下载成功")) {
                                startFacerecognition();
                            } else {//识别使用本地数据
                                if (message.equals("服务器连接超时")) {
                                    activityFaceRecognitionNetworkState.setVisibility(View.VISIBLE);
                                    activityFaceRecognitionNetworkState.setText("服务器连接超时");
                                    activityFaceRecognitionNetworkState.setTextColor(Color.parseColor("#ff0000"));
                                }
                                PoliceFace policeFace = PoliceFaceHelp.getPoliceFaceByNum(policeNum);
                                if (policeFace != null) {
                                    if (policeFace.getEMP_FEATURE() != null) {
                                        App.byteface = SwitchUtils.base64tobyte(policeFace.getEMP_FEATURE());
                                        App.police_name = policeFace.getEMP_NAME();
                                        startFacerecognition();
                                    } else {
                                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("此卡无人脸数据");
                                    }
                                } else {
                                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(message);
                                }
                            }
                        }
                    });
/*
                    PoliceFace policeFace = PoliceFaceHelp.getPoliceFaceByNum(policeNum);
                    if (policeFace != null) {
                        if (policeFace.getEMP_FEATURE() != null) {
                            App.byteface = SwitchUtils.base64tobyte(policeFace.getEMP_FEATURE());
                            App.police_name = policeFace.getEMP_NAME();
                            if (policeFace.getEMP_TYPE() == 1 || policeFace.getEMP_TYPE() == 2 || policeFace.getEMP_TYPE() == 5 || policeFace.getEMP_TYPE() == 6) {
                                if (App.byteface.length != 1032) {
                                    textToSpeechUtils.notifyNewMessage("人脸数据异常");
                                }
                                startFacerecognition();
                            } else {//外来人员
                                RequestHelper.getRequestHelper().getPoliceFace(policeNum, new RequestHelper.OpenDownloadListener() {
                                    @Override
                                    public void openDownload(String message) {
                                        if (message.equals("下载成功")) {
                                            startFacerecognition();
                                        } else {
                                            textToSpeechUtils.notifyNewMessage(message);
                                            if (App.byteface != null) {
                                                startFacerecognition();
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            textToSpeechUtils.notifyNewMessage("此卡无人脸数据");
                        }
                    } else {
                        textToSpeechUtils.notifyNewMessage("数据同步中");
                        LogUtils.a("联网获取卡号时间");
                        RequestHelper.getRequestHelper().getPoliceFace(policeNum, new RequestHelper.OpenDownloadListener() {
                            @Override
                            public void openDownload(String message) {
                                if (message.equals("下载成功")) {
                                    startFacerecognition();
                                } else {
                                    textToSpeechUtils.notifyNewMessage(message);
                                }
                            }
                        });
                    }*/
                }
            }

            @Override
            public void onDataReceive(String buffer) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(clear);
    }

    public void startFacerecognition() {
        requestFeatureStatusMap.clear();//清除预览人脸
        livenessMap.clear();
        compareResultList.clear();
        recognitionstate = 1;
        handler.post(runnable);//刷卡进入识别页面
        handler.postDelayed(runnable_time_out, out_time);
    }

    private void initView() {
        recognitionstate = 0;
        visibilityLayout();
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person);
        compareResultList = new ArrayList<>();
        // adapter = new ShowFaceInfoAdapter(compareResultList, this);
        //recyclerShowFaceInfo.setAdapter(adapter);
        // DisplayMetrics dm = getResources().getDisplayMetrics();
        // int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
        //recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
        //recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());
        openBroadcast();//网络状态监听广播
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("");
    }


    public void openBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//连上与否
        networkBroadcast = new networkBroadcast();
        this.registerReceiver(networkBroadcast, intentFilter);
    }


    @Override
    public void onGlobalLayout() {
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
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            LogUtils.a("unInitEngine: " + afCode);
        }
    }

    private void initCamera() {
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
                //FR成功
                if (faceFeature != null) {
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    //活体检测通过，搜索特征
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        if (App.byteface == null) {
                            return;
                        }
                        //LogUtils.a("活体检测通过，搜索特征");
                        compareFace(faceFeature, requestId);
                    }
                    //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }
                }
                //FR 失败
                else {
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, false, false, false);
                faceHelper = new FaceHelper.Builder()
                        .faceEngine(faceEngine)
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .currentTrackId(ConfigUtil.getTrackId(FaceRecognitionActivity.this.getApplicationContext()))
                        .build();
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                //float similarity = FaceUtils.getFaceUtils().similarity(App.byteface, nv21, camera.getParameters().getPreviewSize());
                // LogUtils.a("人脸相似度："+similarity);
                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);

                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {

                    drawPreviewInfo(facePreviewInfoList);
                }
                clearLeftFace(facePreviewInfoList);
                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        livenessMap.put(facePreviewInfoList.get(i).getTrackId(), facePreviewInfoList.get(i).getLivenessInfo().getLiveness());
                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                            //LogUtils.a("requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null");
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                            faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                        }
                    }
                } else if (previewSize == null) {
                    LogUtils.a("previewSize==null");
                }
            }

            @Override
            public void onCameraClosed() {
                LogUtils.a("onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                LogUtils.a("onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
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
                    try {
                        compareResultList.remove(i);//退出程序，app在后台时刷卡发送角标越界
                    } catch (IndexOutOfBoundsException e) {
                        LogUtils.a(e.getMessage().toString());
                    }
                    //adapter.notifyItemRemoved(i);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initEngine();
                initCamera();
                if (cameraHelper != null) {
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

    private void compareFace(FaceFeature faceFt, Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
                if (recognitionstate != 1) {
                    return;
                }
                if (compareNum >= fail_num) {
                    handler.post(runnable_recognition_fail);
                    return;
                }
                compareNum++;
                FaceFeature faceFeature = new FaceFeature(App.byteface);
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFeature, faceFt, App.police_name);
                if (compareResult == null) {
                    emitter.onError(null);
                } else {
                    LogUtils.a(compareResult.getUserName(), compareResult.getSimilar());
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
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            return;
                        }
                        LogUtils.a("相似度" + compareResult.getSimilar());
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                    //adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                                // adapter.notifyItemInserted(compareResultList.size() - 1);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUserName());
                            //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(compareResult.getUserName());//+ "识别成功"语音删除
                            // LogUtils.a("识别成功时间");
                            //上传照片
                            takePictures(policeNum);

                            //发送卡号、成功、开门
                            SerialPortUtils.gethelp().successOpenDoor(policeNum);
                            displaysubtitles(1);
                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void takePictures(String policenum) {
        LogUtils.a("开始takePictures图片");
        handler.post(new Runnable() {
            @Override
            public void run() {
                CaptureUtil captureUtil = new CaptureUtil().setUpMediaProjection(mMediaProjection);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = captureUtil.startCapture();
                            if (bitmap != null) {
                                LogUtils.a("开始存储图片");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                Date date = new Date(System.currentTimeMillis());
                                String format = simpleDateFormat.format(date);
                                //FileUtils.getFileUtilsHelp().saveMyBitmap(bitmap);
                                // LogUtils.a("图片",ImageBase64Utils.getBitmapByte(bitmap));
                                JsonPolicePhoto jsonPolicePhoto = new JsonPolicePhoto(policenum, NetWorkUtils.getIP(), format, ImageBase64Utils.getBitmapByte(bitmap), "F");
                                RequestHelper.getRequestHelper().uploadPolicephoto(jsonPolicePhoto);
                            }


                        } catch (NullPointerException r) {
                            LogUtils.a("图片", r.getMessage().toString());
                            r.printStackTrace();
                        }
                    }
                }, 300);
            }
        });
       /* View view = getWindow().getDecorView();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);禁止截屏标记
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();*/
      /*  Bitmap bitmap = getbitmap();
        if (bitmap == null) {
            LogUtils.a("bitmap为null");
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        FileUtils.getFileUtilsHelp().saveMyBitmap(bitmap);
        // LogUtils.a("图片",ImageBase64Utils.getBitmapByte(bitmap));
        JsonPolicePhoto jsonPolicePhoto = new JsonPolicePhoto(policeNum, NetWorkUtils.getIP(), format, ImageBase64Utils.getBitmapByte(bitmap), "F");
        RequestHelper.getRequestHelper().uploadPolicephoto(jsonPolicePhoto);*/
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
      /*  if (recognitionstate == 0 && isFastDoubleClick()) {
            showdkdialog();
        }*/
        SerialPortUtils.gethelp().successOpenDoor(App.policeNum);
    }

    private long lastClickTime = System.currentTimeMillis();

    private boolean isFastDoubleClick() {
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
                    Intent intent = new Intent(FaceRecognitionActivity.this, SettingActivity.class);
                    intent.putExtra("mode", 1);
                    startActivity(intent);
                } else if (text.equals("njzx")) {//2干警
                    api.setStatusBar(true);
                    Intent intent = new Intent(FaceRecognitionActivity.this, SettingActivity.class);
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
