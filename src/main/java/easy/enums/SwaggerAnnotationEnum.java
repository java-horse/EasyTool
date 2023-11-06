package easy.enums;

/**
 * Swagger注解枚举处理
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/11/06 11:43:12
 */
public enum SwaggerAnnotationEnum {

    API("Api", "io.swagger.annotations.Api"),
    API_MODEL("ApiModel", "io.swagger.annotations.ApiModel"),
    API_OPERATION("ApiOperation","io.swagger.annotations.ApiOperation"),
    API_IMPLICIT_PARAM("ApiImplicitParam","io.swagger.annotations.ApiImplicitParam"),
    API_IMPLICIT_PARAMS("ApiImplicitParams","io.swagger.annotations.ApiImplicitParams"),
    API_MODEL_PROPERTY("ApiModelProperty", "io.swagger.annotations.ApiModelProperty");


    private final String className;
    private final String classPackage;

    public String getClassName() {
        return className;
    }

    public String getClassPackage() {
        return classPackage;
    }

    SwaggerAnnotationEnum(String className, String classPackage) {
        this.className = className;
        this.classPackage = classPackage;
    }

}


