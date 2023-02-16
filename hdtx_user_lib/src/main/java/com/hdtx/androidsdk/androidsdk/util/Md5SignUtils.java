package com.hdtx.androidsdk.androidsdk.util;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class Md5SignUtils {
    /*签名*/
    public static String sign(Map<String, String> map, String key) {
        String content = getStringForSign(map);
        return sign(content, key);
    }

    /*签名*/
    public static String sign(String content, String key) {
        String veriCode = digest(content + key);
        return veriCode;
    }

    public static String digest(String src) {
        try {
            byte[] btInput = src.getBytes("UTF-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = (md[i]) & 0xff;
                if (val < 16) sb.append("0");
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*获取待签名字符串*/
    public static String getStringForSign(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> treeMap = new TreeMap<String, String>(map);
        if (treeMap != null) {
            for (Map.Entry<String, String> entity : treeMap.entrySet()) {
                if (entity.getKey() != null && entity.getValue() != null) {
                    sb.append(entity.getKey()).append("=").append(String.valueOf(entity.getValue())).append("&");
                }
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
