package easy.api.parse.model;

import java.util.List;

/**
 * 类型解析上下文参数
 */
public class TypeParseContext {

    /**
     * 分组校验
     */
    private List<String> jsr303ValidateGroups;

    public List<String> getJsr303ValidateGroups() {
        return jsr303ValidateGroups;
    }

    public void setJsr303ValidateGroups(List<String> jsr303ValidateGroups) {
        this.jsr303ValidateGroups = jsr303ValidateGroups;
    }
}
