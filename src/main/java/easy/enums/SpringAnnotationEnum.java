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
    REQUEST_PARAM_TEXT("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_HEADER_TEXT("org.springframework.web.bind.annotation.RequestHeader"),
    PATH_VARIABLE_TEXT("org.springframework.web.bind.annotation.PathVariable"),
    REQUEST_BODY_TEXT("org.springframework.web.bind.annotation.RequestBody"),
    CONTROLLER_ANNOTATION("org.springframework.stereotype.Controller"),
    REST_CONTROLLER_ANNOTATION("org.springframework.web.bind.annotation.RestController"),
    FEIGN_CLIENT_ANNOTATION("org.springframework.cloud.openfeign.FeignClient");

    private final String name;

    SpringAnnotationEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
