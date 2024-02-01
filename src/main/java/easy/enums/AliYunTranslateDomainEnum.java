package easy.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 阿里云专业翻译领域枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/27 10:26:24
 */
public enum AliYunTranslateDomainEnum {

    GENERAL("general", "通用"),
    TITLE("title", "商品标题"),
    DESCRIPTION("description", "商品描述"),
    COMMUNICATION("communication", "商品沟通"),
    MEDICAL("medical", "医疗"),
    SOCIAL("social", "社交"),
    FINANCE("finance", "金融");

    private final String domain;
    private final String name;

    AliYunTranslateDomainEnum(String domain, String name) {
        this.domain = domain;
        this.name = name;
    }

    public static String getDomain(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (AliYunTranslateDomainEnum aliYunTranslateDomainEnum : values()) {
            if (aliYunTranslateDomainEnum.getName().equals(name)) {
                return aliYunTranslateDomainEnum.getDomain();
            }
        }
        return null;
    }

    public String getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }

}
