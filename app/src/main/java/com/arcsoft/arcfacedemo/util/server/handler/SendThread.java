package com.arcsoft.arcfacedemo.util.server.handler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.arcsoft.arcfacedemo.common.IpPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;

public class SendThread {
    private  MulticastSocket multicastSocket;
    private  InetAddress address;
    protected LinkedList<byte[]> mRecordQueue;
    int minBufferSize;
    private static AcousticEchoCanceler aec;
    private static AutomaticGainControl agc;
    private static NoiseSuppressor nc;
    AudioRecord audioRec;
    byte[] buffer;

    public SendThread() {
        // 侦听的端口
        try {
            initAudio();
            multicastSocket = new MulticastSocket(IpPort.local_Audio_port);
            // 使用D类地址，该地址为发起组播的那个ip段，即侦听10001的套接字
            address = InetAddress.getByName(IpPort.distal_ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initAudio() {
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = 44100;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;

        minBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        System.out.println("****RecordMinBufferSize = " + minBufferSize);
        audioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize);
        buffer = new byte[minBufferSize];

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
        mRecordQueue = new LinkedList<byte[]>();
    }


    public void run() {
        if (multicastSocket == null)
            return;
        try {
            audioRec.startRecording();
            while (true) {
                try {
                    byte[] bytes_pkg = buffer.clone();
                    if (mRecordQueue.size() >= 2) {
                        int length = audioRec.read(buffer, 0, minBufferSize);
                        // 组报
                        DatagramPacket datagramPacket = new DatagramPacket(buffer, length);
                        // 向组播ID，即接收group /239.0.0.1  端口 10001
                        datagramPacket.setAddress(address);
                        // 发送的端口号
                        datagramPacket.setPort(IpPort.distal_Audio_port);
                        System.out.println("AudioRTwritePacket = " + datagramPacket.getData().toString());

                        multicastSocket.send(datagramPacket);
                    }
                    mRecordQueue.add(bytes_pkg);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
