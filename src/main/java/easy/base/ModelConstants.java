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
     * 阿里巴巴-通义千问
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.ModelConstants
     * @date 2024/03/18 15:29
     */
    public enum TONG_YI {
        MAX("qwen-max"),
        PLUS("qwen-plus"),
        TURBO("qwen-turbo"),
        MAX_1201("qwen-max-1201"),
        MAX_LONG_CONTEXT("qwen-max-longcontext");

        private final String model;

        public String getModel() {
            return model;
        }

        TONG_YI(String model) {
            this.model = model;
        }
    }

    /**
     * 月之暗面-Kimi大模型
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.ModelConstants
     * @date 2024/03/18 15:28
     */
    public enum KIMI {
        MOONSHOT_V1_8K("moonshot-v1-8k"),
        MOONSHOT_V1_32K("moonshot-v1-32k"),
        MOONSHOT_V1_128K("moonshot-v1-128k");
        private final String model;

        public String getModel() {
            return model;
        }

        KIMI(String model) {
            this.model = model;
        }
    }

}
