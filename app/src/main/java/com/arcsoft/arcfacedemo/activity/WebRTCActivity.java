package com.arcsoft.arcfacedemo.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.faceserver.SignalingClient;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.widget.PeerConnectionAdapter;
import com.arcsoft.arcfacedemo.widget.SdpAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class WebRTCActivity extends AppCompatActivity implements SignalingClient.Callback {

    private String TAG = "WebRTCActivity";
    private MediaStream mediaStreamRemote;
    private PeerConnectionFactory mPeerConnectionFactory;
    private SurfaceViewRenderer remoteSurfaceView;
    private SurfaceViewRenderer localSurfaceView;
    PeerConnection peerConnectionLocal;
    private MediaConstraints audioConstarints;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtc);
        //打开扬声器
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        initWebRTC();
    }

    private void initWebRTC() {
        EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
        //PeerConnectionFactory初始化
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions
                        .builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBaseContext, true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBaseContext);
        //获取mPeerConnectionFactory对象
        mPeerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)//添加编码器
                .setVideoDecoderFactory(defaultVideoDecoderFactory)//添加解码器
                .createPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
        //获取VideoCapturer
        VideoCapturer videoCapturer = createVideoCapturer();
        //获取数据源VideoSource
        VideoSource videoSource = mPeerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        // VideoCapture 与 VideoSource 关联
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        //打开摄像头开始工作
        videoCapturer.startCapture(480, 640, 30);

        //获取显示本地视频的view
        localSurfaceView = findViewById(R.id.LocalSurfaceView);
        localSurfaceView.setMirror(true);
        localSurfaceView.init(eglBaseContext, null);

        VideoTrack videoTrack = mPeerConnectionFactory.createVideoTrack("101", videoSource);
        // display in localView
        videoTrack.addSink(localSurfaceView);

        //获取数据源AudioSource

        audioConstarints = new MediaConstraints();

        //回声消除
        audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        //自动增益
        audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        //高音过滤
        audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        //噪音处理
        audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));

        AudioSource audioSource = mPeerConnectionFactory.createAudioSource(audioConstarints);
        AudioTrack audioTrack = mPeerConnectionFactory.createAudioTrack("102", audioSource);
        //获取显示远端视频的view
        remoteSurfaceView = findViewById(R.id.RemoteSurfaceView);
        remoteSurfaceView.setMirror(false);
        remoteSurfaceView.init(eglBaseContext, null);

        mediaStreamRemote = mPeerConnectionFactory.createLocalMediaStream("mediaStreamRemote");
        mediaStreamRemote.addTrack(videoTrack);
        mediaStreamRemote.addTrack(audioTrack);

        SignalingClient.get().setCallback(this);

        call();

    }

    public void jumpTojion(View view) {

        SignalingClient.get().call();
    }

    private void call() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        // iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());//穿透服务器
        peerConnectionLocal = mPeerConnectionFactory.createPeerConnection(iceServers, new PeerConnectionAdapter("localconnection") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                LogUtils.a("onIceCandidatexxxxxxx");
                SignalingClient.get().sendIceCandidate(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                LogUtils.a("onAddStream");
                super.onAddStream(mediaStream);
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
               // AudioTrack audioTrack = mediaStream.audioTracks.get(0);
                runOnUiThread(() -> {
                    remoteVideoTrack.addSink(remoteSurfaceView);
                });
            }
        });
        peerConnectionLocal.addStream(mediaStreamRemote);
    }


    private VideoCapturer createVideoCapturer() {
        if (Camera2Enumerator.isSupported(this)) {
            return createCameraCapturer(new Camera2Enumerator(this));
        } else {
            return createCameraCapturer(new Camera1Enumerator(true));
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        //  LogUtils.a(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                //  LogUtils.a(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // Front facing camera not found, try something else
        LogUtils.a(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                LogUtils.a(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreateRoom() {
    }

    @Override
    public void onPeerJoined() {
    }

    @Override
    public void onSelfJoined() {
        LogUtils.a("onSelfJoined");
        peerConnectionLocal.createOffer(new SdpAdapter("local offer sdp") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                LogUtils.a("sdp" + "onCreateSuccess");
                super.onCreateSuccess(sessionDescription);
                peerConnectionLocal.setLocalDescription(new SdpAdapter("local set local"), sessionDescription);
                SignalingClient.get().sendOfferSDP(sessionDescription);
            }

            @Override
            public void onCreateFailure(String s) {
                super.onCreateFailure(s);
                LogUtils.a("sdp" + "onCreateFailure" + s);
            }
        }, audioConstarints);
    }

    @Override
    public void onPeerLeave(String msg) {
        this.finish();
    }

    @Override
    public void onOfferReceived(JSONObject data) {
        LogUtils.a("onOfferReceived");
        runOnUiThread(() -> {
            try {
                peerConnectionLocal.setRemoteDescription(new SdpAdapter("localSetRemote"),
                        new SessionDescription(SessionDescription.Type.OFFER, data.getString("sdp")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            peerConnectionLocal.createAnswer(new SdpAdapter("localAnswerSdp") {
                @Override
                public void onCreateSuccess(SessionDescription sdp) {
                    super.onCreateSuccess(sdp);
                    LogUtils.a("sdp" + "onCreateSuccess");
                    peerConnectionLocal.setLocalDescription(new SdpAdapter("localSetLocal"), sdp);
                    SignalingClient.get().sendSessionDescription(sdp);
                }
                @Override
                public void onCreateFailure(String s) {
                    super.onCreateFailure(s);
                    LogUtils.a("sdp" + "onCreateFailure" + s);
                }
            }, new MediaConstraints());
        });
    }

    @Override
    public void onAnswerReceived(JSONObject data) {
        LogUtils.a("onAnswerReceived");
        try {
            peerConnectionLocal.setRemoteDescription(new SdpAdapter("localSetRemote"),
                    new SessionDescription(SessionDescription.Type.ANSWER, data.getString("sdp")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIceCandidateReceived(JSONObject data) {
        LogUtils.a("onIceCandidateReceived" + data);
        try {
            peerConnectionLocal.addIceCandidate(new IceCandidate(
                    data.getString("sdpMid"),
                    data.getInt("sdpMLineIndex"),
                    data.getString("candidate")
            ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SignalingClient.get().destroy();
    }

}
