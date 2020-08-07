package com.arcsoft.arcfacedemo.activity.callroll;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.kingsun.KingsunSmartAPI;
import android.content.Context;
import android.content.Intent;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.dao.bean.PrisonerFace;
import com.arcsoft.arcfacedemo.dao.helper.PrisonerFaceHelp;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.model.FacePreviewInfo;
import com.arcsoft.arcfacedemo.util.camera.CameraHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraListener;
import com.arcsoft.arcfacedemo.util.face.FaceHelper;
import com.arcsoft.arcfacedemo.util.face.FaceListener;
import com.arcsoft.arcfacedemo.util.face.RequestFeatureStatus;
import com.arcsoft.arcfacedemo.util.image.ImageBase64Utils;
import com.arcsoft.arcfacedemo.util.utils.CaptureUtil;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DrawHelper;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;
import com.arcsoft.arcfacedemo.widget.CriminalNameAdapter;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
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

public class CriminalFacecontrastActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.facecontrast_texturepreview)
    TextureView previewView;
    @BindView(R.id.facecontrast_facerectview)
    FaceRectView faceRectView;
    @BindView(R.id.facecontrast_subtitles)
    TextView faceRecognitionSubtitles;
    @BindView(R.id.facecontrast_recycleListview)
    android.support.v7.widget.RecyclerView facecontrastRecycleListview;
    @BindView(R.id.facecontrast_Linear)
    LinearLayout faceRecognitionLinear;

    /**
     * 优先打开的摄像头，本界面主要用于单目RGB摄像头设备，因此默认打开前置
     */
    private Integer rgbCameraID = 0;
    private FaceEngine faceEngine;
    private int afCode = -1;
    private static final int MAX_DETECT_NUM = 10;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private FaceHelper faceHelper;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private static float SIMILAR_THRESHOLD = 0.82F;
    private KingsunSmartAPI api;


    private Handler handler = new Handler() {
    };
    //定时退出
    Runnable runnable_time_out = new Runnable() {
        @Override
        public void run() {
            PrisonerFaceHelp.savePrisonerresult(recognition_num-1,3);
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(prisonerNameList.get(recognition_num - 1)+"点名超时");
            recognition_over++;
            displaysubtitles(2);
        }
    };
    //定时显示一秒字幕再推出
    Runnable runnable_time_display = new Runnable() {
        @Override
        public void run() {
            startFacerecognition();
        }
    };
     Runnable runnable_CallRoll_flase = new Runnable() {
        @Override
        public void run() {
            faceRecognitionSubtitles.setVisibility(View.VISIBLE);
                faceRecognitionSubtitles.setText("识别失败");
                faceRecognitionSubtitles.setTextColor(Color.parseColor("#ff0000"));
                api.controlLight("01");
        }
    };
    private List<String> prisonerNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criminal_facecontrast);
        ButterKnife.bind(this);
        initView();
        //本地人脸库初始化
        FaceServer.getInstance().init(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 1);
        }
    }


    private int recognition_num = 0;
    private int recognition_over = 0;

    @SuppressLint("WrongConstant")
    @Override
    protected void onStart() {
        super.onStart();
        if (api == null) {
            api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
            api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
        }
        prisonerNameList = PrisonerFaceHelp.getPrisonerNameListFromDB();
        CriminalNameAdapter criminalNameAdapter = new CriminalNameAdapter(prisonerNameList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        facecontrastRecycleListview.setLayoutManager(layoutManager);
        facecontrastRecycleListview.setAdapter(criminalNameAdapter);
        startFacerecognition();
    }

    private List<CompareResult> compareResultList;

    private void initView() {
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        compareResultList = new ArrayList<>();
        // openBroadcast();
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("");
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
                    LogUtils.a("开始人脸识别");
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        //LogUtils.a("活体检测通过，搜索特征");
                        LogUtils.a("活体识别通过开始对比");
                        searchFace(faceFeature, requestId);
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
                        .currentTrackId(ConfigUtil.getTrackId(CriminalFacecontrastActivity.this.getApplicationContext()))
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
    //识别次数
    int compareNum = 0;
    private void searchFace(FaceFeature faceFt, Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {

                String name = prisonerNameList.get(recognition_num - 1);
                PrisonerFace prisonerFace = PrisonerFaceHelp.getPrisonerFaceFromName(recognition_num - 1);
                FaceFeature faceFeature = new FaceFeature(SwitchUtils.base64tobyte(prisonerFace.getEmp_feature()));
                CompareResult compareResult = FaceServer.getInstance().getSimilar(faceFeature, faceFt, name);
                if (compareResult == null) {
                    emitter.onError(null);
                } else {
                    LogUtils.a(compareResult.getUserName(), compareResult.getSimilar());
                    // compareSimilar = compareSimilar > compareResult.getSimilar() ? compareSimilar : compareResult.getSimilar();
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
                        if (compareResult == null || compareResult.getUserName() == null) {
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
                            if (recognition_num - 1 == recognition_over) {
                                recognition_over++;
                                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别成功");//+ "识别成功"语音删除
                                LogUtils.a("识别成功时间");
                                //上传照片
                                takePictures(recognition_num);
                                PrisonerFaceHelp.savePrisonerresult(recognition_num-1,1);
                                displaysubtitles(1);
                            }
                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            compareNum++;
                            if (compareNum >= 3) {
                                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("识别失败请"+prisonerNameList.get(recognition_num - 1)+"开始识别");
                                PrisonerFaceHelp.savePrisonerresult(recognition_num-1,2);
                                compareNum=0;
                                  handler.post(runnable_CallRoll_flase);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        LogUtils.a(e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    MediaProjectionManager mMediaProjectionManager = null;
    private MediaProjection mMediaProjection;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mResultCode = resultCode;
        //mResultData = data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        }
    }
    private void takePictures(int prisonernum) {
        LogUtils.a("开始takePictures图片");
        handler.post(new Runnable() {
            @Override
            public void run() {
                CaptureUtil captureUtil = new CaptureUtil().setUpMediaProjection(Utils.getContext(), mMediaProjection);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = captureUtil.startCapture();
                            if (bitmap!=null){
                                LogUtils.a("开始存储图片");
                                PrisonerFace prisonerFace = PrisonerFaceHelp.getPrisonerFaceFromName(prisonernum - 1);
                                prisonerFace.setPhoto( ImageBase64Utils.getBitmapByte(bitmap));
                                PrisonerFaceHelp.savePrisonerFaceToDB(prisonerFace);
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

    //开始人脸识别
    public void startFacerecognition() {
        recognition_num++;
        if (recognition_num > prisonerNameList.size()) {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("结束");
            finish();
            return;
        }
        compareNum=0;
        requestFeatureStatusMap.clear();//清除预览人脸
        livenessMap.clear();
        compareResultList.clear();
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请" + prisonerNameList.get(recognition_num - 1) + "开始识别");
        //handler.post(runnable);//刷卡进入识别页面
        handler.postDelayed(runnable_time_out, 40 * 1000);
    }


    //0失败，1成功，2超时
    public void displaysubtitles(int i) {
        handler.removeCallbacks(runnable_time_out);
        handler.postDelayed(runnable_time_display, 500);//延迟一秒执行
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
}
