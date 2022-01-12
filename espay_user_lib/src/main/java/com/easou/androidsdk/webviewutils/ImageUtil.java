package com.easou.androidsdk.webviewutils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.easou.androidsdk.util.ESdkLog;
import com.easou.espay_user_lib.BuildConfig;

import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by nina on 3/3 0003.
 */

public class ImageUtil {


    private static final String TAG = "ImageUtil";

    public static Intent choosePicture() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");

        return Intent.createChooser(intent, "Choose image");

    }


    /**
     * 拍照后返回
     */

    public static Intent takeBigPicture(Activity activity) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        intent.putExtra(MediaStore.EXTRA_OUTPUT, newPictureUri(getNewPhotoPath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, newPictureUri(activity));

        return intent;

    }


    public static String getDirPath() {

        return Environment.getExternalStorageDirectory().getPath() + "/WebViewUploadImage";

    }


    public static String getNewPhotoPath(Activity activity) {
        if (Build.VERSION.SDK_INT < 29) {
            Log.d("takephoto", "<29");
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "WebViewUploadImage").getPath();
        } else {
            Log.d("takephoto", "path--->" + activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".jpg");
            return activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".jpg";
        }

    }


    public static String retrievePath(Context context, Intent sourceIntent, Intent dataIntent) {
        String picPath = null;
        try {
            Uri uri;
            if (dataIntent != null) {
                uri = dataIntent.getData();
                if (uri != null) {
                    picPath = ContentUtil.getPath(context, uri);
                }

                if (isFileExists(picPath)) {
                    return picPath;
                }
            }
            if (sourceIntent != null) {
                uri = sourceIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                if (uri != null) {
                    String scheme = uri.getScheme();
                    if (scheme != null && scheme.startsWith("file")) {
                        picPath = uri.getPath();
                    }
                }
                if (!TextUtils.isEmpty(picPath)) {
                    File file = new File(picPath);
                    if (!file.exists() || !file.isFile()) {
                    }
                }
            }
            return picPath;
        } finally {

        }
    }

    private static Uri newPictureUri(Activity activity) {

        if (Build.VERSION.SDK_INT >= 29) {
            return getUriAboveAndroidQ(activity);
        } else {
            return getUriBelowAndroidQ(activity);
        }
//        return Uri.fromFile(new File(path));

    }

    private static Uri getUriBelowAndroidQ(Activity activity) {
        File file = new File(getNewPhotoPath(activity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getUriForFile24(activity, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    private static Uri getUriAboveAndroidQ(Activity activity) {
        ContentValues contentValues = new ContentValues();
        String filename = getNewPhotoPath(activity);
        contentValues.put(MediaStore.Images.Media.DATA, filename);

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);

        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    public static Uri getUriForFile(Activity activity, File file) {

        Uri fileUri = null;
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                Log.d("takephoto", ">29");
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                contentValues.put("relative_path", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator);

                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());

                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = getUriForFile24(activity, file);
            } else {
                fileUri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        return fileUri;
    }


    private static boolean isFileExists(String path) {

        if (TextUtils.isEmpty(path)) {

            return false;

        }

        File f = new File(path);

        if (!f.exists()) {

            return false;

        }

        return true;

    }

}