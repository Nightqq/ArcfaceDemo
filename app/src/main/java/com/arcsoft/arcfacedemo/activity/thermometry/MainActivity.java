package com.arcsoft.arcfacedemo.activity.thermometry;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.arcsoft.arcfacedemo.util.utils.SPUtils;
import com.guide.guidecore.FirmwareUpgradeResultCode;
import com.guide.guidecore.GuideInterface;
import com.guide.guidecore.UsbStatusInterface;
import com.guide.guidecore.jni.AutoCorrectResult;
import com.guide.guidecore.utils.BaseDataTypeConvertUtils;
import com.guide.guidecore.view.IrSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements GuideInterface.ImageCallBackInterface, UsbStatusInterface, View.OnClickListener {

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
  /*  private EditText mDistanceEditText;
    private TextView mEnvtempTextView;
    private EditText mAmbientTempEditText;
    private EditText mNearKFEditText;
    private EditText mNearBEditText;
    private EditText mFarKFEditText;
    private EditText mFarBEditText;
    private EditText mBrightEditText;
    private EditText mContrastEditText;
    private TextView mSNTextView;
    private TextView mSDKVersionTextView;
    private TextView mFirmwareVersionTextView;
    private TextView mUpgradePathTextView;*/
    private FrameLayout mDisplayFrameLayout;
    private FrameLayout mHumanDisplayFrameLayout;
   // private LinearLayout mExpertLayout;
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

 //   private Button mRecordDebugData;

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
    private int rotateType = 1;
    private float mScale = 3f;
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

                /*    mEnvtempTextView.setText("环境温：" + mGuideInterface.getEnvTemp() + "\n" +
                            "冷热机状态：" + (mGuideInterface.getColdHotState() ? "冷机" : "热机") + "\n" +
                            "测温状态:" + (mGuideInterface.isCalTempOk() ? "稳定" : "不稳定"));*/
                    break;
                case 1:
                    float centerTemp = Float.valueOf(mGuideInterface.getCenterTemp());
                    float ambientTemp;
                    ambientTemp = GuideInterface.DEFAULT_AMBIENT_TEMP;
                 /*   if (TextUtils.isEmpty(mAmbientTempEditText.getText())) {
                        ambientTemp = GuideInterface.DEFAULT_AMBIENT_TEMP;
                    } else {
                        ambientTemp = Float.valueOf(mAmbientTempEditText.getText().toString());
                    }*/

                    float maxTemp = Float.parseFloat(maxTempStr);
                    mHumanCenterTextView.setText(
                            "体内中心温:" + "\r\n" +
                                    mGuideInterface.getHumanTemp(centerTemp, ambientTemp) + "\r\n" +
                                    "体内最高温:" + "\r\n" +
                                    mGuideInterface.getHumanTemp(maxTemp, ambientTemp));
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "0.5米参数保存成功", Toast.LENGTH_LONG).show();
                    break;

                case 4:
                    Toast.makeText(MainActivity.this, "1.2米参数保存成功", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        verifyStoragePermissions();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();

        mGuideInterface = new GuideInterface();
        int paletteIndex = Integer.valueOf(SPUtils.getPalette(MainActivity.this));
        mScale = Float.valueOf(SPUtils.getScale(MainActivity.this));
        rotateType = Integer.valueOf(SPUtils.getRotate(MainActivity.this));
        mGuideInterface.guideCoreInit(this, paletteIndex, mScale, rotateType);

        String imageAlgoSwitch = SPUtils.getImageAlgo(MainActivity.this);
        mGuideInterface.controlImageOptimizer(TextUtils.equals(imageAlgoSwitch, "开"));

        //mSDKVersionTextView.setText("SDK Version: " + mGuideInterface.getVersion());

        //原始红外视频的分辨率是90*120
        mY16Frame = new short[SRC_WIDTH * SRC_HEIGHT];
        mSyncY16Frame = new short[SRC_WIDTH * SRC_HEIGHT];

        //createFile();
    }


    private void initView() {
        mCenterTextView = findViewById(R.id.temp_display);
        mHumanCenterTextView = findViewById(R.id.human_temp_display);
        mFocusTextView = findViewById(R.id.focus_temp_display);
        // mDistanceEditText = findViewById(R.id.distance_et);
        //  mEnvtempTextView = findViewById(R.id.envtemp_textview);
        // mAmbientTempEditText = findViewById(R.id.ambient_temp_et);
        // mNearKFEditText = findViewById(R.id.adjust_temp_5_kf_et);
        //   mNearBEditText = findViewById(R.id.adjust_temp_5_b_et);
        //  mFarKFEditText = findViewById(R.id.adjust_temp_12_kf_et);
        //mFarBEditText = findViewById(R.id.adjust_temp_12_b_et);
        // mAjustT1EditText = findViewById(R.id.adjust_T1_et);
        //mAjustT2EditText = findViewById(R.id.adjust_T2_et);
        //mBrightEditText = findViewById(R.id.bright_et);
        //  mContrastEditText = findViewById(R.id.contrast_et);
        // mSNTextView = findViewById(R.id.sn_tv);
        // mSDKVersionTextView = findViewById(R.id.sdk_version_tv);
        // mFirmwareVersionTextView = findViewById(R.id.firmware_version_tv);
        //mUpgradePathTextView = findViewById(R.id.upgrade_path_tv);
        mDisplayFrameLayout = findViewById(R.id.temp_display_layout);
        mHumanDisplayFrameLayout = findViewById(R.id.human_temp_display_layout);
        //mExpertLayout = findViewById(R.id.expert_ll);
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


        //mRecordDebugData = findViewById(R.id.record);
        // mRecordDebugData.setText("录制数据");

        mHighCrossView = new ImageView(this);
        mHighCrossView.setImageResource(R.drawable.high_cross);
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
        String autoShutterSwitch = SPUtils.getSwitch(MainActivity.this);
        long period = Long.valueOf(SPUtils.getPeriod(MainActivity.this));
        long delay = Long.valueOf(SPUtils.getDelay(MainActivity.this));

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
    @Override
    public void callBackOneFrameBitmap(Bitmap bitmap, final short[] y16Frame) {
        mIrBitmap = bitmap;
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
                   // mFirmwareVersionTextView.setText("固件版本号： " + mGuideInterface.getFirmwareVersion());

                    mDisplayFrameLayout.removeView(mHighCrossView);

                    float scale = irSurfaceViewWidth / rawWidth;

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(highCrossWidth, highCrossHeight);
                    lp.leftMargin = (int) ((maxIndex % rawWidth) * scale - highCrossWidth / 2);
                    lp.topMargin = (int) ((maxIndex / rawWidth) * scale - highCrossHeight / 2);
                    mDisplayFrameLayout.addView(mHighCrossView, lp);

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
        try {
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "此设备不支持文件浏览！", Toast.LENGTH_LONG).show();
        }
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
   /* public static String getPath(final Context context, final Uri uri) {

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
    }*/

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
   /* public static String getDataColumn(Context context, Uri uri, String selection,
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
*/

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

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGuideInterface != null) {
            mGuideInterface.guideCoreDestory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
//            String path = getPath(this, uri);
            String path = "";
            if (Build.VERSION.SDK_INT >= 24) {
                // path = getFilePathFromURI(this, uri);
            } else {
                path = getPath(this, uri);
            }
            //mUpgradePathTextView.setText(path);
        }*/
    }

    /**
     * 体表温度
     */
    public void onTempBtnClick(View view) {
        isDispLayTemp = !isDispLayTemp;
        if (isDispLayTemp) {
            mDisplayFrameLayout.setVisibility(View.VISIBLE);
//            mEnvtempTextView.setVisibility(View.VISIBLE);

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
            //mEnvtempTextView.setVisibility(View.GONE);

            mTimerTask.cancel();
            mTimerTask = null;
            mTimer.cancel();
            mTimer = null;
        }
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
       /* if (!TextUtils.isEmpty(mAmbientTempEditText.getText())) {
            String ambientTempStr = mAmbientTempEditText.getText().toString();
            float ambientTemp = Float.valueOf(ambientTempStr);
            if (ambientTemp < 10 || ambientTemp > 32) {
                Toast.makeText(this, "环境温度输入不合法", Toast.LENGTH_LONG).show();
                return;
            }
        }*/

        isDispLayHumanTemp = !isDispLayHumanTemp;
        if (isDispLayHumanTemp) {
          /*  if (TextUtils.isEmpty(mAmbientTempEditText.getText())) {
                Toast.makeText(this, "正在使用内置算法得到的环境温度", Toast.LENGTH_LONG).show();
            }*/
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
    }

    /**
     * 设置距离
     */
    public void onDistanceBtnClick(View view) {
       /* if (TextUtils.isEmpty(mDistanceEditText.getText())) {

            mDistanceEditText.setText("");
            return;
        }
        String distanceStr = mDistanceEditText.getText().toString();*/
       /* float distance = Float.valueOf(distanceStr);
        if (distance < 0.5f || distance > 1.2f) {

            mDistanceEditText.setText("");
            return;
        }*/
       /* mGuideInterface.setDistance(distance);*/

    }


    /**
     * 设置亮度
     */
    public void onBrightBtnClick(View view) {
      /*  if (TextUtils.isEmpty(mBrightEditText.getText())) {

            mBrightEditText.setText("");
            return;
        }*/

        //int bright = -1;
      /*  try {
            bright = Integer.valueOf(mBrightEditText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*if (bright < 0 || bright > 100) {

            mBrightEditText.setText("");
            return;
        }*/

       /* mGuideInterface.setBright(bright);*/



    }

    /**
     * 设置对比度
     */
    public void onContrastBtnClick(View view) {
       /* if (TextUtils.isEmpty(mContrastEditText.getText())) {

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

        mGuideInterface.setContrast(contrast);*/


    }

    /**
     * 获取SN
     */
    public void onSnBtnClick(View view) {
       // mSNTextView.setText(mGuideInterface.getSN());
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
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGuideInterface != null) {
                    String path = mUpgradePathTextView.getText().toString();
                    Log.d(TAG, "firmwareUpgrade start");
                    final FirmwareUpgradeResultCode code = mGuideInterface.firmwareUpgrade(path);
                    Log.d(TAG, "firmwareUpgrade: " + code.getMsg());
                    if (code == FirmwareUpgradeResultCode.SUCCESS) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //重置mcu程序;
                                if (mGuideInterface != null) {
                                    mGuideInterface.sendResetOrder();
                                }
                                Toast.makeText(MainActivity.this, code.getMsg(), Toast.LENGTH_SHORT).show();
                                view.setClickable(true);
                            }
                        }, 60 * 1000);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG, "FU " + code.getMsg());
                                Toast.makeText(MainActivity.this, code.getMsg(), Toast.LENGTH_SHORT).show();
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
      /*  if (EXPERT_HITS[0] >= (System.currentTimeMillis() - EXPERT_MODE_HIT_DURATION)) {
            if (mExpertLayout.getVisibility() == View.GONE) {
                mExpertLayout.setVisibility(View.VISIBLE);
            } else {
                mExpertLayout.setVisibility(View.GONE);
            }
            EXPERT_HITS = new long[EXPERT_MODE_HIT_COUNT];
        }*/
    }

    /**
     * 0.5米校温
     */
    public void onAjustTemp5Click(View view) {
        Toast.makeText(this, "请站在0.5米处校准温度", Toast.LENGTH_LONG).show();
        //初始化校温参数
       // mNearKFEditText.setText(mGuideInterface.getNearKf() + "");
       // mNearBEditText.setText(mGuideInterface.getNearB() + "");
        mGuideInterface.setDistance(0.5f);
    }

    /**
     * 1.2米校温
     */
    public void onAjustTemp12Click(View view) {
        Toast.makeText(this, "请站在1.2米处校准温度", Toast.LENGTH_LONG).show();
      //  mFarKFEditText.setText(mGuideInterface.getFarKf() + "");
       // mFarBEditText.setText(mGuideInterface.getFarB() + "");
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
                       /* if (!TextUtils.isEmpty(mNearKFEditText.getText())) {
                            short nearKf = Short.valueOf(mNearKFEditText.getText().toString());
                            mGuideInterface.setNearKf(nearKf);
                        }
                        if (!TextUtils.isEmpty(mNearBEditText.getText())) {
                            short nearB = Short.valueOf(mNearBEditText.getText().toString());
                            mGuideInterface.setNearB(nearB);
                        }
                        mHandler.sendEmptyMessage(3);*/
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
                        /*if (!TextUtils.isEmpty(mFarKFEditText.getText())) {
                            short farKf = Short.valueOf(mFarKFEditText.getText().toString());
                            mGuideInterface.setFarKf(farKf);
                        }
                        if (!TextUtils.isEmpty(mFarBEditText.getText())) {
                            short farB = Short.valueOf(mFarBEditText.getText().toString());
                            mGuideInterface.setFarB(farB);
                        }
                        mHandler.sendEmptyMessage(4);*/
                    }
                }
        ).start();
    }

    private void autoCorrectCalcY16(final View view, final GuideInterface.AutoCorrectCalcY16Mode mode) {
        view.setClickable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGuideInterface != null) {
                    if (mode == GuideInterface.AutoCorrectCalcY16Mode.near30
                            || mode == GuideInterface.AutoCorrectCalcY16Mode.near33
                            || mode == GuideInterface.AutoCorrectCalcY16Mode.near36) {
                        mGuideInterface.setDistance(0.5f);
                    } else {
                        mGuideInterface.setDistance(1.2f);
                    }
                    final short y16 = mGuideInterface.autoCorrectCalcY16(mode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MainActivity.this, mode.name() + "的y16计算完成 = " + y16, Toast.LENGTH_SHORT).show();
                            view.setClickable(true);
                        }
                    });
                }
            }
        }).start();
    }

    public void onNear30Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.near30);
    }

    public void onNear33Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.near33);
    }

    public void onNear36Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.near36);
    }

    public void onNearSetClick(final View view) {
        view.setClickable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final AutoCorrectResult result = new AutoCorrectResult();
                mGuideInterface.autoCorrectNear(result, 30, 33, 36);
                mGuideInterface.setNearKf((short) (result.nearKf * 10000));
                mGuideInterface.setNearB((short) (result.nearB * 100));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                });
            }
        }).start();
    }

    public void onFar30Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.far30);
    }

    public void onFar33Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.far33);
    }

    public void onFar36Click(View view) {
        autoCorrectCalcY16(view, GuideInterface.AutoCorrectCalcY16Mode.far36);
    }

    public void onFarSetClick(final View view) {
        view.setClickable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AutoCorrectResult result = new AutoCorrectResult();
                mGuideInterface.autoCorrectFar(result, 30, 33, 36);
                mGuideInterface.setFarKf((short) (result.farKf * 10000));
                mGuideInterface.setFarB((short) (result.farB * 100));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);

                    }
                });
            }
        }).start();
    }

    public void onNucShutterClick(View view) {
        if (mGuideInterface != null) {
            mGuideInterface.nucTest();
        }
    }

    /**
     * 保存一帧正常K/X/B原始数据
     */
    public void onIRFrameSaveClick(View view) {
        if (mGuideInterface != null) {
            mGuideInterface.saveIRRawData(tempPath);
            Toast.makeText(MainActivity.this, "正常数据保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存一帧异常K/X/B原始数据
     */
    public void onBadIRFrameSaveClick(View view) {
        if (mGuideInterface != null) {
            mGuideInterface.saveBadIRRawData(tempPath);
            Toast.makeText(MainActivity.this, "异常数据保存成功", Toast.LENGTH_SHORT).show();
        }
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
       /* if (!isDebugRecordDataing) {
            File file = new File(recordDebugDataPath);
            if (file.exists()) {
                file.delete();
            }

            mRecordDebugData.setText("正在录制...");

            mGuideInterface.startRecordDebugData(recordDebugDataPath);
        } else {
            mGuideInterface.stopRecordDebugData();
            mRecordDebugData.setText("录制数据");
        }*/
        isDebugRecordDataing = !isDebugRecordDataing;
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


        mSettingView = (SettingView) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_setting, null);

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

    private void showMessagePositiveDialog() {

    }

    private void showTipDialog(String tip, int type) {

    }
}


