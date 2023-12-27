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
import java.util.*;

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
    private static final int CONNECT_TIMEOUT = 10000;

    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒
     */
    private static final int SOCKET_TIMEOUT = 10000;


    public static String doGet(String url) {
        return doGet(url, null, null);
    }


    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, null, params);
    }


    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().build();
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build());
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("HttpUtil.doGet execute exception: " + e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

    public static String doPost(String url, String paramJson) {
        return doPost(url, null, paramJson);
    }

    public static String doPost(String url, Map<String, Object> params) {
        return doPost(url, null, params);
    }

    public static String doPost(String url, Map<String, String> headers, Map<String, Object> params) {
        return doPost(url, headers, params, null, Boolean.FALSE);
    }

    public static String doPost(String url, Map<String, String> headers, String paramJson) {
        return doPost(url, headers, null, paramJson, Boolean.TRUE);
    }

    private static String doPost(String url, Map<String, String> headers, Map<String, Object> params, String paramJson, Boolean isJson) {
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
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    nameValuePairList.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, StandardCharsets.UTF_8));
            } else {
                if (headers == null) {
                    headers = new HashMap<>(3);
                }
                headers.put("Content-Type", "application/json;charset=utf-8");
                httpPost.setEntity(new StringEntity(paramJson, StandardCharsets.UTF_8));
            }
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("HttpUtil.doPost execute exception: " + e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

}
