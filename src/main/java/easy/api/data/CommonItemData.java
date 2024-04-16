package easy.api.data;

import easy.enums.ApiDocContentType;

import java.util.List;

public class CommonItemData {

    /**
     * 接口唯一标识
     */
    private String apiKey;

    /**
     * 接口标题
     */
    private String title;

    /**
     * 接口详情信息
     */
    private String detailInfo;

    /**
     * 接口请求地址
     */
    private List<String> urlList;

    /**
     * 接口请求类型
     */
    private String requestType;

    /**
     * 请求内容类型
     */
    private ApiDocContentType contentType;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public ApiDocContentType getContentType() {
        return contentType;
    }

    public void setContentType(ApiDocContentType contentType) {
        this.contentType = contentType;
    }
}
