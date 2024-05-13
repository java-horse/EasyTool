package easy.api.model.common;

import com.google.common.collect.Sets;

import java.util.Set;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS;

    public static HttpMethod of(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }

    /**
     * 请求方法是否允许消息体
     *
     * @return boolean
     * @author mabin
     * @date 2024/05/11 14:26
     */
    public boolean isAllowBody() {
        Set<HttpMethod> sets = Sets.newHashSet(POST, PUT, DELETE, PATCH);
        return sets.contains(this);
    }
}
