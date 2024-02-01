package easy.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 百度翻译领域枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/26 17:41:20
 */
public enum BaiDuTranslateDomainEnum {

    IT("it", "信息技术领域"),
    FINANCE("finance", "金融财经领域"),
    MACHINERY("machinery", "机械制造领域"),
    SENIMED("senimed", "生物Medical领域"),
    NOVEL("novel", "网络文学领域"),
    ACADEMIC("academic", "学术论文领域"),
    AEROSPACE("aerospace", "航空航天领域"),
    WIKI("wiki", "人文社科领域"),
    NEWS("news", "新闻资讯领域"),
    LAW("law", "法律法规领域"),
    CONTRACT("contract", "合同领域");

    private final String domain;
    private final String name;

    public static String getDomain(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (BaiDuTranslateDomainEnum baiDuTranslateDomainEnum : values()) {
            if (baiDuTranslateDomainEnum.getName().equals(name)) {
                return baiDuTranslateDomainEnum.getDomain();
            }
        }
        return null;
    }

    BaiDuTranslateDomainEnum(String domain, String name) {
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
