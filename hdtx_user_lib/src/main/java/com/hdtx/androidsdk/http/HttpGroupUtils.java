package com.hdtx.androidsdk.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class HttpGroupUtils {


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String sendGet(String url, String param, String token) {
        String result = "";
        BufferedReader in = null;
        URL realUrl = null;
        try {
            String urlName = url;

            if (param != null && !"".equals(param)) {
                urlName = url + "?" + param;
            }
            realUrl = new URL(urlName);

            try {
                // 打开和URL之间的连接
                HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
                if (realUrl.getProtocol().toLowerCase().equals("https")) {
                    trustAllHosts();
                    HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    conn = https;
                }
                // 设置通用的请求属性
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
                conn.setRequestProperty("Cookie", "TGC=" + token);
                // 建立实际的连接
                conn.connect();
                // 获取所有响应头字段
                Map<String, List<String>> map = conn.getHeaderFields();
                // 遍历所有的响应头字段
                for (String key : map.keySet()) {
                    Log.d("EsPayNetGetPost", key + "--->" + map.get(key));
                }
                // 定义BufferedReader输入流来读取URL的响应
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
//				Log.i("testttttttt", "请求地址：" + urlName);
//				Log.i("testttttttt", "返回结果：" + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("EsPayNetGetPost", "发送GET请求出现异常！" + e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定URL发送POST方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String sendPost(String url, String param, String token) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            }
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Cookie", "TGC=" + token);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; text/html; charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            int i = 0;
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成URL
     *
     * @param url
     * @param
     * @return
     */
    protected static String buildURL(String url, Map paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return url;
        }
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = paramMap.keySet().iterator(); iterator
                .hasNext(); ) {
            String key = (String) iterator.next();
            sb.append(key).append("=").append(paramMap.get(key));
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        String paraStr = sb.toString();
        StringBuffer buffer = new StringBuffer();

        buffer.append(url);
        if (url.indexOf("?") != -1) {
            if (url.indexOf("?") == url.length() - 1) {
                buffer.append(paraStr);
            } else {
                buffer.append("&").append(paraStr);
            }
        } else {
            buffer.append("?").append(paraStr);
        }
        return buffer.toString();
    }
}
