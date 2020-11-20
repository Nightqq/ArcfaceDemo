package com.arcsoft.arcfacedemo.activity.arcface;

import com.arcsoft.arcfacedemo.activity.BaseActivity;

public class FaceContrastActivity extends BaseActivity {

  /*  @BindView(R.id.face_contrast_rotate)
    ImageView faceContrastRotate;
    private Handler handler = new Handler() {
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_contrast);
        ButterKnife.bind(this);
        SerialPortUtils.gethelp().openSerialPort();
    }

    boolean jumpFlag=false;
    @Override
    protected void onStart() {
        super.onStart();
        //旋转动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        faceContrastRotate.startAnimation(animation);

        SerialPortUtils.gethelp().setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer) {//收到卡号查询的人脸
                String policeNum = SwitchUtils.byte2HexStr(buffer).replaceAll(" ", "");
                LogUtils.a("收到卡号：" + policeNum);
                PoliceFace policeFace = PoliceFaceHelp.getPoliceFaceByNum(policeNum);
                if (policeFace != null) {
                    if (policeFace.getEMP_FEATURE() != null) {
                        App.byteface = SwitchUtils.base64tobyte(policeFace.getEMP_FEATURE());
                        App.police_name=policeFace.getEMP_NAME();
                        LogUtils.a("特征值大小:"+App.byteface.length);
                        if (jumpFlag){
                            jumpFlag=false;
                            handler.post(runnable);
                        }
                    } else {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("有卡号无人脸数据");
                    }
                } else {
                    textToSpeechUtils.notifyNewMessage("数据同步中");
                    RequestHelper.getRequestHelper().getPoliceFace(policeNum, new RequestHelper.OpenDownloadListener() {
                        @Override
                        public void openDownload(String message) {
                            textToSpeechUtils.notifyNewMessage(message);
                            if (message.equals("下载成功")){
                                handler.post(runnable);
                            }
                        }
                    });
                }
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LogUtils.a("收到卡号准备跳转");
                    startActivity(new Intent(FaceContrastActivity.this, FaceRecognitionActivity.class));
                }
            };
        });
    }


    public void jumptotest(View view) {
        showdkdialog();
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
                        if (text.equals("123456")){
                            startActivity(new Intent(FaceContrastActivity.this, SettingActivity.class));
                        }else {
                            textToSpeechUtils.notifyNewMessage("密码错误");
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
                if (editable.length()==6){
                    button.callOnClick();
                }
            }
        });
        dialog.show();
    }*/

}
