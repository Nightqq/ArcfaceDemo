package com.arcsoft.arcfacedemo.faceserver;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SockrtMap;
import com.arcsoft.arcfacedemo.util.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by chao on 2019/1/30.
 */

public class SignalingClient {

    private static SignalingClient instance;

    private SignalingClient() {
        init();
    }

    public static SignalingClient get() {
        if (instance == null) {
            synchronized (SignalingClient.class) {
                if (instance == null) {
                    instance = new SignalingClient();
                   SharedPreferences arcface = Utils.getContext().getSharedPreferences("Arcface", 0);
                   ip = arcface.getString("ip", SignalingClient.ip);
                   distal=arcface.getString("name",SignalingClient.distal);
                }
            }
        }
        return instance;
    }

    private Socket socket;
    private String roomid = "webrtc_1v1";
    private String account = "111";
    private Callback callback;

    private final TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    String USER_JOIN = "join";
    String USER_CALL = "apply";
    String USER_REPLY = "reply";
    public static String distal = "pc105";
    public static String ip="https://192.168.0.10:3221/#/remote1";
    private void init() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, null);
            IO.setDefaultHostnameVerifier((hostname, session) -> true);
            IO.setDefaultSSLContext(sslContext);
            socket = IO.socket(ip);//https://192.168.0.10:3221/#/remote1
            Emitter.Listener connectListener;
            connectListener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {//连接成功
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomid", roomid);
                        obj.put("account", account);
                        //LogUtils.a("join" + obj);
                        socket.emit(USER_JOIN, obj);//用户注册
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            socket.on(Socket.EVENT_CONNECT, connectListener);
            socket.connect();
            replyParse();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void replyParse() {
        socket.on("apply", args -> {//收到通话请求
            LogUtils.a("收到apply " + Arrays.toString(args));
            Object arg = args[0];
            if (arg instanceof String) {

            } else if (arg instanceof JSONObject) {
                try {
                    JSONObject data = (JSONObject) arg;
                    JSONObject jo = new JSONObject();
                    jo.put("account", data.optString("self"));
                    jo.put("self", account);
                    jo.put("type", "1");

                    LogUtils.a("发送reply " + jo);
                    socket.emit("reply", jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("reply", args -> {//通话请求回复发送offer
            LogUtils.a("reply " + Arrays.toString(args));
            //添加判断是否同意
            callback.onSelfJoined();
        });
        socket.on("1v1offer", args -> {
            LogUtils.a("1v1offer " + Arrays.toString(args));
            Object arg = args[0];
            if (arg instanceof JSONObject) {
                JSONObject data = (JSONObject) arg;
                JSONObject sdp = (JSONObject) data.opt("sdp");
                callback.onOfferReceived(sdp);
            }
        });
        socket.on("1v1hangup", args -> {
            LogUtils.a("1v1hangup " + Arrays.toString(args));
            callback.onPeerLeave("over");
        });
        socket.on("1v1ICE", args -> {
           // LogUtils.a("1v1ICE " + Arrays.toString(args));
            Object arg = args[0];
            if (arg instanceof JSONObject) {
                JSONObject data = (JSONObject) arg;
                JSONObject sdp = (JSONObject) data.opt("sdp");
                callback.onIceCandidateReceived(sdp);
            }
        });
        socket.on("1v1answer", args -> {
            LogUtils.a("1v1answer " + Arrays.toString(args));
            Object arg = args[0];
            if (arg instanceof JSONObject) {
                JSONObject data = (JSONObject) arg;
                JSONObject sdp = (JSONObject) data.opt("sdp");
                callback.onAnswerReceived(sdp);
            }
        });
    }
    public void call() {//呼叫连接
        JSONObject obj = new JSONObject();
        try {
            obj.put("account", distal);
            obj.put("self", account);
            LogUtils.a(USER_CALL + obj);
            socket.emit(USER_CALL, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String type = "offer";
    String type2 = "answer";



    public void sendIceCandidate(IceCandidate miceCandidate) {

        JSONObject jo = new JSONObject();
        try {
            jo.put("account", distal);
            jo.put("self", account);
            JSONObject obj = new JSONObject();
            obj.put("candidate", miceCandidate.sdp);
            obj.put("sdpMid", miceCandidate.sdpMid);
            obj.put("sdpMLineIndex", miceCandidate.sdpMLineIndex);
            jo.put("sdp", obj);
            socket.emit("1v1ICE", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendSessionDescription(SessionDescription mSdp) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("account", distal);
            jo.put("self", account);
            JSONObject obj = new JSONObject();
            obj.put("type", type2);
            obj.put("sdp", mSdp.description);
            jo.put("sdp", obj);
            socket.emit("1v1answer", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendOfferSDP(SessionDescription mSdp) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("account", distal);
            jo.put("self", account);
            JSONObject obj = new JSONObject();
            obj.put("type", type);
            obj.put("sdp", mSdp.description);
            jo.put("sdp", obj);
            socket.emit("1v1offer", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void destroy() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("account", distal);
            jo.put("self", account);
            socket.emit("1v1hangup", jo);
            socket.disconnect();
            socket.close();
            instance = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public interface Callback {
        void onCreateRoom();

        void onPeerJoined();

        void onSelfJoined();

        void onPeerLeave(String msg);

        void onOfferReceived(JSONObject data);

        void onAnswerReceived(JSONObject data);

        void onIceCandidateReceived(JSONObject data);
    }

}
