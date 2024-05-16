package easy.base;

public class ApiDocConstants {

    private ApiDocConstants() {
    }

    /**
     * 默认无字符串
     */
    public static final String DEFAULT_NONE = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

    /**
     * 配置文件相对路径
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.CommonConstants
     * @date 2024/05/12 11:03
     */
    public interface CONFIG_FILE {
        String ROOT_CONFIG = "properties/.EasyToolApiDocConfig";
        String MOCKS = "properties/api/mocks.properties";
        String TYPES = "properties/api/types.properties";
        String TYPES_FORMAT = "properties/api/type2formats.properties";
    }


}
