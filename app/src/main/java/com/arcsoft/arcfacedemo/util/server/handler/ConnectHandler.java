package com.arcsoft.arcfacedemo.util.server.handler;

import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.server.net.HttpUtils;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ConnectHandler implements RequestHandler {

    HttpResponse httpResponse;

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        String content = HttpRequestParser.getContentFromBody(httpRequest);  //body      形式请求
        this.httpResponse = httpResponse;
//        String username = (String) httpContext.getAttribute("username");
//        String content = HttpRequestParser.getContentFromUri(httpRequest);     //get请求
        parsenerJsonContent(content);
    }

    /**
     * 解析请求信息
     *
     * @param content
     */
    private void parsenerJsonContent(String content) {
        LogUtils.a(content);
        try {
            if (content == null || content.length() < 2) {
                backIntoToView(-1, "请求参数异常,请检查");
                return;
            }
            JSONObject jsonObject = new JSONObject(content);
            HttpUtils.ip = jsonObject.getString("ip");
            HttpUtils.port = jsonObject.getString("port");
            backIntoToView(200, "请求成功");
        } catch (JSONException e) {
            backIntoToView(-1, "请求参数不正确");
            e.printStackTrace();
        }
    }


    /**
     * 提交的数据必须是JSON格式的=================================================================================
     */
    private void backIntoToView(int code, String msg) {
        try {
            String content = "{\"code\":" + code + ",\"msg\":" + msg + "}";
            JSONObject jsonObject = new JSONObject(content);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
