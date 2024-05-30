package easy.enums;

public enum ExtraPackageNameEnum {

    //----------------------javax.validation.constraints.*-------------------------//
    NULL("javax.validation.constraints.Null", "Null"),
    SIZE("javax.validation.constraints.Size", "Size"),
    NOT_NULL("javax.validation.constraints.NotNull", "NotNull"),
    NOT_EMPTY("javax.validation.constraints.NotEmpty", "NotEmpty"),
    NOT_BLANK("javax.validation.constraints.NotBlank", "NotBlank"),
    MIN("javax.validation.constraints.Min", "Min"),
    MAX("javax.validation.constraints.Max", "Max"),
    DECIMAL_MIN("javax.validation.constraints.DecimalMin", "DecimalMin"),
    DECIMAL_MAX("javax.validation.constraints.DecimalMax", "DecimalMax"),
    POSITIVE("javax.validation.constraints.Positive", "Positive"),
    POSITIVE_OR_ZERO("javax.validation.constraints.PositiveOrZero", "PositiveOrZero"),
    NEGATIVE("javax.validation.constraints.Negative", "Negative"),
    NEGATIVE_OR_ZERO("javax.validation.constraints.NegativeOrZero", "NegativeOrZero"),

    //----------------------jakarta.validation.constraints.*-------------------------//
    NOT_NULL2("jakarta.validation.constraints.NotNull", "NotNull"),
    NOT_EMPTY2("jakarta.validation.constraints.NotEmpty", "NotEmpty"),
    NOT_BLANK2("jakarta.validation.constraints.NotBlank", "NotBlank"),
    SIZE2("jakarta.validation.constraints.Size", "Size"),
    MIN2("jakarta.validation.constraints.Min", "Min"),
    MAX2("jakarta.validation.constraints.Max", "Max"),
    DECIMAL_MIN2("jakarta.validation.constraints.DecimalMin", "DecimalMin"),
    DECIMAL_MAX2("jakarta.validation.constraints.DecimalMax", "DecimalMax"),
    POSITIVE2("jakarta.validation.constraints.Positive", "Positive"),
    POSITIVE_OR_ZERO2("jakarta.validation.constraints.PositiveOrZero", "PositiveOrZero"),
    NEGATIVE2("jakarta.validation.constraints.Negative", "Negative"),
    NEGATIVE_OR_ZERO2("jakarta.validation.constraints.NegativeOrZero", "NegativeOrZero"),

    //----------------------other-------------------------//
    LENGTH("org.hibernate.validator.constraints.Length", "Length"),
    MULTIPART_FILE("org.springframework.web.multipart.MultipartFile", "MultipartFile"),
    DATE_TIME_FORMAT("org.springframework.format.annotation.DateTimeFormat", "DateTimeFormat"),
    PARAMETER_IN("io.swagger.v3.oas.annotations.enums.ParameterIn", "ParameterIn"),
    SCHEDULED("org.springframework.scheduling.annotation.Scheduled", "Scheduled"),
    JSON_PROPERTY("com.fasterxml.jackson.annotation.JsonProperty", "JsonProperty"),
    JSON_FORMAT("com.fasterxml.jackson.annotation.JsonFormat", "JsonFormat"),
    JSON_IGNORE("com.fasterxml.jackson.annotation.JsonIgnore", "JsonIgnore"),
    JSON_IGNORE_PROPERTIES("com.fasterxml.jackson.annotation.JsonIgnoreProperties", "JsonIgnoreProperties"),
    JSON_INCLUDE_PROPERTIES("com.fasterxml.jackson.annotation.JsonIncludeProperties", "JsonIncludeProperties"),
    VALIDATED("org.springframework.validation.annotation.Validated", "Validated"),
    RUN_WITH("org.junit.runner.RunWith", "RunWith"),
    ;

    private final String name;
    private final String className;


    ExtraPackageNameEnum(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }
}
