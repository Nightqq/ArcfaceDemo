package com.arcsoft.arcfacedemo.util.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by admin on 2017/11/19.
 * 声音播放类
 */

public class AudioPlayUtils {

    private static Context mContext = null;

    AudioManager audioManager;

    MediaPlayer playerSound;

    Thread playThread;

    /**
     * 播放声音的资源ID
     */
    private static int mRawId;

    private boolean isLoop = false;

   // public static boolean isPLayComplete=false;
    private static AudioPlayUtils audioPlayUtils;

    private AudioPlayUtils() {
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);
        //使用扬声器播放，即使已经插入耳机
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public static AudioPlayUtils getAudio( int rawId) {
        mRawId = rawId;
        mContext = Utils.getContext();
        if (audioPlayUtils == null) {
            audioPlayUtils = new AudioPlayUtils();
        }
        return audioPlayUtils;
    }


    class PlayThread implements Runnable {
        @Override
        public void run() {
            if (audioManager!=null) {
                if (audioManager.isSpeakerphoneOn()) {
                    //  LogUtils.e("扬声器打开了1");
                } else {
                    audioManager.setSpeakerphoneOn(false);
                    //   LogUtils.e("扬声器关闭了");
                    if (audioManager.isSpeakerphoneOn()) {
                        //  LogUtils.e("扬声器打开了2");
                    } else {
                        //  LogUtils.e("扬声器还是没打开");
                    }
                }
                playerSound = MediaPlayer.create(mContext, mRawId);
                playerSound.setLooping(isLoop);
                playerSound.start();
                playerSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (playerSound != null) {
                            playerSound.release();
                        }
                        LogUtils.a("语音lll结束");
                        // LogUtils.a( isPLayComplete);
                        isLoop = false;
                    }
                });
            }
        }
    }

  /*  private boolean isPLayComplete() {//循环播放是否运行
        return isPLayComplete;
    }

    private void setPLayComplete(boolean PLayComplete) {
        isPLayComplete = PLayComplete;
    }*/

    public void play() {
        playThread = new Thread(new PlayThread());
        playThread.run();
    }

    public void play(boolean flag) {
        isLoop = true;
        play();
    }

}
