package easy.restful.util;

import cn.hutool.http.ContentType;
import com.intellij.openapi.project.Project;
import easy.restful.api.ApiService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModuleHeaders {

    private ModuleHeaders() {
    }

    @NotNull
    public static Map<String, String> getModuleHeader(@NotNull Project project, @NotNull String moduleName) {
        Map<String, String> map = Storage.MODULE_HTTP_HEADER.getMap(project, moduleName);
        if (map.isEmpty()) {
            fillDefault(map);
        }
        return map;
    }

    @NotNull
    public static String getModuleHeader(@NotNull Project project, @NotNull String moduleName, @NotNull String headerName) {
        return Storage.MODULE_HTTP_HEADER.getMap(project, moduleName).getOrDefault(headerName, "");
    }

    public static void setModuleHeader(@NotNull Project project, @NotNull String moduleName, @NotNull Map<String, String> headers) {
        Storage.MODULE_HTTP_HEADER.setValue(project, moduleName, headers);
    }

    public static void resetModuleHeader(@NotNull Project project, @NotNull String moduleName) {
        Map<String, String> headers = new HashMap<>();
        fillDefault(headers);
        Storage.MODULE_HTTP_HEADER.setValue(project, moduleName, headers);
    }

    public static void fillDefault(@NotNull Map<String, String> headers) {
        headers.put("Content-Type", ContentType.JSON.getValue());
    }

    public static void apply(@NotNull Map<String, String> headers, @NotNull ApiService apiService) {
        apiService.setModuleHeaders(headers);
    }
}