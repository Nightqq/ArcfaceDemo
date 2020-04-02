package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.faceserver.SignalingClient;
import com.arcsoft.arcfacedemo.util.utils.FaceUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.PermissionsUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.communi.SerialPortUtils;
import com.arcsoft.arcfacedemo.util.server.handler.PersonAddHandler;
import com.arcsoft.arcfacedemo.util.server.net.HttpUtils;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.util.server.server.OnServerChangeListener;
import com.arcsoft.arcfacedemo.util.server.server.ServerPresenter;
import com.arcsoft.arcfacedemo.util.server.server.ServerService;
import com.arcsoft.arcfacedemo.util.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity implements OnServerChangeListener {
    private static final String TAG = "TestActivity";
    private android.widget.ImageView imageView;
    private ServerPresenter serverPresenter;
    private Handler handler = new Handler() {
    };
    private byte[] mBuffer;
    private EditText edi_ip;
    private EditText test_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        imageView = (ImageView) findViewById(R.id.choose_img);
        edi_ip = ((EditText) findViewById(R.id.test_ip));
        test_name = ((EditText) findViewById(R.id.test_name));
        SharedPreferences arcface = Utils.getContext().getSharedPreferences("Arcface", 0);
        edi_ip.setText(arcface.getString("ip", SignalingClient.ip));
        test_name.setText(arcface.getString("name",SignalingClient.distal));
        edi_ip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String ip = edi_ip.getText().toString().replaceAll("\r|\n", "");
                Toast.makeText(getApplicationContext(),"输入的为:"+ ip,Toast.LENGTH_LONG).show();
                edi_ip.setText(ip);
                SharedPreferences arcface = Utils.getContext().getSharedPreferences("Arcface", 0);
                SharedPreferences.Editor edit = arcface.edit();
                edit.putString("ip",ip);
                edit.commit();
                SignalingClient.ip=ip;
                return false;
            }
        });
        test_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String name = test_name.getText().toString();
                Toast.makeText(getApplicationContext(),"输入的为:"+ name,Toast.LENGTH_LONG).show();
                test_name.setText(name.replaceAll("\r|\n",""));
                SharedPreferences arcface = Utils.getContext().getSharedPreferences("Arcface", 0);
                SharedPreferences.Editor edit = arcface.edit();
                edit.putString("name",name);
                edit.commit();
                SignalingClient.distal=name;
                return false;
            }
        });


        serverPresenter = new ServerPresenter(this, this);
        //串口数据监听事件
        SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer) {
                Log.d(TAG, "进入数据监听事件中。。。" + new String(buffer));
                //
                //在线程中直接操作UI会报异常：ViewRootImpl$CalledFromWrongThreadException
                //解决方法：handler
                //
                mBuffer = buffer;
                LogUtils.a(buffer.length);
                handler.post(runnable);
            }
            //开线程更新UI
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LogUtils.a("size：" + String.valueOf(mBuffer.length) + "数据监听：" + SwitchUtils.byte2HexStr(mBuffer));
                    Toast.makeText(TestActivity.this, SwitchUtils.byte2HexStr(mBuffer), Toast.LENGTH_SHORT).show();
                }
            };
        });
        getpermiss();
    }

    private void getpermiss(){
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
                Manifest.permission.RECORD_AUDIO};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Toast.makeText(TestActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(TestActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void jumpToovideocall(View view) {
        startActivity(new Intent(this, WebRTCActivity.class));
    }
    public void jumpTooaideocall(View view) {
        startActivity(new Intent(this, WebRTCActivity.class));
    }

    public void jumpToChooswFun(View view) {
        startActivity(new Intent(this, ChooseFunctionActivity.class));
    }
    public void jumpToopenserialport(View view) {

        SerialPortUtils.gethelp().openSerialPort();
    }
    public void jumpTosendinstructions(View view) {
        byte[] bytes = SwitchUtils.hexStringToByte("AABB0300020002");
        LogUtils.a(SwitchUtils.byte2HexStr(bytes));
        SerialPortUtils.gethelp().sendSerialPort(bytes);
    }

    public void jumpTooupdate(View view) {//更新


    }


    public void jumpToStratServer(View view) {
        serverPresenter.startServer(TestActivity.this);
        PersonAddHandler.imageListenter = new PersonAddHandler.getImageListenter() {
            @Override
            public void setimage(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        };
    }

    public void jumpToRecordsreturn(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.getHttpHeper().postJson();
            }
        }).start();
    }


    @Override
    public void onServerStarted(String ipAddress) {
        LogUtils.a(TAG, "IP Address: " + ipAddress);
        Toast.makeText(this, ipAddress, Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(ipAddress)) {
            List<String> addressList = new ArrayList<>();
            addressList.add("http://" + ipAddress + ":" + ServerService.port + ServerService.connect_post);
            String join = TextUtils.join("\n", addressList);
            LogUtils.a(join);
        } else {
            LogUtils.a("error");
        }
    }

    @Override
    public void onServerStopped() {
        LogUtils.a("服务器停止了");
    }

    @Override
    public void onServerError(String errorMessage) {
        LogUtils.a(errorMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverPresenter.unregister(this);
        serverPresenter = null;
    }


    public void jumpTogetface(View view) {
        App.byteface = FaceUtils.getFaceUtils().getFace("张三");
    }

    public void jumpTosetface(View view) {
        FaceUtils.getFaceUtils().registerFace( App.byteface ,"李四");
    }
}
