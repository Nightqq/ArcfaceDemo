package com.arcsoft.arcfacedemo.activity.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.kingsun.KingsunSmartAPI;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.activity.ChooseFunctionActivity;
import com.arcsoft.arcfacedemo.activity.TestActivity;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;
import com.arcsoft.arcfacedemo.dao.helper.PoliceFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.TemperatureSettingHelp;
import com.arcsoft.arcfacedemo.dao.helper.TerminalInformationHelp;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.DeviceUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.activity_setting_gone)
    LinearLayout activitySettingGone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        int mode = getIntent().getIntExtra("mode", 0);
        if (mode == 2) {
            activitySettingGone.setVisibility(View.GONE);
        }
    }

    public void jumpToGetPoliceFace(View view) {
        showgetPolicedialog();
    }

    public void jumpToterminalinform(View view) {
        shoeTterminalinformdialog();
    }

    private void shoeTterminalinformdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_terminal_inform, null);
        dialog.setView(view, 0, 0, 0, 0);
        TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
        TextView serial = (TextView) view.findViewById(R.id.dialog_terminal_serial);
        serial.setText(terminalInformation.getSerial());
        TextView device_num = (TextView) view.findViewById(R.id.dialog_terminal_device_num);
        device_num.setText(terminalInformation.getTerminalNum());
        TextView device_name = (TextView) view.findViewById(R.id.dialog_terminal_device_name);
        device_name.setText(terminalInformation.getTerminalName());
        TextView ip = (TextView) view.findViewById(R.id.dialog_terminal_ip);
        ip.setText(NetWorkUtils.getIP());

        EditText serverip_edt = (EditText) view.findViewById(R.id.dialog_terminal_serverip_edt);
        serverip_edt.setText(terminalInformation.getServerIP() + "");
        Button serverip_button = (Button) view.findViewById(R.id.dialog_terminal_serverip_button);

        EditText serverport_edt = (EditText) view.findViewById(R.id.dialog_terminal_serverport_edt);
        serverport_edt.setText(terminalInformation.getServerPost() + "");
        Button serverport_button = (Button) view.findViewById(R.id.dialog_terminal_serverport_button);


        EditText threshold_edt = (EditText) view.findViewById(R.id.dialog_terminal_threshold_edt);
        threshold_edt.setText(terminalInformation.getRecognitionThreshold() + "");
        Button threshold_button = (Button) view.findViewById(R.id.dialog_terminal_threshold_button);

        EditText failnum_edt = (EditText) view.findViewById(R.id.dialog_terminal_failnum_edt);
        failnum_edt.setText(terminalInformation.getRecognitionNum() + "");
        Button failnum_button = (Button) view.findViewById(R.id.dialog_terminal_failnum_button);

        EditText outtime_edt = (EditText) view.findViewById(R.id.dialog_terminal_outtime_edt);
        outtime_edt.setText(terminalInformation.getOutTime() + "");
        Button outtime_button = (Button) view.findViewById(R.id.dialog_terminal_outtime_button);

        serverport_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = serverport_edt.getText().toString();
                if (text != null && text.replaceAll(" ", "").length() > 0) {
                    if (SwitchUtils.isNumeric(text)) {
                        terminalInformation.setServerPost(text);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                        return;
                    }
                }
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式不正确");
            }
        });
        serverip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = serverip_edt.getText().toString();
                if (text != null && text.replaceAll(" ", "").length() > 0) {
                    if (SwitchUtils.isIP(text)) {

                        terminalInformation.setServerIP(text);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                        return;
                    }
                }
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式不正确");
            }
        });

        threshold_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = threshold_edt.getText().toString();
                if (text != null && text.replaceAll(" ", "").length() > 0) {
                    float v = Float.parseFloat(text.replaceAll(" ", ""));
                    if (v < 1 && v > 0) {
                        terminalInformation.setRecognitionThreshold(v);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                        return;
                    }
                }
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式不正确");
            }
        });
        failnum_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = failnum_edt.getText().toString();
                if (text != null && text.replaceAll(" ", "").length() > 0) {
                    int i = Integer.parseInt(text.replaceAll(" ", ""));
                    if (i > 0) {
                        terminalInformation.setRecognitionNum(i);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                        return;
                    }
                }
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式不正确");
            }
        });
        outtime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = outtime_edt.getText().toString();
                if (text != null && text.replaceAll(" ", "").length() > 0) {
                    long l = Long.parseLong(text.replaceAll(" ", ""));
                    if (l > 0) {
                        terminalInformation.setOutTime(l);
                        TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                        return;
                    }
                }
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式不正确");
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showgetPolicedialog() {
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
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage(msgs);
                        if (msgs.equals("存储数据成功")) {
                            dialog.dismiss();
                            return;
                        } else  {
                            dialog.dismiss();
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

    public void jumpToupdatedevice(View view) {
        showupdatedevicedialog();
    }

    private void showupdatedevicedialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_regist_device, null);
        dialog.setView(view, 0, 0, 0, 0);
        TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
        EditText edtDeviceId = (EditText) view.findViewById(R.id.dialog_device_id);
        edtDeviceId.setText(terminalInformation.getTerminalNum());
        TextView edtserverIP = (TextView) view.findViewById(R.id.dialog_server_ip);
        edtserverIP.setText(terminalInformation.getServerIP());
        EditText edtDeviceName = (EditText) view.findViewById(R.id.dialog_device_name);
        edtDeviceName.setText(terminalInformation.getTerminalName());
        TextView edtDeviceIP = (TextView) view.findViewById(R.id.dialog_device_ip);
        edtDeviceIP.setText(NetWorkUtils.getIP());
        TextView edtDeviceSerial = (TextView) view.findViewById(R.id.dialog_device_Serial);
        edtDeviceSerial.setText(DeviceUtils.getAndroidID());
        Button edtDeviceconfirm = (Button) view.findViewById(R.id.dialog_device_confirm);
        Button edtDevicecancel = (Button) view.findViewById(R.id.dialog_device_cancel);
        dialog.setCanceledOnTouchOutside(true);
        edtDeviceconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = edtDeviceId.getText().toString();
                String deviceName = edtDeviceName.getText().toString();
                if (deviceId == null || deviceId.length() == 0) {
                    Toast.makeText(SettingActivity.this, "设备编号不能为空", Toast.LENGTH_SHORT).show();
                } else if (deviceName == null || deviceName.length() == 0) {
                    Toast.makeText(SettingActivity.this, "设备名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    TerminalInformation terminalInformation = TerminalInformationHelp.getTerminalInformation();
                    terminalInformation.setTerminalNum(deviceId);
                    terminalInformation.setTerminalName(deviceName);
                    terminalInformation.setDeviceIP(NetWorkUtils.getIP());
                    terminalInformation.setDevicePost("3639");
                    TerminalInformationHelp.savePoliceInfoToDB(terminalInformation);
                    RequestHelper.getRequestHelper().updateFaceDevice(terminalInformation, new RequestHelper.OpenDownloadListener() {
                        @Override
                        public void openDownload(String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (SwitchUtils.isNumeric(message)) {
                                        if (message.equals("1")) {
                                            Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else if (message.equals("0")) {
                                            Toast.makeText(SettingActivity.this, "超时", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        edtDevicecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void jumpTowendusetting(View mview) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_wendu_setting, null);
        dialog.setView(view, 0, 0, 0, 0);

        TemperatureSetting temperatureSetting = TemperatureSettingHelp.getTerminalInformation();
        EditText x = (EditText) view.findViewById(R.id.dialog_x);
        x.setText(temperatureSetting.getForehead_X());
        EditText xx = (EditText) view.findViewById(R.id.dialog_xx);
        xx.setText(temperatureSetting.getForehead_xx());
        EditText y = (EditText) view.findViewById(R.id.dialog_y);
        y.setText(temperatureSetting.getForehead_Y());
        EditText yx = (EditText) view.findViewById(R.id.dialog_yx);
        yx.setText(temperatureSetting.getForehead_Yx());
        EditText shangx = (EditText) view.findViewById(R.id.dialog_shangxian);
        shangx.setText(temperatureSetting.getWenshang());
        EditText xiax = (EditText) view.findViewById(R.id.dialog_xiaxian);
        xiax.setText(temperatureSetting.getWenxia());
        Button edtDeviceconfirm = (Button) view.findViewById(R.id.dialog_device_confirm);
        Button edtDevicecancel = (Button) view.findViewById(R.id.dialog_device_cancel);
        dialog.setCanceledOnTouchOutside(true);
        edtDeviceconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!x.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("第一行格式错误");
                } else if (!xx.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("第二行格式错误");
                } else if (!y.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("第三行格式错误");
                } else if (!yx.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("第四行格式错误");
                }else if (!shangx.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("上限格式错误");
                }else if (!xiax.getText().toString().matches("-?[0-9]+.*[0-9]*")) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("下限格式错误");
                }  else {
                    temperatureSetting.setForehead_X(x.getText().toString());
                    temperatureSetting.setForehead_xx(xx.getText().toString());
                    temperatureSetting.setForehead_Y(y.getText().toString());
                    temperatureSetting.setForehead_Yx(yx.getText().toString());
                    temperatureSetting.setWenshang(shangx.getText().toString());
                    temperatureSetting.setWenxia(xiax.getText().toString());
                    TemperatureSettingHelp.savePoliceInfoToDB(temperatureSetting);
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                    dialog.dismiss();
                }
            }
        });
        edtDevicecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void jumpTotest(View view) {
        startActivity(TestActivity.class);
    }

    public void jumpTodeleteAllFace(View view) {
        showPositivedialog();
    }

    public void jumpToModeChange(View mView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_mode_change, null);
        dialog.setView(view, 0, 0, 0, 0);
        EditText mode = (EditText) view.findViewById(R.id.dialog_mode_edt);
        Button modeconfirm = (Button) view.findViewById(R.id.dialog_mode_confirm);
        Button modecancel = (Button) view.findViewById(R.id.dialog_mode_cancel);
        TextView restart = (TextView) view.findViewById(R.id.mode_reastart);
        restart.setVisibility(View.VISIBLE);
        modeconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.getText() != null&&mode.getText().length()>0) {
                    String string = mode.getText().toString();
                    if (SwitchUtils.isNumeric(string)) {
                        int i = Integer.parseInt(string);
                        if (i >= 1 && i <= 6) {
                            ConfigUtil.setMode(i);
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("修改成功");
                            dialog.dismiss();
                        } else {
                            TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("模式只有一到六");
                        }
                    } else {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("格式错误");
                    }
                } else {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("不能为空");
                }
            }
        });
        modecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showPositivedialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_positive, null);
        dialog.setView(view, 0, 0, 0, 0);
        Button edtpositiveconfirm = (Button) view.findViewById(R.id.dialog_positive_confirm);
        Button edtpositivecancel = (Button) view.findViewById(R.id.dialog_positive_cancel);

        edtpositiveconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PoliceFaceHelp.deleteAllPoliceInfoAllList();
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("删除成功");
                dialog.dismiss();
            }
        });
        edtpositivecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void jumpTodeleteone(View view) {
        //FileUtils.getFileUtilsHelp().openLogFile(this);
        /*PoliceFace policeFaceByNum = PoliceFaceHelp.getPoliceFaceByNum("2859CD");
        if (policeFaceByNum != null) {
            PoliceFaceHelp.deletePoliceInfoAllInDB(policeFaceByNum);
        }*/
        openAssignFolder(FILE_PATH);
    }

    private final static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "日志";

    private void openAssignFolder(String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
        Uri uri = FileProvider.getUriForFile(this, "com.arcsoft.arcfacedemo.provider", file);
        intent.setDataAndType(uri, "file/*");

        try {
            startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void jumpTogetLastVersion(View view) {
        RequestHelper.getRequestHelper().getLastVersion();
    }


    public void jumpToupdateApk(View view) {
        long l = RequestHelper.downLoadApk();
        listener(l);
    }


    private BroadcastReceiver broadcastReceiver;

    private void listener(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                // 这里是通过下面这个方法获取下载的id，
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                // 这里把传递的id和广播中获取的id进行对比是不是我们下载apk的那个id，如果是的话，就开始获取这个下载的路径
                if (ID == Id) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(Id);
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        // 获取文件下载路径
                        String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        // 如果文件名不为空，说明文件已存在,则进行自动安装apk
                        if (fileName != null) {
                            File file = new File(Uri.parse(fileName).getPath());
                            String filePath = file.getAbsolutePath();
                            @SuppressLint("WrongConstant") final KingsunSmartAPI api = (KingsunSmartAPI) getSystemService("kingsunsmartapi");
                            api.installApk(filePath);//静默安装
                        }
                    }
                    cursor.close();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    //初始化人脸引擎
    private FaceEngine faceEngine = new FaceEngine();
    public void jumpToactivation(View view) {
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int activeCode = faceEngine.activeOnline(SettingActivity.this,
                        Constants.ACTIVE_KEY(), Constants.APP_ID, Constants.SDK_KEY);
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
                            ConfigUtil.setFirstStart();

                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                            ConfigUtil.setFirstStart();

                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = faceEngine.getActiveFileInfo(activeFileInfo);
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

    public void jumpToupdateFace(View view) {
        startActivity(new Intent(this,RegistFaceActivity.class));
    }
    private static final String[] NEEDED_PERMISSIONS_OFFLINE = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    private String offline_path = FILE_PATH + File.separator + "离线激活";
    public void jumpToofflineactivation(View view) {
        if (!checkPermissions(NEEDED_PERMISSIONS_OFFLINE)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS_OFFLINE, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        String name = Constants.ACTIVE_KEY().replaceAll("-", "");
        int activeCode = faceEngine.activeOffline(SettingActivity.this,
                offline_path + File.separator + name+".dat");
        if (activeCode == ErrorInfo.MOK) {
            showToast(getString(R.string.active_success));
        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            showToast(getString(R.string.already_activated));
        } else {
            showToast(getString(R.string.active_failed, activeCode));
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
}
