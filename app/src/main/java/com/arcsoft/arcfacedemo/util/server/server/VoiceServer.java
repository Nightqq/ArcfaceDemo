package com.arcsoft.arcfacedemo.util.server.server;

import com.arcsoft.arcfacedemo.common.IpPort;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class VoiceServer {
    DatagramSocket socket;
    public handleReceiveData callback;
    public VoiceServer(int port) throws SocketException {
        //服务端DatagramSocket
        socket = new DatagramSocket(port);
        System.out.println("服务器启动。");
    }

    public void setReceiveCallback(handleReceiveData call) {
        callback = call;
    }
    public void service() throws IOException {
        while (true) {
            DatagramPacket dp = new DatagramPacket(new byte[102400], 102400);
            socket.receive(dp); //接收客户端信息
            byte[] data = dp.getData();
            callback.handleReceive(data);
        }
    }
    public void start() throws SocketException, IOException {
        service();
    }
    //发送语音
    public void sendVoice(final byte[] data) {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        LogUtils.a("发送语音");
                        SocketAddress socketAddres = new InetSocketAddress(IpPort.distal_ip, IpPort.distal_port);//发送给远端
                        socket.send(new DatagramPacket(data, data.length, socketAddres));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        send.start();
    }

}
