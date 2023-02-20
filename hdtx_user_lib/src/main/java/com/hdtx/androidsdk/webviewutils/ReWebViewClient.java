package com.hdtx.androidsdk.webviewutils;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nina on 3/3 0003.
 */

public class ReWebViewClient extends WebViewClient {


    @Override

    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        super.onPageStarted(view, url, favicon);

    }


    @Override

    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);

    }

}

