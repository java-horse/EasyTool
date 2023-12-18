package easy.util;

import com.intellij.openapi.module.Module;
import com.intellij.psi.search.GlobalSearchScope;
import easy.restful.util.Storage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class SystemUtil {

    private static final String SLASH = "/";

    private SystemUtil() {
    }

    /**
     * 格式化request path
     *
     * @param path path
     * @return format path
     */
    @NotNull
    @Contract(pure = true)
    public static String formatPath(@Nullable Object path) {
        if (path == null) {
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

    public static GlobalSearchScope getModuleScope(@NotNull Module module) {
        return getModuleScope(module, Storage.scanServiceWithLibrary(module.getProject()));
    }

    protected static GlobalSearchScope getModuleScope(@NotNull Module module, boolean hasLibrary) {
        if (hasLibrary) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleScope();
        }
    }

    public static class Clipboard {

        private Clipboard() {
        }

        /**
         * 把文本设置到剪贴板（复制）
         */
        public static void copy(String text) {
            // 获取系统剪贴板
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 封装文本内容
            Transferable trans = new StringSelection(text);
            // 把文本内容设置到系统剪贴板
            clipboard.setContents(trans, null);
        }

        /**
         * 从剪贴板中获取文本（粘贴）
         */
        @Nullable
        public static String paste() {
            // 获取系统剪贴板
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 获取剪贴板中的内容
            Transferable trans = clipboard.getContents(null);
            if (trans != null) {
                // 判断剪贴板中的内容是否支持文本
                if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        // 获取剪贴板中的文本内容
                        return (String) trans.getTransferData(DataFlavor.stringFlavor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

}
