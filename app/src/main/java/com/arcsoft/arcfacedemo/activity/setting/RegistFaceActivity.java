package com.arcsoft.arcfacedemo.activity.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.net.RequestHelper;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistFaceActivity extends AppCompatActivity {

    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.regist_activity)
    Button registActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 111);
    }

    public void jumpTotakephoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RegistFaceActivity.this, "取消了拍照", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap photo = data.getParcelableExtra("data");
            ivShow.setImageBitmap(photo);
            registActivity.setVisibility(View.VISIBLE);
        } else if (requestCode == 222) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RegistFaceActivity.this, "点击取消从相册选择", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Uri imageUri = data.getData();
                ivShow.setImageURI(imageUri);
                registActivity.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void jumpToregist(View view) {
        showdiog();
    }
    private void showdiog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_regist, null);
        dialog.setView(view, 0, 0, 0, 0);
        TextView id = (TextView) view.findViewById(R.id.dialog_id);
        Button edtpositiveconfirm = (Button) view.findViewById(R.id.dialog_device_confirm);
        Button edtpositivecancel = (Button) view.findViewById(R.id.dialog_device_cancel);
        edtpositiveconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mID = id.getText().toString();
                if (mID == null || mID.length() == 0) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("数据不能为空");
                } else {
                    ivShow.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(ivShow.getDrawingCache());
                    ivShow.setDrawingCacheEnabled(false);
                    String base64String = FaceServer.getInstance().registerToDao(bitmap);
                    if (base64String!=null&&base64String.length()>0) {
                        RequestHelper.getRequestHelper().updateFace(mID,base64String,true);
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("开始上传");
                    } else {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("注册失败");
                    }
                    dialog.dismiss();
                }
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