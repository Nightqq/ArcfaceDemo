package com.arcsoft.arcfacedemo.activity.local;

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
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistActivity extends AppCompatActivity {

    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.regist_activity)
    Button registActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        FaceServer.getInstance().init(this);
    }

    public void jumpTotakephoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 111);
    }

    public void jumpToPhotoalbum(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RegistActivity.this, "取消了拍照", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap photo = data.getParcelableExtra("data");
            ivShow.setImageBitmap(photo);
            registActivity.setVisibility(View.VISIBLE);
        } else if (requestCode == 222) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RegistActivity.this, "点击取消从相册选择", Toast.LENGTH_LONG).show();
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


    private void showdiog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_regist, null);
        dialog.setView(view, 0, 0, 0, 0);
        TextView name = (TextView) view.findViewById(R.id.dialog_name);
        TextView id = (TextView) view.findViewById(R.id.dialog_id);
        Button edtpositiveconfirm = (Button) view.findViewById(R.id.dialog_device_confirm);
        Button edtpositivecancel = (Button) view.findViewById(R.id.dialog_device_cancel);
        edtpositiveconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mName = name.getText().toString();
                String mID = id.getText().toString();
                if (mName == null || mID == null || mName.length() == 0 || mID.length() == 0) {
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("数据不能为空");
                } else {
                    ivShow.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(ivShow.getDrawingCache());
                    ivShow.setDrawingCacheEnabled(false);
                    TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("已删除");
                   /* boolean b = FaceServer.getInstance().registerToDao(bitmap, mName, mID);
                    if (b) {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("注册成功");
                    } else {
                        TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("注册失败");
                    }*/
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

    public void jumpToregist(View view) {
        showdiog();
    }

    public void jumpTofinish(View view) {
        startActivity(new Intent(this,LocalActivity.class));
        finish();
    }
}