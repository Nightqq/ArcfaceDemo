package com.arcsoft.arcfacedemo.activity.setting;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.BaseActivity;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        Button button = (Button) view.findViewById(R.id.dialog_download_confirm);
        Button button_cancel = (Button) view.findViewById(R.id.dialog_download_cancel);
        TextView textView = (TextView) view.findViewById(R.id.dialog_download_text);
        ProgressBar  progressBar = (ProgressBar) view.findViewById(R.id.dialog_download_progressbar);
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
        ProgressBar  progressBar = (ProgressBar) view.findViewById(R.id.dialog_download_progressbar);
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
                        } else if (SwitchUtils.isNumeric(msgs)) {
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
}
