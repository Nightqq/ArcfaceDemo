package com.arcsoft.arcfacedemo.activity.arcface;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.activity.TestActivity;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.PermissionsUtils;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        if (!ConfigUtil.getFirstStart()) {
            startActivity(FaceRecognitionActivity.class);
            this.finish();
            return;
        }
        getpermiss();

    }

    //动态获取权限
    private void getpermiss() {
        //两个日历权限和一个数据读写权限
        String[] permissions = new String[]{
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,// 开机自启动权限
                Manifest.permission.RECORD_AUDIO};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Toast.makeText(LogoActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(LogoActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };
    //初始化人脸引擎
    private FaceEngine faceEngine = new FaceEngine();

    public void jumpToactiveEngine(final View view) {
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int activeCode = faceEngine.activeOnline(LogoActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = faceEngine.getActiveFileInfo(LogoActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            LogUtils.i(activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void jumptogetallFace(View view) {
        showdkdialog();
    }

    private void showdkdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_download_progress, null);
        dialog.setView(view, 0, 0, 0, 0);
        Button button = (Button) view.findViewById(R.id.dialog_download_confirm);
        Button button_cancel = (Button) view.findViewById(R.id.dialog_download_cancel);
        TextView textView = (TextView) view.findViewById(R.id.dialog_download_text);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.dialog_download_progressbar);
        dialog.setCanceledOnTouchOutside(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                //RequestHelper.getRequestHelper().getAllPoliceFace();
                textView.setText("下载数据中(返回键已经屏蔽)");
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                button_cancel.setVisibility(View.GONE);
                progressBar.setProgress(1);
                RequestHelper.getRequestHelper().getAllPoliceFace(new RequestHelper.OpenDownloadListener() {
                    @Override
                    public void openDownload(String msgs) {

                        if (msgs.equals("存储数据成功") || msgs.equals("存储数据成功")) {
                            dialog.dismiss();
                            return;
                        } else if (isNumeric(msgs)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtils.a(msgs);
                                    int i = Integer.parseInt(msgs);
                                    progressBar.setProgress(i);
                                }
                            });
                        }
                    }
                });
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    int model = 1;//1干警入监，2犯人点名

    public void jumptonextactivity(View view) {
        if (model == 1) {
            startActivity(FaceRecognitionActivity.class);
        } else if (model == 2) {
            startActivity(TestActivity.class);
        }
    }
}
