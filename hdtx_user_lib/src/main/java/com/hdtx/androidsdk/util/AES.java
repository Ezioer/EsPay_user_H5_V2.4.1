package com.hdtx.androidsdk.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 通用 AES加密解密类库
 * (无需修改)
 *
 * @author onnes
 */
public final class AES {

    private static String key = "ezGW6SrVAFezVftc";

    // 加密
    public static String encrypt(String sSrc) throws Exception {
        try {
            // 判断Key是否为16位
            if (key.length() != 16) {
                // System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
            IvParameterSpec iv = new IvParameterSpec("L+\\~f4,Ir)b$=pkf"
                    .getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());

            return Base64.encode(encrypted);// 此处使用BASE64做转码功能
        } catch (Exception ex) {

            return null;
        }
    }

    // 解密
    public static String decrypt(String sSrc) throws Exception {
        try {
            // 判断Key是否为16位
            if (key.length() != 16) {
                //System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("L+\\~f4,Ir)b$=pkf"
                    .getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc);// 先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);

                return originalString;
            } catch (Exception e) {

                return null;
            }
        } catch (Exception ex) {

            return null;
        }
    }

}
