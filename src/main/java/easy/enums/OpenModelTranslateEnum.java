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

    TONG_YI("通义千问", "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
            "{\"model\":\"%s\",\"input\":{\"messages\":[{\"role\":\"system\",\"content\":\"将反引号中的%s翻译成%s，并输出到一对反引号中，如`cat`->`猫`\"},{\"role\":\"user\",\"content\":\"将反引号中的指令翻译成%s:`dog`\"},{\"role\":\"assistant\",\"content\":\"`狗`\"},{\"role\":\"user\",\"content\":\"将反引号中的指令翻译成%s:`%s`\"}]}}"),
    ;

    private final String model;
    private final String url;
    private final String prompt;

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

    OpenModelTranslateEnum(String model, String url, String prompt) {
        this.model = model;
        this.url = url;
        this.prompt = prompt;
    }

    public String getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

    public String getPrompt() {
        return prompt;
    }

}
