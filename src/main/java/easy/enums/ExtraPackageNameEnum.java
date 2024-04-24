package easy.enums;

public enum ExtraPackageNameEnum {

    SIZE("javax.validation.constraints.Size"),
    LENGTH("org.hibernate.validator.constraints.Length"),
    NULL("javax.validation.constraints.Null"),
    MULTIPART_FILE("org.springframework.web.multipart.MultipartFile"),
    FILE("java.io.File");

    private final String name;


    ExtraPackageNameEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
