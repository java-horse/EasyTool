package easy.enums;

public enum BackgroundImageChangeScopeEnum {

    BOTH("BOTH"),
    EDITOR("EDITOR"),
    FRAME("FRAME");

    private final String name;

    BackgroundImageChangeScopeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
