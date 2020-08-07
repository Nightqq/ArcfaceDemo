package com.arcsoft.arcfacedemo.util.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.arcsoft.face.FaceEngine;

public class ConfigUtil {
    private static final String APP_NAME = "ArcFaceDemo";
    private static final String TRACK_ID = "trackID";
    private static final String FT_ORIENT = "ftOrient";
    private static final String First_Start = "FirstStart";
    private static final String Start_self = "startself";
    private static final String Success_Open_Door = "successOpenDoor";
    public static final String MODE = "mode";
    public static final String Apk_name = "查看器.apk";

    public static void setTrackId(Context context, int trackId) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(TRACK_ID, trackId)
                .apply();
    }

    public static int getTrackId(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TRACK_ID, 0);
    }

    public static void setFtOrient(Context context, int ftOrient) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(FT_ORIENT, ftOrient)
                .apply();
    }

    public static int getFtOrient(Context context) {
        if (context == null) {
            return FaceEngine.ASF_OP_270_ONLY;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(FT_ORIENT, FaceEngine.ASF_OP_270_ONLY);
    }

    public static void setFirstStart() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(First_Start, false)
                .apply();
    }

    public static boolean getFirstStart() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(First_Start, true);
    }

    public static void setMode(int mode) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(MODE, mode)
                .apply();
    }

    public static int getMode() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(MODE,1);
    }
    public static void setstartself() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(Start_self, false)
                .apply();
    }

    public static boolean getstartself() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Start_self, true);
    }

    public static byte getsuccessOpenDoor() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        int anInt = sharedPreferences.getInt(Success_Open_Door, 1);
        if (anInt == 255) {
            sharedPreferences.edit()
                    .putInt(Success_Open_Door, 1)
                    .apply();
        } else {
            sharedPreferences.edit()
                    .putInt(Success_Open_Door, anInt + 1)
                    .apply();
        }
        byte bytes =SwitchUtils.IntToByte(anInt);
        LogUtils.a("int:"+anInt+"string"+Integer.toHexString(anInt));
        return bytes;
    }

}
