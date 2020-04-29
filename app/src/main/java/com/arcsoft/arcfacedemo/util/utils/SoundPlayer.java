package com.arcsoft.arcfacedemo.util.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.arcsoft.arcfacedemo.R;

public class SoundPlayer {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayer soundPlayUtils;
    // 上下文
    static Context mContext;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayer init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayer();
        }
        // 初始化声音
        mContext = context;
        mSoundPlayer.load(mContext, R.raw.swipingcard, 1);// 1
        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

}
