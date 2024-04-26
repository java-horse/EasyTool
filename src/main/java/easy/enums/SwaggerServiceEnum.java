package easy.enums;

import easy.icons.EasyIcons;
import easy.swagger.Swagger2GenerateServiceImpl;
import easy.swagger.Swagger3GenerateServiceImpl;
import easy.swagger.SwaggerGenerateService;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Objects;

public enum SwaggerServiceEnum {

    SWAGGER_2("Swagger2.x", new Swagger2GenerateServiceImpl(), EasyIcons.ICON.SWAGGER),
    SWAGGER_3("Swagger3.x", new Swagger3GenerateServiceImpl(), EasyIcons.ICON.SWAGGER);

    private final String name;
    private final SwaggerGenerateService swaggerGenerateService;
    private final Icon icon;

    SwaggerServiceEnum(String name, SwaggerGenerateService swaggerGenerateService, Icon icon) {
        this.name = name;
        this.swaggerGenerateService = swaggerGenerateService;
        this.icon = icon;
    }

    /**
     * 获取Swagger服务实现
     *
     * @param name 类型
     * @return {@link easy.swagger.SwaggerGenerateService}
     * @author mabin
     * @date 2024/04/22 14:49
     */
    public static SwaggerGenerateService getSwaggerGenerateService(String name) {
        SwaggerServiceEnum swaggerAnnotationEnum = getSwaggerAnnotationEnum(name);
        return Objects.isNull(swaggerAnnotationEnum) ? null : swaggerAnnotationEnum.getSwaggerGenerateService();
    }

    /**
     * 获取swagger注释枚举
     *
     * @param name 名称
     * @return {@link easy.enums.SwaggerServiceEnum}
     * @author mabin
     * @date 2024/04/26 09:50
     */
    public static SwaggerServiceEnum getSwaggerAnnotationEnum(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (SwaggerServiceEnum swaggerServiceEnum : values()) {
            if (swaggerServiceEnum.getName().equals(name)) {
                return swaggerServiceEnum;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public SwaggerGenerateService getSwaggerGenerateService() {
        return swaggerGenerateService;
    }

    public Icon getIcon() {
        return icon;
    }

}
