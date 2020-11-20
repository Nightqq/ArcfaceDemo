package com.arcsoft.arcfacedemo.util.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.arcsoft.arcfacedemo.faceserver.FaceServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 *
 * 截屏工具类需要用户权限
 * */
public class CaptureUtil {
    private static final String TAG = "CaptureUtil";

    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String pathImage = null;
    private String nameImage = null;
    private ImageReader mImageReader = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;

    private int windowWidth = 0;
    private int windowHeight = 0;
    private int mScreenDensity = 0;

    private DisplayMetrics metrics = null;
    private WindowManager mWindowManager = null;
    Context context;
    private CaptureUtil captureUtil;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CaptureUtil setUpMediaProjection( MediaProjection mediaProjection) {
        if (mediaProjection == null) {
            LogUtils.a("MediaProjection null图片");
            return null;
        }
        this.context = Utils.getContext();
        mMediaProjection = mediaProjection;
        createVirtualEnvironment();
        virtualDisplay();
        return CaptureUtil.this;
    }


    public CaptureUtil getCaptureUtil(){
        if (captureUtil==null){
            captureUtil = new CaptureUtil();
        }
        return captureUtil;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        Log.i(TAG, "pathImage : " + Utils.getContext().getExternalCacheDir().getPath() + "/Pictures/");
        pathImage = Utils.getContext().getExternalCacheDir().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mWindowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565
        if (mImageReader == null) {
            Log.i(TAG, "ImageReader.newInstance null");
        }
        Log.i(TAG, "prepared the virtual environment");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        Log.i(TAG, "virtual displayed");
    }


    //ImageReader.newInstance 不能直接调用acquireLatestImage.可能会出现null 需要加一定延迟

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Bitmap startCaptureBitmap() throws NullPointerException {
        return startCaptureBitmap(0, 0, 0, 0);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Bitmap startCaptureBitmap(int x, int y, int mWidth, int mHeight) throws NullPointerException {
        LogUtils.a("日志","开始截屏");
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";
        Image image = mImageReader.acquireLatestImage();
        long startTime = System.currentTimeMillis();
        int num = 0;
        //最好先创建captureUtil 示例 300ms后 在调用否则可能检查为空
        if (image == null) {
            for (; ; ) {
                image = mImageReader.acquireLatestImage();
                num++;
                if (image != null || num > 30000)
                    break;
            }

        }
        long endTime = System.currentTimeMillis();
        Log.e(TAG, endTime - startTime + "  time  num :" + num);
        int width = 0;
        int height = 0;
        if (mWidth == 0 || mHeight == 0) {
            width = image.getWidth();
            height = image.getHeight();
        }
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, x<0?x:0, y<0?y:0, width, height);
        image.close();
        LogUtils.a( "图片","image data captured");
        return bitmap;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Bitmap startCapture() throws NullPointerException {
        Bitmap bitmap = startCaptureBitmap();
        bitmap = FaceServer.getInstance().getFaceRect(bitmap);
        LogUtils.a("日志","截图照片返回");
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public File startCapture(Bitmap bitmap) throws NullPointerException {
        File fileImage = null;
        if (bitmap != null) {
            try {
                fileImage = new File(nameImage);
                if(fileImage.getParentFile().exists()||fileImage.getParentFile().mkdirs()){
                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                        Log.i(TAG, "image file created");
                    }
                }else {
                    return null;
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(fileImage);
                    media.setData(contentUri);
                    context.sendBroadcast(media);
                    Log.i(TAG, "screen image saved");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileImage;
    }
}
