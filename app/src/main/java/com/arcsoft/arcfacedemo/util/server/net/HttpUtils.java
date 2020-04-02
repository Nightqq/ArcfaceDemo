package com.arcsoft.arcfacedemo.util.server.net;

import com.alibaba.fastjson.JSON;
import com.arcsoft.arcfacedemo.model.FaceRecordInfo;
import com.arcsoft.arcfacedemo.model.VerificationInfo;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static String ip = "";
    public static String port = "";
    private static URL url;
    private static HttpUtils httpUtils;
    private HttpURLConnection conn;
    private ByteArrayOutputStream baos;
    private final String face;


    private HttpUtils() {
        VerificationInfo verificationInfo = new VerificationInfo();
        verificationInfo.setEquipmentID(4);
        verificationInfo.setEquipmentVerificationID("63912e99229f57f01fcc77a2e116fa63");
        verificationInfo.setPersoncode("000001");
        verificationInfo.setVerificationType(1);
        verificationInfo.setCheckTime(000001);
        verificationInfo.setImageName("success_[230_61_330_160_2]_20191031103203297");
        verificationInfo.setRecognitionName("zzd");

        List<VerificationInfo> list = new ArrayList<>();
        list.add(verificationInfo);

        FaceRecordInfo faceRecordInfo = new FaceRecordInfo();
        faceRecordInfo.setVerificationList(list);
        face = JSON.toJSONString(faceRecordInfo);
        LogUtils.a(face);
    }


    public static HttpUtils getHttpHeper() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    public void postJson() {
        try {
            url = new URL("http://" + "192.168.0.105" + ":" + "8081" + "/attendance"); //对象创建
            LogUtils.a("http://" + ip + ":" + port + "/attendance");
            conn = (HttpURLConnection) url.openConnection();//创建链接对象，强转对象打开链接
            conn.setRequestMethod("POST");//设置请求类型（大写）
            conn.setUseCaches(false);//是否使用缓存
            // 发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);
          //  conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type", " application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(face.length()));
            conn.connect();   //连接

            PrintWriter print = new PrintWriter(conn.getOutputStream());//设置输出流
           /* StringBuilder builder = new StringBuilder();                        //用map设置key值
            for (Map.Entry<String, String> entry : map2.entrySet()) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
            builder.substring(0, builder.length() - 1); //不显示最后一个&
            String parameters = URLEncoder.encode(builder.toString(), "UTF-8");*/

            print.write(face);            //设置key 发出请求
            // flush输出流的缓冲
            print.flush();

            //开始获得数据
            InputStream bis = conn.getInputStream(); //获得输出流
            baos = new ByteArrayOutputStream();
            int len;
            byte[] b = new byte[2048];
            while ((len = bis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            baos.flush();
            //返回字符串
            LogUtils.a(new String(baos.toByteArray()));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }


}
