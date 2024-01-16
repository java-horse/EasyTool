package easy.util;

import com.intellij.AbstractBundle;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

public class BundleUtil extends AbstractBundle {

    @NonNls
    public static final String I18N = "messages.easytool_i18n";

    @NotNull
    private static final BundleUtil INSTANCE = new BundleUtil(I18N);

    private BundleUtil(@NonNls String resource) {
        super(resource);
    }

    @Nls
    @NotNull
    protected static String message(@PropertyKey(resourceBundle = I18N) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    @Nls
    @NotNull
    public static String getI18n(@PropertyKey(resourceBundle = I18N) String key, Object... params) {
        return message(key, params);
    }

    @Override
    @NotNull
    protected ResourceBundle findBundle(@NotNull @NonNls String pathToBundle,
                                        @NotNull ClassLoader loader,
                                        @NotNull ResourceBundle.Control control) {
        String chineseLanguagePlugin = "com.intellij.zh";
        if (!PluginManager.isPluginInstalled(PluginId.getId(chineseLanguagePlugin))) {
            return ResourceBundle.getBundle(pathToBundle, Locale.ROOT, loader, control);
        }
        return ResourceBundle.getBundle(pathToBundle, Locale.getDefault(), loader, control);
    }

}
