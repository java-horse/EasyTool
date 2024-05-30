package easy.enums;

/**
 * Spring注解的枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/20 10:23:53
 */
public enum SpringAnnotationEnum {
    REQUEST_PARAM_TEXT("org.springframework.web.bind.annotation.RequestParam", "query", "ParameterIn.QUERY"),
    REQUEST_HEADER_TEXT("org.springframework.web.bind.annotation.RequestHeader", "header", "ParameterIn.HEADER"),
    REQUEST_ATTRIBUTE("org.springframework.web.bind.annotation.RequestAttribute", "", ""),
    COOKIE_VALUE("org.springframework.web.bind.annotation.CookieValue", "cookie", "ParameterIn.COOKIE"),
    PATH_VARIABLE_TEXT("org.springframework.web.bind.annotation.PathVariable", "path", "ParameterIn.PATH"),
    REQUEST_BODY_TEXT("org.springframework.web.bind.annotation.RequestBody", "body", ""),
    CONTROLLER_ANNOTATION("org.springframework.stereotype.Controller", "", ""),
    SERVICE("org.springframework.stereotype.Service", "", ""),
    COMPONENT("org.springframework.stereotype.Component", "", ""),
    REPOSITORY("org.springframework.stereotype.Repository", "", ""),
    SPRING_BOOT_APPLICATION("org.springframework.boot.autoconfigure.SpringBootApplication", "", ""),
    SPRING_BOOT_TEST("org.springframework.boot.test.context.SpringBootTest", "", ""),
    CONFIGURATION("org.springframework.context.annotation.Configuration", "", ""),
    REST_CONTROLLER_ANNOTATION("org.springframework.web.bind.annotation.RestController", "", ""),
    FEIGN_CLIENT_ANNOTATION("org.springframework.cloud.openfeign.FeignClient", "", "");

    private final String name;
    private final String paramType;
    private final String paramIn;

    SpringAnnotationEnum(String name, String paramType, String paramIn) {
        this.name = name;
        this.paramType = paramType;
        this.paramIn = paramIn;
    }

    public String getName() {
        return name;
    }

    public String getParamType() {
        return paramType;
    }

    public String getParamIn() {
        return paramIn;
    }

    public static SpringAnnotationEnum getEnum(String name) {
        for (SpringAnnotationEnum springAnnotationEnum : values()) {
            if (springAnnotationEnum.name.equals(name)) {
                return springAnnotationEnum;
            }
        }
        return null;
    }

}
