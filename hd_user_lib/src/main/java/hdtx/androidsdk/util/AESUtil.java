package hdtx.androidsdk.util;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 通用 AES加密解密类库
 * (无需修改)
 *
 * @author onnes
 */
public final class AESUtil {
    /**
     * 生成大写、小写字母、数字的随机字符串
     *
     * @return
     */
    public static String getRandomString(String base) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            char c = base.charAt(number);
            if (c <= 'z') {
                // 如果是字母，再生成一个随机数以奇偶来决定是大写还是小写字母
                // 采用位运算效率高一点
                if (1 == (random.nextInt(100) & 1)) {
                    c = Character.toUpperCase(c);
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // 加密
    public static String encrypt(String sSrc, String key) throws Exception {
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
    public static String decrypt(String sSrc, String key) throws Exception {
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
