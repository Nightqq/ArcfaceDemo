package com.arcsoft.arcfacedemo.util.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;

import com.arcsoft.arcfacedemo.activity.App;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.model.ItemShowInfo;
import com.arcsoft.arcfacedemo.util.server.handler.PersonAddHandler;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.util.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaceUtils {
    private static FaceUtils faceUtils;
    private Context mcontext;
    private String root_path;
    private String save_feature_dir;
    private FaceEngine faceEngine;

    private FaceUtils() {
        mcontext = Utils.getContext();
    }

    public static FaceUtils getFaceUtils() {
        if (faceUtils == null) {
            faceUtils = new FaceUtils();

        }
        return faceUtils;
    }

    //传入特征值注册人脸
    public void registerFace(byte[] faceByte, String userName) {
        //特征存储的文件夹
        root_path = Utils.getContext().getFilesDir().getAbsolutePath();
        save_feature_dir = "register" + File.separator + "features";
        boolean dirExists = true;
        File featureDir = new File(root_path + File.separator + save_feature_dir);
        if (!featureDir.exists()) {
            dirExists = featureDir.mkdirs();
        }
        if (!dirExists) {
            LogUtils.a("!dirExists");
            return;
        }
        try {
            FileOutputStream fosFeature = new FileOutputStream(featureDir + File.separator + userName);
            fosFeature.write(faceByte);
            fosFeature.close();
            LogUtils.a("人脸写入结束");
        } catch (Exception e) {
            LogUtils.a(e.getMessage());
        }
    }

    //传入照片注册人脸
    public void registerFace(Bitmap bitmap, String name) {
        PersonAddHandler personAddHandler = new PersonAddHandler();
        personAddHandler.rigistPerson(bitmap, name);
    }

    //传入名字获取人脸特征值
    public byte[] getFace(String name) {
        boolean dirExists = true;
        File featureDir = new File(root_path + File.separator + save_feature_dir);
        if (!featureDir.exists()) {
            dirExists = featureDir.mkdirs();
        }
        if (!dirExists) {
            LogUtils.a("!dirExists");
            return null;
        }
        try {
            FileInputStream in = new FileInputStream(featureDir + File.separator + name);
            long inSize = in.getChannel().size();//判断FileInputStream中是否有内容
            if (inSize == 0) {
                LogUtils.a("The FileInputStream has no content!");
            }
            byte[] buffer = new byte[in.available()];//in.available() 表示要读取的文件中的数据长度
            in.read(buffer);  //将文件中的数据读到buffer中
            return buffer;
        } catch (Exception e) {
            LogUtils.a(e.getMessage());
            return null;
        }
    }

    public void delateFace() {
        FaceServer.getInstance().clearAllFaces(Utils.getContext());
    }

    private void initEngine() {
        faceEngine = new FaceEngine();
        int faceEngineCode = faceEngine.init(Utils.getContext(), FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
    }

}
