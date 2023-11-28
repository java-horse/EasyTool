package easy.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 开源大模型翻译服务枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/11/28 09:55:45
 */
public enum OpenModelTranslateEnum {

    TONG_YI("通义千问", "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"),
    WEN_XIN("文心一言", "xxx"),
    ;

    private final String model;
    private final String url;

    /**
     * 获取全部开源大模型名称
     *
     * @param
     * @return java.util.Set<java.lang.String>
     * @author mabin
     * @date 2023/11/28 9:59
     */
    public static Set<String> getModelTranslator() {
        return Arrays.stream(values()).map(OpenModelTranslateEnum::getModel).collect(Collectors.toSet());
    }

    OpenModelTranslateEnum(String model, String url) {
        this.model = model;
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

}
