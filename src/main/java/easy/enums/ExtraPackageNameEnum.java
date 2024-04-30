package easy.enums;

public enum ExtraPackageNameEnum {

    SIZE("javax.validation.constraints.Size", "Size"),
    LENGTH("org.hibernate.validator.constraints.Length", "Length"),
    NULL("javax.validation.constraints.Null", "Null"),
    MULTIPART_FILE("org.springframework.web.multipart.MultipartFile", "MultipartFile"),
    DATE_TIME_FORMAT("org.springframework.format.annotation.DateTimeFormat", "DateTimeFormat"),
    PARAMETER_IN("io.swagger.v3.oas.annotations.enums.ParameterIn", "ParameterIn"),
    SCHEDULED("org.springframework.scheduling.annotation.Scheduled", "Scheduled"),
    FILE("java.io.File", "File"),
    DEPRECATED("java.lang.Deprecated", "Deprecated");

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
