package easy.util;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Http客户端处理
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/04 11:23
 **/

public class HttpUtil {

    private static final Logger log = Logger.getInstance(HttpUtil.class);

    private HttpUtil() {

    }

    /**
     * 设置连接超时时间，单位毫秒
     */
    private static final int CONNECT_TIMEOUT = 2000;
    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒
     */
    private static final int SOCKET_TIMEOUT = 2000;

    /**
     * 发送get请求；不带请求头和请求参数
     *
     * @param url 请求地址
     */
    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    /**
     * 发送get请求；带请求参数
     *
     * @param url    请求地址
     * @param params 请求参数集合
     */
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, null, params);
    }

    /**
     * 发送get请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build();
            httpGet.setConfig(requestConfig);
            packageHeader(headers, httpGet);
            return getResult(httpClient, httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(httpClient);
        }
        return null;
    }

    /**
     * 发送post请求；不带请求头和请求参数
     *
     * @param url 请求地址
     */
    public static String doPost(String url) {
        return doPost(url, null, null, Boolean.FALSE);
    }

    /**
     * 发送post请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     */
    public static String doPost(String url, Map<String, String> params, Boolean isJson) {
        return doPost(url, null, params, isJson);
    }

    /**
     * 发送post请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     */
    public static String doPost(String url, Map<String, String> headers, Map<String, String> params, Boolean isJson) {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPost.setConfig(requestConfig);
            if (isJson) {
                headers.put("Content-Type", "application/json;charset=utf-8");
            }
            packageHeader(headers, httpPost);
            packageParam(params, httpPost);
            return getResult(httpClient, httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(httpClient);
        }
        return null;
    }

    /**
     * 封装请求头
     */
    public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 封装请求参数
     */
    public static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod) {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
        }
    }

    /**
     * 获得响应结果
     */
    public static String getResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) {
        String content = "";
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpMethod)) {
            if (httpResponse != null && httpResponse.getStatusLine() != null) {
                if (httpResponse.getEntity() != null) {
                    content = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                }
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 释放资源
     */
    public static void close(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
