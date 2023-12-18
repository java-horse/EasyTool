package easy.restful.util;

import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Storage implements IStorage {

    /**
     * 扫描service时包括lib
     */
    SCAN_SERVICE_WITH_LIB("SCAN_SERVICE_WITH_LIB"),

    /**
     * 模块api前缀路径
     */
    MODULE_API_PREFIX_PATH("MODULE_API_PREFIX_PATH"),

    /**
     * 模块的http配置
     */
    MODULE_HTTP_CONFIG("MODULE_HTTP_CONFIG"),

    /**
     * 模块的header配置
     */
    MODULE_HTTP_HEADER("MODULE_HTTP_HEADER");

    private final String name;

    Storage(@NotNull String name) {
        this.name = Constants.PLUGIN_NAME + ":" + name;
    }

    public static boolean scanServiceWithLibrary(@NotNull Project project) {
        return SCAN_SERVICE_WITH_LIB.getBoolean(project, false);
    }

    public static void scanServiceWithLibrary(@NotNull Project project, boolean flag) {
        SCAN_SERVICE_WITH_LIB.setValue(project, flag);
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    public Map<String, String> getMap(@NotNull Project project) {
        String json = getString(project, "{}");
        Map<?, ?> map = JsonUtil.fromJson(json, Map.class);
        if (map == null) {
            return new HashMap<>();
        }
        //noinspection CastCanBeRemovedNarrowingVariableType,unchecked
        return (Map<String, String>) map;
    }

    public void setValue(@NotNull Project project, @Nullable Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            setValue(project, (String) null);
            return;
        }
        setValue(project, JsonUtil.toJson(map));
    }

    @NotNull
    public Map<String, String> getMap(@NotNull Project project, @NotNull String childName) {
        String json = IStorage.getInstance(project).getValue(getName() + ":" + childName, "{}");
        Map<?, ?> map = JsonUtil.fromJson(json, Map.class);
        if (map == null) {
            return new HashMap<>();
        }
        //noinspection CastCanBeRemovedNarrowingVariableType,unchecked
        return (Map<String, String>) map;
    }

    public void setValue(@NotNull Project project, @NotNull String childName, @Nullable Map<String, String> map) {
        String value = "{}";
        if (map != null && !map.isEmpty()) {
            value = JsonUtil.toJson(map);
        }
        IStorage.getInstance(project).setValue(getName() + ":" + childName, value);
    }

}
