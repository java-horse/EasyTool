package easy.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Request请求注解
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/11/07 11:30:39
 */
public enum RequestAnnotationEnum {

    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", ""),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", "PUT"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", "PATCH"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    FEIGN_CLIENT("org.springframework.cloud.openfeign.FeignClient", "FEIGN");

    private final String qualifiedName;
    private final String methodName;

    RequestAnnotationEnum(String qualifiedName, String methodName) {
        this.qualifiedName = qualifiedName;
        this.methodName = methodName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getMethodName() {
        return methodName;
    }

    public static RequestAnnotationEnum getEnumByQualifiedName(String qualifiedName) {
        if (StringUtils.isBlank(qualifiedName)) {
            return null;
        }
        for (RequestAnnotationEnum anEnum : values()) {
            if (StringUtils.equals(anEnum.getQualifiedName(), qualifiedName)) {
                return anEnum;
            }
        }
        return null;
    }

}
