package com.arcsoft.arcfacedemo.activity.callroll;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.WebRTCActivity;
import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;
import com.arcsoft.arcfacedemo.dao.bean.PrisonerFace;
import com.arcsoft.arcfacedemo.dao.helper.PoliceFaceHelp;
import com.arcsoft.arcfacedemo.dao.helper.PrisonerFaceHelp;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.net.bean.JsonCallRoll;
import com.arcsoft.arcfacedemo.util.server.handler.CallRollHandler;
import com.arcsoft.arcfacedemo.util.server.server.OnServerChangeListener;
import com.arcsoft.arcfacedemo.util.server.server.ServerPresenter;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.OkhttpUtil;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CallRollActivity extends AppCompatActivity implements OnServerChangeListener {

    private ServerPresenter serverPresenter;
    private Handler handler = new Handler() {};

    Runnable runnable_start_callroll = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(CallRollActivity.this,CriminalFacecontrastActivity.class));
        }
    };
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_roll);
        serverPresenter = new ServerPresenter(this, this);
        serverPresenter.startServer(CallRollActivity.this);
        CallRollHandler.GetCallRollListenter setCallRoll = new CallRollHandler.GetCallRollListenter() {
            @Override
            public void setCallRol(String jsonObject) {
                try {
                    JSONObject object = new JSONObject(jsonObject);
                    String code = object.getString("code");
                    LogUtils.a("阿东code", code);
                    if (code.equals("001")) {//点名开始
                        JsonCallRoll jsonCallRoll = JSON.parseObject(jsonObject, JsonCallRoll.class);
                        int call_roll_mode = jsonCallRoll.getCall_roll_mode();
                        List<PrisonerFace> prisonerFaceList = jsonCallRoll.getPrisonerFaceList();
                        PrisonerFaceHelp.savePrisonerFaceAllListToDB(prisonerFaceList);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("30秒后开始点名请准备");
                        handler.postDelayed(runnable_start_callroll,3000);
                    } else if (code.equals("004")) {//取消报警
                        CallRollActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        if (dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
    }

    public void jumpToCriminalFacecontrast(View view) {
        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("30秒后开始点名请准备");
        handler.postDelayed(runnable_start_callroll,3000);
        PoliceFace policeFaceByNum = PoliceFaceHelp.getPoliceFaceByNum("2859CD");
        if (policeFaceByNum!=null){
            PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace(policeFaceByNum.getEMP_ID(),policeFaceByNum.getEMP_NAME(),policeFaceByNum.getEMP_FEATURE()));
        }
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("002","张二",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("003","张三",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("005","张五",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("006","张六",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("007","张七",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("008","张八",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("009","张九",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("010","张妖灵",""));
        PrisonerFaceHelp.savePrisonerFaceToDB(new PrisonerFace("011","张零吆吆",""));
        RequestHelper.getRequestHelper().uploadPrisonerCallRoll();
    }

    @Override
    public void onServerStarted(String ipAddress) {
        LogUtils.a("IP Address: " + ipAddress);
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
    public void jumpToCallVideo(View view) {
        Intent intent = new Intent(this, RadioActivity.class);
        intent.putExtra("mode",1);//1主动呼叫，2被呼叫
        startActivity(intent);
    }

    public void jumpToCallPolice(View view) {
        showPositivedialog();
    }


    public void showPositivedialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_positive, null);
        dialog.setView(view, 0, 0, 0, 0);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("请再次确认是否需要报警\n(确认后不能取消)");
        Button edtpositiveconfirm = (Button) view.findViewById(R.id.dialog_positive_confirm);
        Button edtpositivecancel = (Button) view.findViewById(R.id.dialog_positive_cancel);
        edtpositiveconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestHelper.getRequestHelper().callPolice();

                //设置全屏不可操5261作，也4102就是触摸失效
                CallRollActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("报警中，次设备现不可操作，等待管理员回复");
                dialog_title.setText("报警中，等待管理员回复");
                edtpositiveconfirm.setVisibility(View.GONE);
                edtpositivecancel.setVisibility(View.GONE);
                dialog.setCancelable(false);
                //dialog.dismiss();
                //取消全屏不可触摸
               // CallRollActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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



}
