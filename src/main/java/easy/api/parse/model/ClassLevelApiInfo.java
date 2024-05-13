package easy.api.parse.model;

/**
 * 类级别上的Api信息
 */
public class ClassLevelApiInfo {

    /**
     * 路径
     */
    private String path;

    /**
     * 分类
     */
    private String category;

    /**
     * 声明的分类
     */
    private String declareCategory;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeclareCategory() {
        return declareCategory;
    }

    public void setDeclareCategory(String declareCategory) {
        this.declareCategory = declareCategory;
    }
}
