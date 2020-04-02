package com.arcsoft.arcfacedemo.util.server.handler;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.arcsoft.arcfacedemo.common.IpPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReceiveThread  {

    private  MulticastSocket multicastSocket;

    public ReceiveThread() {
        // 接收数据时需要指定监听的端口号
        try {
            initAudioTracker();
            multicastSocket = new MulticastSocket(IpPort.distal_Audio_port);
            // 创建组播ID地址
            InetAddress address = InetAddress.getByName(IpPort.distal_ip);
            // 加入地址
            multicastSocket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    byte[] buffer;
    AudioTrack audioTrk;

    private void initAudioTracker() {
        //扬声器播放
        int streamType = AudioManager.STREAM_MUSIC;
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = 44100;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //流模式
        int mode = AudioTrack.MODE_STREAM;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int recBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                channelConfig,
                audioFormat);
        System.out.println("****playRecBufSize = " + recBufSize);
        audioTrk = new AudioTrack(
                streamType,
                sampleRate,
                channelConfig,
                audioFormat,
                recBufSize,
                mode);
        audioTrk.setStereoVolume(AudioTrack.getMaxVolume(),
                AudioTrack.getMaxVolume());
        buffer = new byte[recBufSize];
    }

    public void run() {
        if (multicastSocket == null)
            return;
        //从文件流读数据
        audioTrk.play();
        // 包长
        while (true) {
            try {
                // 数据报
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                // 接收数据，同样会进入阻塞状态
                multicastSocket.receive(datagramPacket);
                audioTrk.write(datagramPacket.getData(), 0, datagramPacket.getLength());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
