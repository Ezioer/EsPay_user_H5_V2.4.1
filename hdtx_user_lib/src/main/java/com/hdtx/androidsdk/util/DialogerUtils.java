package com.hdtx.androidsdk.util;

import com.hdtx.androidsdk.ui.ProgressDialog;

import android.app.Dialog;
import android.content.Context;

public class DialogerUtils {
    private static Dialog dialog = null;

    public DialogerUtils() {
    }

    /**
     * show dialog
     */
    public static void show(Context context, int theme) {
        dismiss(context);
        createDialog(context, theme);
        dialog.show();
    }

    private static void createDialog(Context context, int theme) {
        dialog = new ProgressDialog(context, theme);
    }

    public static void dismiss(Context context) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }

            dialog = null;
        } catch (Exception e) {
        }
    }
}
