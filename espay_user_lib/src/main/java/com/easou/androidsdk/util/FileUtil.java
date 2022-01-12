package com.easou.androidsdk.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class FileUtil {
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

//生成文件

    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

//生成文件夹

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }


    public static Intent startPhotoZoom(Uri uri, Uri mImagePath, int size) {

        return startPhotoZoom(uri, mImagePath, size, size);
    }

    public static Intent startPhotoZoom(Uri uri, String mImagePath, int size) {

        return startPhotoZoom(uri, Uri.fromFile(new File(mImagePath)), size, size);
    }


    public static Intent startPhotoZoom(Uri uri, Uri mImagePath, int sizeX, int sizeY) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            //添加这一句表示对目标应用临时授权该Uri所代表的文件

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImagePath);

            intent.setDataAndType(uri, "image/*");

            intent.putExtra("crop", "true");

//        intent.putExtra("circleCrop", "true");

            intent.putExtra("aspectX", 9998);//2019/5/8 修复华为手机默认为圆角裁剪的问题

            intent.putExtra("aspectY", 9999);//

            intent.putExtra("outputX", sizeX);

            intent.putExtra("outputY", sizeY);

            intent.putExtra("scale", true);

            intent.putExtra("scaleUpIfNeeded", true);

            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            intent.putExtra("return-data", false);
        }
        return intent;
    }

}
