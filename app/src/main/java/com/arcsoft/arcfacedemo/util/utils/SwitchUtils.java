package com.arcsoft.arcfacedemo.util.utils;

import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchUtils {
    private static final String SEP1 = "#";
    private static final String SEP2 = "|";
    private static final String SEP3 = "=";

    /**
     * List转换String
     *
     * @param list :需要转换的List
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                // 如果值是list类型则调用自己
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i)));
                    sb.append(SEP1);
                } else if (list.get(i) instanceof Map) {
                    sb.append(MapToString((Map<?, ?>) list.get(i)));
                    sb.append(SEP1);
                } else {
                    sb.append(list.get(i));
                    sb.append(SEP1);
                }
            }
        }
        return "L" + sb.toString();
    }

    /**
     * Map转换String
     *
     * @param map :需要转换的Map
     * @return String转换后的字符串
     */
    public static String MapToString(Map<?, ?> map) {
        StringBuffer sb = new StringBuffer();
        // 遍历map
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object key = obj;
            Object value = map.get(key);
            if (value instanceof List<?>) {
                sb.append(key.toString() + SEP1 + ListToString((List<?>) value));
                sb.append(SEP2);
            } else if (value instanceof Map<?, ?>) {
                sb.append(key.toString() + SEP1
                        + MapToString((Map<?, ?>) value));
                sb.append(SEP2);
            } else {
                sb.append(key.toString() + SEP3 + value.toString());
                sb.append(SEP2);
            }
        }
        return "M" + sb.toString();
    }

    /**
     * String转换Map
     *
     * @param mapText :需要转换的字符串
     *                :字符串中的分隔符每一个key与value中的分割
     *                <p>
     *                :字符串中每个元素的分割
     * @return Map<?   ,   ?>
     */
    public static Map<String, Object> StringToMap(String mapText) {

        if (mapText == null || mapText.equals("")) {
            return null;
        }
        mapText = mapText.substring(1);

        mapText = mapText;

        Map<String, Object> map = new HashMap<String, Object>();
        String[] text = mapText.split("\\" + SEP2); // 转换为数组
        for (String str : text) {
            String[] keyText = str.split(SEP3); // 转换key与value的数组
            if (keyText.length < 1) {
                continue;
            }
            String key = keyText[0]; // key
            String value = keyText[1]; // value
            if (value.charAt(0) == 'M') {
                Map<?, ?> map1 = StringToMap(value);
                map.put(key, map1);
            } else if (value.charAt(0) == 'L') {
                List<?> list = StringToList(value);
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * String转换List
     *
     * @param listText :需要转换的文本
     * @return List<?>
     */
    public static List<Object> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.substring(1);

        listText = listText;

        List<Object> list = new ArrayList<Object>();
        String[] text = listText.split(SEP1);
        for (String str : text) {
            if (str.charAt(0) == 'M') {
                Map<?, ?> map = StringToMap(str);
                list.add(map);
            } else if (str.charAt(0) == 'L') {
                List<?> lists = StringToList(str);
                list.add(lists);
            } else {
                list.add(str);
            }
        }
        return list;
    }


    /**
     * @return 将十六进制字符串转换为字节数组
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }


    //int0~255转byte
    public static byte IntToByte(int num) {
        byte b = (byte) (num & 0xff);
        return b;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
    /**
     * @return 将字节数组转换为十六进制字符串
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    //单个元素转换为字符串
    public static String byte2HexStr1(byte b) {
        String stmp = Integer.toHexString(b & 0xFF);
        return stmp.toUpperCase().trim();
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    //16进制字符串转10进制字符串
    public static String string2Hexstr(String s) {
        s = s.replaceAll(" ", "");
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < s.length() - 1; i += 2) {
            int p = Integer.parseInt(s.substring(i, i + 2), 16);
            stringBuffer.append(p);
        }
        return stringBuffer.toString();
    }

    //异或运算
    public static byte[] xor(byte[] old) {
        byte temp = 0;
        for (int i = 0; i < old.length-1; i++) {
            temp ^= old[i];
        }
        old[old.length - 1] = temp;
        return old;
    }


    public static byte[] base64tobyte(String string){
        byte[] decode = Base64.decode(string, 0);
        return decode;
    }
}
