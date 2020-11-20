package com.arcsoft.arcfacedemo.util.server.handler;

import com.arcsoft.arcfacedemo.util.server.net.HttpUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
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

public class CallRollHandler implements RequestHandler {

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
                backIntoToView(false, "请求参数异常,请检查");
                return;
            }
            LogUtils.a("服务端发送的数据：", content);
            JSONObject jsonObject = new JSONObject(content);
            String code = jsonObject.getString("code");

            //callRollListenter.setCallRol(content);
            backIntoToView(true, "请求成功");
        } catch (JSONException e) {
            backIntoToView(false, "请求参数不正确");
            e.printStackTrace();
        }
    }


    /**
     * 提交的数据必须是JSON格式的===   ==============================================================================
     */
    private void backIntoToView(boolean success, String msg) {
        try {
            String content = "{\"code\":" + success + ",\"msg\":" + msg + "}";
            JSONObject jsonObject = new JSONObject(content);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GetCallRollListenter callRollListenter = null;

    public interface GetCallRollListenter {
        void setCallRol(String jsonObject);
    }


}
