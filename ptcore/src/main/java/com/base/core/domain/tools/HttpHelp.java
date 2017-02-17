package com.base.core.domain.tools;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * date:13-12-11 editor:YangHongJian
 * <li>创建文档<li>
 * <li>HttpClient封装提供了同步和异步方法，并且支持http的Get和Post方法<li>
 * date:14.4.29 editor:YangHongJian
 * <li>添加Http请求的Head参数</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 3.0
 * @see HttpCallback
 * @since 1.6
 */
public final class HttpHelp {
    private static Logger log = Logger.getLogger(HttpHelp.class);
    private static HttpHelp httpHelp;
    private static HttpClient httpClient;

    /**
     * 定义请求类型
     */
    public static enum HttpMethod {
        GET, POST
    }

    private HttpHelp() {
    }

    /**
     * 获取HttpHelp单例实例
     *
     * @return 返回单例实例
     */
    public static synchronized HttpHelp getInstance() {
        if (null == httpHelp)
            httpHelp = new HttpHelp();
        return httpHelp;
    }

    /**
     * 异步请求(不带参数)
     *
     * @param url      请求地址
     * @param method   请求类型
     * @param callback 回调类，用来处理http请求完毕后的逻辑
     * @see HttpCallback
     */
    public void asyncRequest(final String url, final HttpMethod method, final HttpCallback callback) {
        asyncRequest(url, method, null, callback);
    }

    /**
     * 同步请求(不带参数)
     *
     * @param url      请求地址
     * @param method   请求类型
     * @param callback 回调类，用来处理http请求完毕后的逻辑
     * @see HttpCallback
     */
    public void syncRequest(final String url, final HttpMethod method, final HttpCallback callback) {
        syncRequest(url, method, null, callback);
    }


    /**
     * 异步请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param method   请求类型
     * @param callback 回调类，用来处理http请求完毕后的逻辑
     * @see HttpCallback
     */
    public void asyncRequest(final String url, final HttpMethod method, final Map<String, String> params,
                             final HttpCallback callback) {
        new Thread((new Runnable() {
            public void run() {
                syncRequest(url, method, params, callback);
            }
        })).start();
    }

    /**
     * 同步请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param method   请求类型
     * @see HttpCallback
     */
    public String syncRequest(final String url, final HttpMethod method, final Map<String, String> params) {
        String retStr = null;
        BufferedReader reader = null;
        try {
            HttpUriRequest request = getRequest(url, method, params);
            String httpHeads = BaseTools.getPropertiesByKey("HTTP_HEAD");
            if (!BaseTools.isEmpty(httpHeads)) {
                String[] heads = httpHeads.split("\\|");
                log.info(String.valueOf(heads.length));
                for (String head : heads) {
                    String[] hd = head.split("#");
                    request.addHeader(hd[0], hd[1]);
                }
            }
//            HttpParams hp = request.getParams();
            HttpResponse response = HttpHelp.getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    sb.append(s);
                }
                retStr = sb.toString();
            }
        } catch (ClientProtocolException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                //
            }
        }
        return retStr;
    }

    /**
     * 同步请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param method   请求类型
     * @param callback 回调类，用来处理http请求完毕后的逻辑
     * @see HttpCallback
     */
    public void syncRequest(final String url, final HttpMethod method, final Map<String, String> params,
                            final HttpCallback callback) {
        String retStr = null;
        BufferedReader reader = null;
        try {
            HttpUriRequest request = getRequest(url, method, params);
            String httpHeads = BaseTools.getPropertiesByKey("HTTP_HEAD");
            if (!BaseTools.isEmpty(httpHeads)) {
                String[] heads = httpHeads.split("\\|");
                log.info(String.valueOf(heads.length));
                for (String head : heads) {
                    String[] hd = head.split("#");
                    request.addHeader(hd[0], hd[1]);
                }
            }
//            HttpParams hp = request.getParams();
            HttpResponse response = HttpHelp.getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    sb.append(s);
                }
                retStr = sb.toString();
            }
        } catch (ClientProtocolException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                //
            }
        }
        callback.execute(retStr);
    }


    /**
     * 支持POST/GET方法请求
     *
     * @param url        请求地址
     * @param params     请求参数
     * @param httpMethod 请求类型
     * @return 返回请求结果
     * @see HttpMethod
     */
    private HttpUriRequest getRequest(String url, HttpMethod httpMethod, Map<String, String> params) {
        if (httpMethod.equals(HttpHelp.HttpMethod.POST)) {
            List<NameValuePair> listParams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (String name : params.keySet()) {
                    listParams.add(new BasicNameValuePair(name, params.get(name)));
                }
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(listParams);
                HttpPost request = new HttpPost(url);
                request.setEntity(entity);

                String httpHeads = BaseTools.getPropertiesByKey("HTTP_HEAD");
                if (!BaseTools.isEmpty(httpHeads)) {
                    String[] heads = httpHeads.split("\\|");
                    log.info(String.valueOf(heads.length));
                    for (String head : heads) {
                        String[] hd = head.split("#");
                        request.addHeader(hd[0], hd[1]);
                    }
                }
                return request;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            if (!url.contains("?")) {
                url += "?";
            }
            if (params != null) {
                for (String name : params.keySet()) {
                    url += "&" + name + "=" + URLEncoder.encode(params.get(name));
                }
            }

            return new HttpGet(url);
        }
    }

    /**
     * 在从连接池获取HttpClient单例实例
     *
     * @return 返回单例实例
     */
    public static synchronized HttpClient getHttpClient() {
        if (null == httpClient) {
            String charset = BaseTools.getPropertiesByKey("PARAMS_CHARSET");
            if (BaseTools.isEmpty(charset))
                charset = "UTF-8";

            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, charset);
            HttpProtocolParams.setUseExpectContinue(params, true);
            String userAgent = BaseTools.getPropertiesByKey("USER_AGENT");
            if (!BaseTools.isEmpty(userAgent))
                HttpProtocolParams.setUserAgent(params, userAgent);
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            String connPoolTimeout = BaseTools.getPropertiesByKey("CONN_POOL_TIMEOUT");
            if (BaseTools.isEmpty(connPoolTimeout))
                connPoolTimeout = "1000";
            ConnManagerParams.setTimeout(params, Long.parseLong(connPoolTimeout));
            /* 连接超时 */
            String connTimeout = BaseTools.getPropertiesByKey("CONN_TIMEOUT");
            if (BaseTools.isEmpty(connTimeout))
                connTimeout = "2000";
            HttpConnectionParams.setConnectionTimeout(params, Integer.parseInt(connTimeout));
            /* 请求超时 */
            String connSoTimeout = BaseTools.getPropertiesByKey("CONN_SOTEMOUT");
            if (BaseTools.isEmpty(connSoTimeout))
                connSoTimeout = "4000";
            HttpConnectionParams.setSoTimeout(params, Integer.parseInt(connSoTimeout));

            // 设置HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            String httpPort = BaseTools.getPropertiesByKey("HTTP_PORT");
            if (BaseTools.isEmpty(httpPort))
                httpPort = "80";
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), Integer.parseInt(httpPort)));
            String httpsPort = BaseTools.getPropertiesByKey("HTTPS_PORT");
            if (BaseTools.isEmpty(httpsPort))
                httpsPort = "443";
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), Integer.parseInt(httpsPort)));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            httpClient = new DefaultHttpClient(conMgr, params);
        }
        return httpClient;
    }

    /**
     * 向服务端的url发出同步Get请求，并返回json字符串
     *
     * @param url      请求连接
     * @param mapParam 请求参数
     * @param callback 回调对象
     */
    public static void doGet(String url, Map<String, String> mapParam, final HttpCallback callback) {
        HttpHelp.getInstance().asyncRequest(url, HttpMethod.GET, mapParam, callback);
    }

    /**
     * 向服务端的url发出同步POST请求，并返回json字符串
     *
     * @param url      请求连接
     * @param mapParam 请求参数
     * @param callback 回调方法对象
     */
    public static void doPost(String url, Map<String, String> mapParam, HttpCallback callback) {
        HttpHelp.getInstance().asyncRequest(url, HttpMethod.POST, mapParam, callback);
    }
}
