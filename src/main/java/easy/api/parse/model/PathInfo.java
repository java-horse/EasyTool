package easy.api.parse.model;


import easy.api.model.common.HttpMethod;

import java.util.List;

/**
 * 请求路径和方法信息
 */
public class PathInfo {

    private HttpMethod method;

    private List<String> paths;

    public String getPath() {
        return paths != null && paths.size() > 0 ? paths.get(0) : null;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
