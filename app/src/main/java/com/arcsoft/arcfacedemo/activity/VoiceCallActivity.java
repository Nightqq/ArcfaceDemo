package com.arcsoft.arcfacedemo.activity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.common.IpPort;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.server.handler.ReceiveThread;
import com.arcsoft.arcfacedemo.util.server.handler.SendThread;
import com.arcsoft.arcfacedemo.util.server.server.VoiceServer;
import com.arcsoft.arcfacedemo.util.server.server.handleReceiveData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.LinkedList;

public class VoiceCallActivity extends AppCompatActivity implements handleReceiveData {


    private SendThread sendThread;
    private ReceiveThread receiveThread;
    VoiceServer up;
    VoiceServer upa;
    private AudioTrack player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);
        initserver();
        initAudio();
    }

    private void initserver() {
        Thread service = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    up = new VoiceServer(IpPort.local_port);//本地端口
                    up.setReceiveCallback(VoiceCallActivity.this);
                    up.start();
                    upa = new VoiceServer(IpPort.distal_port);//远端端口
                    up.setReceiveCallback(new handleReceiveData() {
                        @Override
                        public void handleReceive(byte[] data) {

                        }
                    });
                    // button.setEnabled(false);
                } catch (SocketException e) {
                    //button.setEnabled(true);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        service.start();
    }

    public void jumpTovideocall(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {//开始一直发送语音
                recording();
            }
        }).start();
    }

    private int audiosize = 8000;
    //录音
    private void recording() {
        audioRec.startRecording();// 开始录音
        byte[] readBuffer = new byte[640];// 录音缓冲区
        int length = 0;
        while (true) {
            length = audioRec.read(readBuffer, 0, 640);// 从mic读取音频数据
            if (length > 0 && length % 2 == 0) {
                // 把音频数据通过网络发送给对方
                up.sendVoice(readBuffer);
            }
        }
        // recorder.stop();
        // recorder.release();
        // recorder = null;
    }

    private  MulticastSocket multicastSocket;
    private  InetAddress address;
    protected LinkedList<byte[]> mRecordQueue;
    int minBufferSize;
    private static AcousticEchoCanceler aec;
    private static AutomaticGainControl agc;
    private static NoiseSuppressor nc;
    AudioRecord audioRec;

    private void initAudio() {
        //播放对象创建
        int bufferSize = android.media.AudioTrack.getMinBufferSize(audiosize,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // 获得音轨对象
        player = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, audiosize,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);
        // 设置喇叭音量
        player.setStereoVolume(1.0f, 1.0f);

        //录音对应创建
        // 获得录音缓冲区大小
        minBufferSize = AudioRecord.getMinBufferSize(audiosize, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // 获得录音机对象
        audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC, audiosize, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 10);

        if (audioRec == null) {
            return;
        }
        //声学回声消除器 AcousticEchoCanceler 消除了从远程捕捉到音频信号上的信号的作用
        if (AcousticEchoCanceler.isAvailable()) {
            aec = AcousticEchoCanceler.create(audioRec.getAudioSessionId());
            if (aec != null) {
                aec.setEnabled(true);
            }
        }
        //自动增益控制 AutomaticGainControl 自动恢复正常捕获的信号输出
        if (AutomaticGainControl.isAvailable()) {
            agc = AutomaticGainControl.create(audioRec.getAudioSessionId());
            if (agc != null) {
                agc.setEnabled(true);
            }
        }
        //噪声抑制器 NoiseSuppressor 可以消除被捕获信号的背景噪音
        if (NoiseSuppressor.isAvailable()) {
            nc = NoiseSuppressor.create(audioRec.getAudioSessionId());
            if (nc != null) {
                nc.setEnabled(true);
            }
        }
    }

    @Override
    public void handleReceive(byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {//播放语音
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 开始播放声音
                        player.play();
                        byte[] audio = new byte[160];// 音频读取缓存
                        int length = 0;
                        while (true) {
                           // length = is.read(audio);// 从网络读取音频数据
                            byte[] temp = audio.clone();
                            length=temp.length;
                            if (length > 0 && length % 2 == 0) {
                                // for(int
                                // i=0;i<length;i++)audio[i]=(byte)(audio[i]*2);//音频放大1倍
                                LogUtils.a("播放语音");
                                player.write(audio, 0, temp.length);// 播放音频数据
                            }
                        }
                        //player.stop();
                       // player.release();
                       // player = null;
                    }
                }).start();
            }
        });
    }
}
