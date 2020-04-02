package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.util.utils.DrawHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraListener;
import com.arcsoft.arcfacedemo.util.server.server.UDPServer;
import com.arcsoft.arcfacedemo.util.server.server.handleReceiveData;
import com.arcsoft.arcfacedemo.widget.FaceRectView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;

public class VideoCallActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener ,handleReceiveData, SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "VideoCallActivity";
    /**
     * 本地相机预览显示的控件，可为SurfaceView或TextureView
     */
    private SurfaceView localpreviewView;
    private SurfaceHolder surfaceHolder;
    /**
     * 优先打开的摄像头，本界面主要用于单目RGB摄像头设备，因此默认打开前置
     */
    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    /**
     * 绘制人脸框的控件
     */
    private FaceRectView localfaceRectView;
    private ImageView distalimageview;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    UDPServer up;
    UDPServer upa;
    private Camera camera;
    private float mCameraOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        initView();
        initserver();
    }

    private void initserver() {
        Thread service = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    up = new UDPServer(8804);//本地端口
                    up.setReceiveCallback(VideoCallActivity.this);
                    up.start();
                    upa = new UDPServer(8805);//远端端口
                    up.setReceiveCallback(new handleReceiveData() {
                        @Override
                        public void handleReceive(byte[] data) {

                        }
                    });
                    // button.setEnabled(false);
                } catch (SocketException e) {
                    //button.setEnabled(true);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        service.start();
    }

    private void initView() {
        localpreviewView = findViewById(R.id.local_texture_preview);
        surfaceHolder = localpreviewView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        distalimageview = findViewById(R.id.distal_image_view);
        //在布局结束后才做初始化操作
        localpreviewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        localfaceRectView = findViewById(R.id.local_face_rect_view);
    }

    /**
     * 在{@link #localpreviewView}第一次布局完成后，去除该监听，并且相机的初始化
     */
    @Override
    public void onGlobalLayout() {
        localpreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initCamera();
        }
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
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, localpreviewView.getWidth(), localpreviewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);

            }

            @Override
            public void onPreview(byte[] data, Camera camera) {
                if (localfaceRectView != null) {
                    localfaceRectView.clearFaceInfo();
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(localpreviewView.getMeasuredWidth(), localpreviewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraID != null ? rgbCameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(localpreviewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();


    }


    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE

    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final float SIMILAR_THRESHOLD = 0.8F;

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
    public void handleReceive(final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length);
                distalimageview.setImageBitmap(bit);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCanmera();
    }
    private void initCanmera() {
        int cameras = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera = Camera.open(i);
                break;
            }
        }
        //没有前置摄像头
        if (camera == null) camera = Camera.open();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(this);
        } catch (Exception e) {
            camera.release();//释放资源
            camera = null;
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        int currentCamera =0;
        Camera.Parameters parameters = camera.getParameters();//得到相机设置参数
        Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小

        parameters.setPictureFormat(PixelFormat.JPEG);//设置图片格式
        Camera.CameraInfo info = new Camera.CameraInfo();
        camera.getCameraInfo(currentCamera, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int resultA = 0, resultB = 0;
        if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            resultA = (info.orientation - degrees + 360) % 360;
            resultB = (info.orientation - degrees + 360) % 360;
            camera.setDisplayOrientation(resultA);
        } else {
            resultA = (360 + 360 - info.orientation - degrees) % 360;
            resultB = (info.orientation + degrees) % 360;
            camera.setDisplayOrientation(resultA);
        }
        camera.setPreviewCallback(this);
        parameters.setRotation(resultB);
        mCameraOrientation = resultB;
        camera.setParameters(parameters);
        camera.startPreview();//开始预览
    }

  /*  protected void setDisplayOrientation(Camera camera, int angle) {
        try {
            Method downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[]{angle});
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }*/

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    //相机图片处理并发送远端
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        int[] rgb = decodeYUV420SP(bytes, previewSize.width, previewSize.height);
        Bitmap bmp = Bitmap.createBitmap(rgb, previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888);
        int smallWidth, smallHeight;
        int dimension = 200;
        if (previewSize.width > previewSize.height) {
            smallWidth = dimension;
            smallHeight = dimension * previewSize.height / previewSize.width;
        } else {
            smallHeight = dimension;
            smallWidth = dimension * previewSize.width / previewSize.height;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(mCameraOrientation);
        Bitmap bmpSmall = Bitmap.createScaledBitmap(bmp, smallWidth, smallHeight, false);
        Bitmap bmpSmallRotated = Bitmap.createBitmap(bmpSmall, 0, 0, smallWidth, smallHeight, matrix, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmpSmallRotated.compress(Bitmap.CompressFormat.WEBP, 80, baos);
        up.sendMsg(baos.toByteArray());
    }
    public int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[width * height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }
}
