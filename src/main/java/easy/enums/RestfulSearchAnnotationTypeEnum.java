package easy.enums;

public enum RestfulSearchAnnotationTypeEnum {
    CONTROLLER("Controller", "org.springframework.stereotype.Controller"),
    REST_CONTROLLER("RestController", "org.springframework.web.bind.annotation.RestController"),
    ROSE_PATH("Path", "net.paoding.rose.web.annotation.Path"),
    JAVAX_PATH("Path", "javax.ws.rs.Path");

    private final String name;
    private final String qualifiedName;

    RestfulSearchAnnotationTypeEnum(String name, String qualifiedName) {
        this.name = name;
        this.qualifiedName = qualifiedName;
    }

    public static RestfulSearchAnnotationTypeEnum getByQualifiedName(String qualifiedName) {
        for (RestfulSearchAnnotationTypeEnum annotationTypeEnum : values()) {
            if (annotationTypeEnum.getQualifiedName().equals(qualifiedName)) {
                return annotationTypeEnum;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

}
