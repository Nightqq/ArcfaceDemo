package com.arcsoft.arcfacedemo.util.communi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.ToDoubleBiFunction;

import android_serialport_api.SerialPort;

import android.app.kingsun.KingsunSmartAPI;
import android.util.Log;

import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;
import com.arcsoft.arcfacedemo.dao.helper.TemperatureSettingHelp;
import com.arcsoft.arcfacedemo.util.utils.ConfigUtil;
import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;
import com.arcsoft.arcfacedemo.util.utils.SwitchUtils;
import com.arcsoft.arcfacedemo.util.utils.TextToSpeechUtils;

import org.greenrobot.greendao.annotation.Id;

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

    public static SerialPortUtils gethelp() {
        if (serialPortUtils == null) {
            serialPortUtils = new SerialPortUtils();
        }
        return serialPortUtils;
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort() {
        try {
            int mode = ConfigUtil.getMode();
           /* if (mode == 3) {
                path = "/dev/ttyCOM0";
                baudrate = 115200;
            }else if (mode == 2) {
                path = "/dev/ttyCOM2";
                baudrate = 9600;
            }*/
            path = "/dev/ttyCOM0";
            baudrate = 115200;
            LogUtils.a("串口：",path+baudrate);
            serialPort = new SerialPort(new File(path), baudrate, 0);
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
    public void closeSerialPort() {
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常：" + e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 发送串口指令（字符串）
     *
     * @param data String数据指令
     */
    public void sendSerialPort(String data) {
        //  Log.d(TAG, "sendSerialPort: 发送数据");

        try {
            byte[] sendData = data.getBytes(); //string转byte[]
            if (sendData.length > 0) {
                outputStream.write(sendData);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                //Log.d(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString());
        }

    }

    /**
     * 发送串口指令（成功/失败）
     */
    private void sendReply(byte num, boolean isok) {
        byte is;
        if (isok) {
            is = 0x00;
        } else {
            is = 0x01;
        }
        byte[] ok = {num, is, 0x00, 0x00};
        ok = SwitchUtils.xor(ok);
        sendSerialPort(ok);//收到卡号返回ok
    }

    /**
     * 发送串口指令（byte[]）
     *
     * @param data byte[]数据指令
     */
    public void sendSerialPort(byte[] data) {
        FileUtils.getFileUtilsHelp().savaserialportLog("回复时间" + System.currentTimeMillis() + "板子：" + SwitchUtils.byte2HexStr(data) + "\n");
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
            LogUtils.a(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString());
        }
    }

    /**
     * 发送人脸识别成功指令给串口
     */
    public void successOpenDoor(String num) {
        byte[] bytes = SwitchUtils.hexStringToByte(num);
        byte[] success = {ConfigUtil.getsuccessOpenDoor(), 0x50, 0x06, 0x00, bytes[2], bytes[1], bytes[0], 0x00, 0x01, 0x00};
        success = SwitchUtils.xor(success);
        sendSerialPort(success);
    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus) {
                Log.d(TAG, "进入线程run");
                //64   1024
                try {
                    byte[] buffer = new byte[16];
                    if (inputStream.read(buffer) > 0) {
                        //LogUtils.a("收到卡号开始解析数据");
                        LogUtils.a("run: 接收到了数据：" + SwitchUtils.byte2HexStr(buffer));
                        btyeParse(buffer);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: 数据读取异常：" + e.toString());
                }
            }
        }
    }

    //解析返回的数据
    public void btyeParse(byte[] buffer) {
        switch (buffer[1]) {
            case 0x4F://温度返回
                    LogUtils.a("日志","温度返回");
                break;
            case 0x40://刷卡返回接收到的卡号
                FileUtils.getFileUtilsHelp().savaserialportLog("收到卡号：" + SwitchUtils.byte2HexStr(buffer) + "\n");
                byte[] bs = new byte[9];
                System.arraycopy(buffer, 0, bs, 0, 9);//目标，起始位置，目的，起始位置，长度
                byte xor = xor(bs);
                if (bs[8] == xor) {//校验正常返回ok
                    sendReply(bs[0], true);
                    byte[] num = {bs[5], bs[4], bs[3]};
                    onDataReceiveListener.onDataReceive(num);
                } else {
                    LogUtils.a("校验和异常");
                    sendReply(bs[0], false);
                }
                break;
            case 0x41://板子播放语音
                FileUtils.getFileUtilsHelp().savaserialportLog("收到时间" + System.currentTimeMillis() + "语音：" + SwitchUtils.byte2HexStr(buffer) + "\n");
                if (isrepeat(buffer[0], buffer[3])) {//是否重复
                    byte[] num = {buffer[4], buffer[3]};
                    String s = SwitchUtils.byte2HexStr(num);
                    sendReply(buffer[0], true);
                    speak(s);
                }
                break;
            case 0x50://人像对比结果，一般主动发送
                break;
            case 0x00://返回成功
                // LogUtils.a("人像对比结果接收成功");
                break;
            case 0x01://返回失败
                LogUtils.a("人像对比结果接收失败");
                break;
        }
    }


    //传入字节转换温度
    public String getwendu(byte a, byte b) {
        int i1 = Integer.parseInt(SwitchUtils.byte2HexStr(a), 16);
        int i2 = Integer.parseInt(SwitchUtils.byte2HexStr(b), 16);
        float i = ((float) (i1 * 256 + i2)) / 100;
        return "" + i;
    }

    private byte allnum = 0x00;
    private byte allyynum = 0x00;

    //判断收到的串口内容是否重复已经处理过
    public boolean isrepeat(byte num, byte yynum) {
        if (num == allnum && allyynum == yynum) {
            return false;
        } else {
            allyynum = yynum;
            allnum = num;
            return true;
        }
    }

    //处理串口返回的命令播放对应语音
    private void speak(String num) {
        num = SwitchUtils.string2Hexstr(num);
         LogUtils.a("板子语音" + num);
        if (num.length() == 2) {
            num = "0" + num;
        }
        switch (num) {
            case "000":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("对门已开");
                break;
            case "001":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("非法卡号刷卡");
                break;
            case "002":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请通过");
                break;
            case "003":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("叮");
                break;
            case "004":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("人太多，请稍候");
                break;
            case "005":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("前门未刷，返回前门重刷");
                break;
            case "006":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("外门未刷，返回外门重刷");
                break;
            case "007":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("内门未刷，返回内门重刷");
                break;
            case "008":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("开门方式设置错误");
                break;
            case "009":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("输入密码错误");
                break;
            case "010":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("门被禁开");
                break;
            case "011":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("外来人员没有人带");
                break;
            case "012":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("未知卡号");
                break;
            case "013":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("人员过期");
                break;
            case "014":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("打开文件错误");
                break;
            case "015":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请所有进监区的人离开通道");
                break;
            case "016":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请所有出监区的人离开通道");
                break;
            case "017":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请求开门");
                break;
            case "018":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("禁止通行");
                break;
            case "019":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("恢复通行");
                break;
            case "020":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请输入密码");
                break;
            case "021":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("不许外来人员刷卡");
                break;
            case "022":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请找警察带领进入");
                break;
            case "023":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请带领警察重带");
                break;
            case "024":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("不是最后一人");
                break;
            case "025":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请刷公共卡");
                break;
            case "026":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请刷个人卡");
                break;
            case "027":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请选择门号");
                break;
            case "028":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("开启时间过长");
                break;
            case "029":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("被强行打开");
                break;
            case "030":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("临时禁止打开");
                break;
            case "031":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请等待监控中心开门");
                break;
            case "032":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("监控中心没有响应");
                break;
            case "033":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("卡号不符，请重新刷卡");
                break;
            case "034":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("读卡器被拆除");
                break;
            case "035":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("需要授权才能开门");
                break;
            case "036":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("监控中心禁止通行");
                break;
            case "037":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("监控中心恢复通行");
                break;
            case "038":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("有人被劫持");
                break;
            case "039":
                //TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请比对人像");
                break;
            case "040":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("本时间段禁止手动开门");
                break;
            case "041":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("比对人像失败，请重新刷卡");
                break;
            case "042":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("消防通道请求刷卡开门");
                break;
            case "043":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请刷卡");
                break;
            case "044":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请按指纹");
                break;
            case "045":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("已超过审批时间");
                break;
            case "046":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("网络不通，请重新刷卡");
                break;
            case "047":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("无47语音");
                break;
            case "048":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请刷卡");
                break;
            case "049":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("人与卡号不符");
                break;
            case "050":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("不得再次进入");
                break;
            case "051":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请外来人员通过后再刷或稍后再刷");
                break;
            case "052":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("外来人员请先验证");
                break;
            case "053":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请带领警察刷卡");//人像比对成功，请带领警察刷卡.重复删除
                break;
            case "054":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请第一等级人员刷卡");
                break;
            case "055":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("请第二等级人员刷卡");
                break;
            case "056":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("门已开，请注意关门");
                break;
            case "057":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("等待塔楼开门");
                break;
            case "058":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("无权限进入");
                break;
            case "059":
                TextToSpeechUtils.getTextToSpeechHelp().notifyNewMessage("夜间禁止开门");
                break;
        }
    }


    //这是写了一监听器来监听接收数据
    public OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer);

        public void onDataReceive(String buffer);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    //异或运算
    public static byte xor(byte[] old) {
        byte temp = 0;
        for (int i = 0; i < old.length - 1; i++) {
            temp ^= old[i];
        }
        return temp;
    }
}
