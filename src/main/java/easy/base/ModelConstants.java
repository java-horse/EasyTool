package easy.base;

/**
 * 开源大模型版本
 *
 * @project: EasyTool
 * @package: easy.base
 * @author: mabin
 * @date: 2023/11/28 17:17:24
 */
public class ModelConstants {

    private ModelConstants() {
    }

    /**
     * 通义千问
     */
    public enum TONG_YI {
        MAX("qwen-max"),
        PLUS("qwen-plus"),
        TURBO("qwen-turbo");

        private final String model;

        public String getModel() {
            return model;
        }

        TONG_YI(String model) {
            this.model = model;
        }
    }

}
