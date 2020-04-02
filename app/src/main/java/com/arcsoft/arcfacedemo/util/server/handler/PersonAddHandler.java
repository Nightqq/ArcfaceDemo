package com.arcsoft.arcfacedemo.util.server.handler;

import android.graphics.Bitmap;

import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.Utils;
import com.arcsoft.arcfacedemo.util.image.ImageBase64Utils;
import com.arcsoft.face.util.ImageUtils;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PersonAddHandler  implements RequestHandler {

    HttpResponse httpResponse;

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        String content = HttpRequestParser.getContentFromBody(httpRequest);  //body      形式请求
        this.httpResponse = httpResponse;
        parsenerJsonContent(content);
    }

    /**
     * 解析请求信息
     *
     * @param content
     */
    private void parsenerJsonContent(String content) {
        //LogUtils.a(content);
        try {
            if (content == null || content.length() < 2) {
                backIntoToView(-1, "请求参数异常,请检查", null);
                return;
            }
            JSONObject jsonObject = new JSONObject(content);
            String personSerial = jsonObject.getString("personSerial");
            String personName = jsonObject.getString("personName");
            JSONArray faceList = jsonObject.getJSONArray("faceList");
            JSONObject jsonObject1 = faceList.getJSONObject(0);
            String imageBase64 = jsonObject1.getString("imageBase64");

            Bitmap bitmap = ImageBase64Utils.stringtoBitmap(imageBase64);
            imageListenter.setimage(bitmap);
            rigistPerson(bitmap,jsonObject1.getString("imageName"));
            backIntoToView(200, "人员"+personSerial+personName+"接受成功", null);
        } catch (JSONException e) {
            backIntoToView(-1, "请求参数不正确", null);
            e.printStackTrace();
        }
    }

    public void rigistPerson(Bitmap bitmap,String name){
        //本地人脸库初始化
        FaceServer.getInstance().init(Utils.getContext());

        bitmap = ImageUtils.alignBitmapForBgr24(bitmap);
        byte[] bgr24 = ImageUtils.bitmapToBgr24(bitmap);
        boolean b = FaceServer.getInstance().registerBgr24(Utils.getContext(), bgr24, bitmap.getWidth(), bitmap.getHeight(), name);
        LogUtils.a("注册人脸结果"+b);
    }

    /**
     * 提交的数据必须是JSON格式的=================================================================================
     */
    private void backIntoToView(int code, String msg, String data) {
        try {
            String content = "{\"code\":" + code + ",\"msg\":" + msg + ",\"data\":" + data + "}";
            JSONObject jsonObject = new JSONObject(content);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static getImageListenter imageListenter=null;
    public interface getImageListenter{
        void setimage(Bitmap bitmap);
    }


}
