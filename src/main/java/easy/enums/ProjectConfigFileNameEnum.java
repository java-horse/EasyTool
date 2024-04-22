package easy.enums;

public enum ProjectConfigFileNameEnum {

    APPLICATION_YML("application", ".yml", "server.port"),
    APPLICATION_PROPERTIES("application", ".properties", "server.port"),
    BOOTSTRAP_PROPERTIES("bootstrap", ".properties", "server.port"),
    APPLICATION_YAML("application", ".yaml", "server.port"),
    BOOTSTRAP_YAML("bootstrap", ".yaml", "server.port");

    private final String name;
    private final String suffix;
    private final String portKey;

    ProjectConfigFileNameEnum(String name, String suffix, String portKey) {
        this.name = name;
        this.suffix = suffix;
        this.portKey = portKey;
    }

    public String getName() {
        return name;
    }

    public String getPortKey() {
        return portKey;
    }

    public String getSuffix() {
        return suffix;
    }

}
