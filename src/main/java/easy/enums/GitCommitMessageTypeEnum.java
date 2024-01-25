package easy.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Git消息类型枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/24 16:57:37
 */
public enum GitCommitMessageTypeEnum {
    FEAT("feat", "A new feature"),
    FIX("fix", "A bug fix"),
    DOCS("docs", "Documentation only changes"),
    STYLE("style", "Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)"),
    REFACTOR("refactor", "A code change that neither fixes a bug nor adds a feature"),
    PERF("perf", "A code change that improves performance"),
    TEST("test", "Adding missing tests or correcting existing tests"),
    BUILD("build", "Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)"),
    CI("ci", "Changes to our CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs)"),
    CHORE("chore", "Other changes that don't modify src or test files"),
    REVERT("revert", "Reverts a previous commit");

    private final String title;
    private final String desc;

    GitCommitMessageTypeEnum(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public static List<String> allTypeDesc() {
        return Arrays.stream(values()).map(item -> item.getTitle() + " - " + item.getDesc()).collect(Collectors.toList());
    }

    public static String getTitle(String desc) {
        if (StringUtils.isBlank(desc)) {
            return null;
        }
        for (GitCommitMessageTypeEnum typeEnum : values()) {
            if (StringUtils.equals(typeEnum.getDesc(), desc)) {
                return typeEnum.getTitle();
            }
        }
        return null;
    }

    public static String getDesc(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        for (GitCommitMessageTypeEnum typeEnum : values()) {
            if (StringUtils.equals(typeEnum.getTitle(), title)) {
                return typeEnum.getTitle() + " - " + typeEnum.getDesc();
            }
        }
        return null;
    }

}
