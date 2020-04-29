package com.arcsoft.arcfacedemo.util.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.arcsoft.arcfacedemo.activity.arcface.FaceRecognitionActivity;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    private static FileUtils fileUtils;
    //public final static String FILE_PATH = Utils.getContext().getFilesDir().getAbsolutePath() + File.separator + "日志";
    private final static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "日志";
    private String Log_path = FILE_PATH + File.separator + "相似度";
    private String serialport_path = FILE_PATH + File.separator + "串口语音";
    private String abnormal_path = FILE_PATH + File.separator + "异常捕捉";
    private FileUtils() {
    }

    public static FileUtils getFileUtilsHelp() {
        if (fileUtils == null) {
            fileUtils = new FileUtils();
        }
        return fileUtils;
    }

    //保存相似度日志
    public void savaSimilarityLog(String data) {
        File fileDir = new File(Log_path);
        savefile(data, fileDir);
    }

    //保存串口日志
    public void savaserialportLog(String data) {
        File fileDir = new File(serialport_path);
        savefile(data, fileDir);
    }

    //保存异常日志
    public void savaabnormalLog(String data) {
        File fileDir = new File(abnormal_path);
        savefile(data, fileDir);
    }

    private void savefile(String data, File fileDir) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = new Date(System.currentTimeMillis());
            String fileName = simpleDateFormat.format(date);
            String strContent = data;
            if (!fileDir.exists() || !fileDir.getParentFile().exists()) {
                boolean mkdirs = fileDir.getParentFile().mkdirs();
                LogUtils.a(mkdirs);
                if (!fileDir.exists()) {
                    boolean mkdirs1 = fileDir.mkdirs();
                    LogUtils.a(mkdirs1);
                }
            }
            fileDir = new File(fileDir, fileName + ".txt");
            FileWriter fw = new FileWriter(fileDir, true);
            fw.write(strContent);//加上换行
            fw.flush();
            fw.close();
            notifySystemToScan(fileDir);
            //LogUtils.a("结束写文件时间");
        } catch (Exception e) {
            LogUtils.a("结束写文件时间" + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    public void openLogFile(Activity aaa) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtils.a("打开文件有问题");
            return;
        }
        File dir = new File(FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            // 生成文件的uri，，
            // 注意 下面参数com.ausee.fileprovider 为apk的包名加上.fileprovider，
            uri = FileProvider.getUriForFile(aaa, "com.arcsoft.arcfacedemo.provider", dir);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
        } else {
            uri = Uri.fromFile(dir);
        }
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        aaa.startActivityForResult(intent, 200);
    }


    public void notifySystemToScan(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        Utils.getContext().sendBroadcast(intent);
    }


}
