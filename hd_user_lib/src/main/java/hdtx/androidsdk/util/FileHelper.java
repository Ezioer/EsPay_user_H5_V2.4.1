package hdtx.androidsdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 工具类，处理I/O操作
 *
 * @author ted
 */
public class FileHelper {

    private FileHelper() {
    }

    /**
     * 从文件中获取字符串
     *
     * @param file
     * @return the file content
     */
    public static String readFile(File file) {
        if (!initFile(file))
            return "";
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return "";

        }
        return inputStream2String(fis);
    }

    /**
     * convert input stream to string by the special encode.
     *
     * @return String
     */
    public static String inputStream2String(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] data = new byte[2048];
        String result = null;
        try {
            while ((len = is.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            result = new String(baos.toByteArray());
        } catch (IOException e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * write the msg to the file
     *
     * @param file
     * @param msg
     */
    public static void writeFile(File file, String msg) {
        if (!initFile(file))
            return;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(msg.getBytes());
        } catch (IOException e) {
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

    }

    /**
     * init file if the file is not exists
     *
     * @param file
     * @return if the file is null || createFile failed return false,else return
     * true
     */
    public static boolean initFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {

                if (dir.mkdirs()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        return false;
                    }
                } else
                    return false;
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

}
