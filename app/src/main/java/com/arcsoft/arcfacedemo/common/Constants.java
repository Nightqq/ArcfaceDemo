package com.arcsoft.arcfacedemo.common;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.arcsoft.arcfacedemo.util.utils.Utils;

public class Constants {

    //public static final String APP_ID = "4BQav3wGWbhKc57wxVxCQ3oqdwARnZ2CZ8h1HNBCTS27";
    //public static final String SDK_KEY = "ApBKuqaK39REeGXtv27Y9fz4dYQ3pW8CLivMY15pN5eY";
    public static final String APP_ID = "7rWVETw6jZFJskzGeYpBk9TremfJk4rbubS7bSHNmPoN";
    public static final String SDK_KEY = "6AMVto1xKbDFT4WadUtnq9Czh8jMYxrQjmsdrC5C6XqL";
    // public static final String ACTIVE_KEY = "0857-1118-JBWF-33ZZ";

    public static final String ACTIVE_KEY() {
        if (getSerialNumber().equals("PADX202007010213")) {
            return "0857-1124-N9J2-KVCV";
        } else if (getSerialNumber().equals("PAD201912040749")) {
            return "0857-1118-JBWF-33ZZ";
        } else if (getSerialNumber().equals("PAD202003040699")) {
            return "0857-1118-JKZX-T24L";
        }else if (getSerialNumber().equals("PADX202009170006")){
            return "0857-1124-N9KG-VRJP";
        } /*else if (getSerialNumber().substring(getSerialNumber().length() - 4, getSerialNumber().length()).equals("0007")) {
            return "0857-1124-N9J2-KVCV";
        }*/ else if (getSerialNumber().equals("PADX202010140014")) {
            return "0857-1124-N7T2-J3A6";
        }else if (getSerialNumber().equals("PADX202010140015")) {
            return "0857-1124-N7VG-L6EY";
        }else if (getSerialNumber().equals("PADX202010140016")) {
            return "0857-1124-N7WL-T8XV";
        }else if (getSerialNumber().equals("PADX202010140013")) {
            return "0857-1124-N7U7-9T7P";
        } else if (getSerialNumber().equals("PADX202010140031")) {
            return "0857-1124-N7KN-5XLB";
        } else if (getSerialNumber().equals("PADX202010140032")) {
            return "0857-1124-N7HJ-FG2F";
        } else if (getSerialNumber().equals("PADX202010140033")) {
            return "0857-1124-N7J9-31V6";
        } else if (getSerialNumber().equals("PADX202010140029")) {
            return "0857-1124-N7LY-WHLZ";
        } else {
            return "0000000000000000000";
        }
    }

    public static String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            try {
                return Build.getSerial();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Build.SERIAL;
    }


    /**
     * IR预览数据相对于RGB预览数据的横向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
     */
    public static final int HORIZONTAL_OFFSET = 0;
    /**
     * IR预览数据相对于RGB预览数据的纵向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
     */
    public static final int VERTICAL_OFFSET = 0;
}

