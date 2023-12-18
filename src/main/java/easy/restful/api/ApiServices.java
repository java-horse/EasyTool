package easy.restful.api;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import easy.restful.util.ModuleConfigs;
import easy.restful.util.ModuleHeaders;
import easy.restful.scanner.IFrameworkHelper;
import easy.restful.scanner.IJavaFramework;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.module.ModuleUtil.findModuleForFile;

public class ApiServices {

    private ApiServices() {
    }

    /**
     * 获取所有的Request
     *
     * @param project project
     * @return map-{key: moduleName, value: itemRequestList}
     */
    @NotNull
    public static Map<String, List<ApiService>> getApis(@NotNull Project project) {
        return getApis(project, false);
    }

    /**
     * 获取所有的Request
     *
     * @param hasEmpty 是否生成包含空Request的moduleName
     * @param project  project
     * @return map-{key: moduleName, value: itemRequestList}
     */
    @NotNull
    public static Map<String, List<ApiService>> getApis(@NotNull Project project, boolean hasEmpty) {
        return getApis(project, ModuleManager.getInstance(project).getModules(), hasEmpty);
    }

    /**
     * 获取所有的Request
     *
     * @param hasEmpty 是否生成包含空Request的moduleName
     * @param modules  所有模块
     * @param project  project
     * @return map-{key: moduleName, value: itemRequestList}
     */
    @NotNull
    public static Map<String, List<ApiService>> getApis(@NotNull Project project, Module @NotNull [] modules, boolean hasEmpty) {
        Map<String, List<ApiService>> map = new HashMap<>(16);
        for (Module module : modules) {
            List<ApiService> apiServices = getModuleApis(project, module);
            if (!hasEmpty && apiServices.isEmpty()) {
                continue;
            }
            map.put(module.getName(), apiServices);
        }
        return map;
    }

    private static List<ApiService> fill(@NotNull Project project, @NotNull String moduleName,
                                         @NotNull List<ApiService> apiServices) {
        // 填充模块url前缀
        Map<String, String> moduleConfig = ModuleConfigs.getModuleConfig(project, moduleName);
        // 填充HttpHeader
        Map<String, String> moduleHeader = ModuleHeaders.getModuleHeader(project, moduleName);

        apiServices.forEach(api -> {
            if (!moduleConfig.isEmpty()) {
                ModuleConfigs.Config.apply(moduleConfig, api);
            }
            if (!moduleHeader.isEmpty()) {
                ModuleHeaders.apply(moduleHeader, api);
            }
        });
        return apiServices;
    }

    /**
     * 获取选中module的所有Request
     *
     * @param project project
     * @param module  module
     * @return list
     */
    @NotNull
    public static List<ApiService> getModuleApis(@NotNull Project project, @NotNull Module module) {
        List<ApiService> apiServices = new ArrayList<>();
        for (IJavaFramework helper : IFrameworkHelper.getJavaHelpers()) {
            Collection<ApiService> service = helper.getService(project, module);
            if (service.isEmpty()) {
                continue;
            }
            apiServices.addAll(service);
        }

        return fill(project, module.getName(), apiServices);
    }

}
