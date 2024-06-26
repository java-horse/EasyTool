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
    API_IGNORE("ApiIgnore", "springfox.documentation.annotations.ApiIgnore"),
    API_MODEL("ApiModel", "io.swagger.annotations.ApiModel"),
    API_OPERATION("ApiOperation", "io.swagger.annotations.ApiOperation"),
    API_PARAM("ApiParam", "io.swagger.annotations.ApiParam"),
    API_IMPLICIT_PARAM("ApiImplicitParam", "io.swagger.annotations.ApiImplicitParam"),
    API_IMPLICIT_PARAMS("ApiImplicitParams", "io.swagger.annotations.ApiImplicitParams"),
    API_MODEL_PROPERTY("ApiModelProperty", "io.swagger.annotations.ApiModelProperty"),
    TAG("Tag", "io.swagger.v3.oas.annotations.tags.Tag"),
    OPERATION("Operation", "io.swagger.v3.oas.annotations.Operation"),
    SCHEMA("Schema", "io.swagger.v3.oas.annotations.media.Schema"),
    REQUEST_BODY("RequestBody", "io.swagger.v3.oas.annotations.parameters.RequestBody"),
    PARAMETERS("Parameters", "io.swagger.v3.oas.annotations.Parameters"),
    PARAMETER("Parameter", "io.swagger.v3.oas.annotations.Parameter");


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


