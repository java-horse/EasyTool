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

    /**
     * 文心一言大模型
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.ModelConstants
     * @date 2024/03/19 14:52
     */
    public enum WEN_XIN {
        ERNIE_3_5_8K("ERNIE-3.5-8K", "completions"),
        ERNIE_4_8K("ERNIE-4.0-8K", "completions_pro");

        private final String model;
        private final String value;

        public static String getRealModel(String model) {
            if (model == null || model.isBlank()) {
                return null;
            }
            for (WEN_XIN anEnum : values()) {
                if (anEnum.getModel().equals(model)) {
                    return anEnum.value;
                }
            }
            return null;
        }

        public String getModel() {
            return model;
        }

        public String getValue() {
            return value;
        }

        WEN_XIN(String model, String value) {
            this.model = model;
            this.value = value;
        }
    }

}
