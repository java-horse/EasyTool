package easy.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 有道智云AI翻译领域枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/27 14:45:11
 */
public enum YouDaoTranslateDomainEnum {

    GENERAL("general", "通用领域"),
    COMPUTERS("computers", "计算机领域"),
    MEDICINE("medicine", "医学领域"),
    FINANCE("finance", "金融经济领域"),
    GAME("game", "游戏领域");

    private final String domain;
    private final String name;

    public static String getDomain(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (YouDaoTranslateDomainEnum youDaoTranslateDomainEnum : values()) {
            if (youDaoTranslateDomainEnum.getName().equals(name)) {
                return youDaoTranslateDomainEnum.getDomain();
            }
        }
        return null;
    }

    YouDaoTranslateDomainEnum(String domain, String name) {
        this.domain = domain;
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }
}
