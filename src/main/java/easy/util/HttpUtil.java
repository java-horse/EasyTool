package easy.util;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().build();
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build());
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("HttpUtil.doGet execute exception!", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
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
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build());
            if (Objects.isNull(isJson) || Boolean.FALSE.equals(isJson)) {
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, StandardCharsets.UTF_8));
            } else {
                headers.put("Content-Type", "application/json;charset=utf-8");
                httpPost.setEntity(new StringEntity(JsonUtil.toJson(params), StandardCharsets.UTF_8));
            }
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("HttpUtil.doPost execute exception!", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

}
