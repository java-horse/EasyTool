package easy.restful.scanner;


import com.intellij.openapi.module.Module;
import com.intellij.psi.search.GlobalSearchScope;
import easy.restful.util.Storage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 扫描仪api帮助程序
 *
 * @author mabin
 * @project EasyTool
 * @package easy.restful.scanner
 * @date 2024/05/09 10:15
 */
public class ScannerApiHelper {

    private static final String SLASH = "/";

    private ScannerApiHelper() {
    }


    /**
     * 格式化路径
     *
     * @param path 路径
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/09 10:16
     */
    @NotNull
    @Contract(pure = true)
    public static String formatPath(@Nullable Object path) {
        if (Objects.isNull(path)) {
            return SLASH;
        }
        String currPath;
        if (path instanceof String) {
            currPath = (String) path;
        } else {
            currPath = path.toString();
        }
        if (currPath.startsWith(SLASH)) {
            return currPath;
        }
        return SLASH + currPath;
    }

    /**
     * 获取模块范围
     *
     * @param module 模块
     * @return {@link com.intellij.psi.search.GlobalSearchScope}
     * @author mabin
     * @date 2024/05/09 10:16
     */
    public static GlobalSearchScope getModuleScope(@NotNull Module module) {
        return getModuleScope(module, Storage.scanServiceWithLibrary(module.getProject()));
    }

    /**
     * 获取模块范围
     *
     * @param module     模块
     * @param hasLibrary 有图书馆
     * @return {@link com.intellij.psi.search.GlobalSearchScope}
     * @author mabin
     * @date 2024/05/09 10:16
     */
    public static GlobalSearchScope getModuleScope(@NotNull Module module, boolean hasLibrary) {
        if (hasLibrary) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleScope();
        }
    }

}
