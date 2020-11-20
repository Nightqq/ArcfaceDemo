package com.arcsoft.arcfacedemo.util.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.arcsoft.arcfacedemo.util.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageBase64Utils {
    //转换为base64字符串
    public static String getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LogUtils.a("图片尺寸" + bitmap.getWidth(), bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = out.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);

        return encodeString;
    }


    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            if (bitmap==null){
                LogUtils.a("base64转换图片空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
