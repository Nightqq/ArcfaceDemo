package com.arcsoft.arcfacedemo.activity.local;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.kingsun.KingsunSmartAPI;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.PermissionsUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalActivity extends BaseActivity {

    @BindView(R.id.ontoon_yz)
    EditText ontoonYz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getpermiss();
        KingsunSmartAPI api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
        //api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
        api.setStatusBar(false);
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
            Toast.makeText(LocalActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(LocalActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
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
                int activeCode = faceEngine.activeOnline(LocalActivity.this,   Constants.ACTIVE_KEY(),Constants.APP_ID, Constants.SDK_KEY);
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

                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = faceEngine.getActiveFileInfo( activeFileInfo);
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

    //注册人脸
    public void jumpToregist(View view) {
        if (App.iSFinish == true && flag == true) {
            flag = false;
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("启动中请稍等");
            startActivity(new Intent(this, RegistActivity.class));
            finish();
        } else {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("热成像关闭中请稍等");
        }
    }

    boolean flag = true;

    public void jumpToOnetoone(View view) {
        if (App.iSFinish == true && flag == true) {
            flag = false;
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("启动中请稍等");
            startActivity(new Intent(this, OneToOneneActivity.class));
            finish();
        } else {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("热成像关闭中请稍等");
        }
    }

    public void jumpToOnetoN(View view) {
        if (App.iSFinish == true && flag == true) {
            flag = false;
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("启动中请稍等");
            startActivity(new Intent(this, OneToNActivity.class));
            finish();
        } else {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("热成像关闭中请稍等");
        }
    }

    public void jumpTofinish(View view) {
        if (App.iSFinish == true && flag == true) {
            KingsunSmartAPI api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
            //api.setDaemonProcess("com.arcsoft.arcfacedemo", true);//设置为守护app
            api.setStatusBar(true);
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            System.exit(0);
        } else {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("热成像关闭中请稍等");
        }
    }

    public void jumpToOnsxd(View view) {
        String s = ontoonYz.getText().toString();
        if (isDoubleOrFloat(s)) {
            float v = Float.parseFloat(s);
            if (v > 1 || v < 0) {
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("零到一");
            } else {
                TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
                terminalInformation.setRecognitionThreshold(v);
                TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
            }
        } else {
            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式异常");
        }

    }

    public boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void restartApp() {
        Intent intent2 = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}
