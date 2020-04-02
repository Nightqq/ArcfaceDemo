package com.arcsoft.arcfacedemo.util.communi;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.ToDoubleBiFunction;

import android_serialport_api.SerialPort;
import android.util.Log;

import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;

public class SerialPortUtils {
    private final String TAG = "SerialPortUtils";
    private String path = "/dev/ttyCOM2";
    private int baudrate = 9600;
    public boolean serialPortStatus = false; //是否打开串口标志
    public boolean threadStatus; //线程状态，为了安全终止线程
    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    private static SerialPortUtils serialPortUtils;

    private SerialPortUtils() {

    }

    public static SerialPortUtils gethelp(){
        if (serialPortUtils==null){
            serialPortUtils = new SerialPortUtils();
        }
        return serialPortUtils;
    }

    /**
     * 打开串口
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort(){
        try {
            serialPort = new SerialPort(new File(path),baudrate,0);
            this.serialPortStatus = true;
            threadStatus = false; //线程状态
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口");
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常："+e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 发送串口指令（字符串）
     * @param data String数据指令
     */
    public void sendSerialPort(String data){
        Log.d(TAG, "sendSerialPort: 发送数据");

        try {
            byte[] sendData = data.getBytes(); //string转byte[]
            if (sendData.length > 0) {
                outputStream.write(sendData);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败："+e.toString());
        }

    }
    public void sendSerialPort( byte[] data){
        LogUtils.a(TAG, "sendSerialPort: 发送数据");
        try {
            if (data.length > 0) {
                outputStream.write(data);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                //LogUtils.a(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            LogUtils.a(TAG, "sendSerialPort: 串口数据发送失败："+e.toString());
        }
    }

    public void successOpenDoor(String num){
        byte[] bytes = SwitchUtils.hexStringToByte(num);
        byte[] success={ConfigUtil.getsuccessOpenDoor(),0x50,0x06,0x00,bytes[2],bytes[1],bytes[0],0x00,0x01,0x00};
        success = SwitchUtils.xor(success);
        sendSerialPort(success);
    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                Log.d(TAG, "进入线程run");
                //64   1024
                try {
                    byte[] buffer = new byte[16];
                    if (inputStream.read(buffer )> 0){
                        LogUtils.a("收到卡号开始解析数据");
                        Log.d(TAG, "run: 接收到了数据：" + SwitchUtils.byte2HexStr(buffer));
                        btyeParse(buffer);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: 数据读取异常：" +e.toString());
                }
            }
        }
    }

    //解析返回的数据
    public void btyeParse(byte[] buffer){
        switch (buffer[1]){
            case 0x40://刷卡返回接收到的卡号
                byte[] bs = new byte[9];
                System.arraycopy(buffer,0,bs,0,9);//目标，起始位置，目的，起始位置，长度
                byte xor = xor(bs);
                if (bs[8]==xor){//校验正常返回ok
                    byte[] ok={bs[0],0x00,0x00,0x00};
                    ok = SwitchUtils.xor(ok);
                    sendSerialPort(ok);//收到卡号返回ok
                    byte[] num={bs[5],bs[4],bs[3]};
                    onDataReceiveListener.onDataReceive(num);
                }else {
                    LogUtils.a("校验和异常");
                    byte[] no={bs[0],0x01,0x00,0x00};
                    no = SwitchUtils.xor(no);
                    sendSerialPort(no);
                }
                break;
            case 0x50://人像对比结果，一般主动发送
                break;
            case 0x00://返回成功
                LogUtils.a("开门成功");
                break;
            case 0x01://返回失败
                LogUtils.a("开门失败");
                break;

        }
    }


    //这是写了一监听器来监听接收数据
    public OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    //异或运算
    public static byte xor(byte[] old) {
        byte temp = 0;
        for (int i = 0; i < old.length-1; i++) {
            temp ^= old[i];
        }
        return temp;
    }
}
