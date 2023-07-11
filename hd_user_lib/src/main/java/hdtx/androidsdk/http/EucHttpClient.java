package hdtx.androidsdk.http;

import android.text.TextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * HTTP请求工具类
 *
 * @author jay
 * @version 1.0
 * @since 2013.3.27
 */
public class EucHttpClient {

    private static HttpClient httpClient = new DefaultHttpClient();

    enum Mode {
        POST, GET;
    }

    /**
     * 获取响应对象
     *
     * @param request 请求对象
     * @return
     */
    public static HttpResponse getHttpResponse(HttpUriRequest request) {
        try {
            return httpClient.execute(request);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            request.abort();
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            request.abort();
            e.printStackTrace();
        }
        return null;
    }

    /*  *//**
     * HTTP GET 请求
     *
     * @param url
     * @param paraMap
     * @return
     *//*
    public static String httpGet(String url, Map<String, String> paraMap) {
        return execute(url, paraMap, Mode.GET);
    }
*/

    /**
     * HTTP POST 请求
     *
     * @param url
     * @param paraMap
     * @return
     */
    public static String httpPost(String url, JSONObject paraMap) {
        return execute(url, paraMap, Mode.POST);
    }

    /**
     * HTTP请求
     *
     * @param url
     * @param paraMap
     * @param mode
     * @return
     */
    public static String execute(String url, JSONObject paraMap, Mode mode) {
//		System.out.println("当前请求的   url 是---------------" + url);
        // 请求者
        HttpUriRequest request = null;
        // 应答者
        HttpResponse response = null;
        // 配置对象
        //BasicHttpContext context = new BasicHttpContext();
        if (mode == Mode.POST) {// POST 请求
            request = new HttpPost(url);
            StringEntity entity = null;
            try {
                entity = new StringEntity(paraMap.toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (entity != null) {
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
            }
            ((HttpPost) request).setEntity(entity);
            /*List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> key = paraMap.keySet();
            //遍历
            for (Iterator it = key.iterator(); it.hasNext(); ) {
                String s = (String) it.next();
                nvps.add(new BasicNameValuePair(s, paraMap.get(s)));
            }
            try {
                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
        }
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
        //httpclient 设置请求超时
        HttpParams params = request.getParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        // 发送请求
        try {
            response = getHttpResponse(request);
        } catch (Exception e) {
            request.abort();
            e.printStackTrace();
        }
        if (response == null || response.getStatusLine().getStatusCode() != 200) {// 通讯失败
            // 终止连接
            request.abort();
            if (response.getStatusLine().getStatusCode() == 502) {
                return "502";
            }
            return null;
        }
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "utf-8");
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            request.abort();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            request.abort();
            e.printStackTrace();
        } catch (IOException e) {
            request.abort();
            e.printStackTrace();
        }
        return null;
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
        for (Iterator iterator = paramMap.keySet().iterator(); iterator.hasNext(); ) {
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
