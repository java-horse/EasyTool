package easy.provider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import easy.base.Constants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PluginStatusBarWidgetProvider implements StatusBarWidgetFactory {

    @Override
    public @NonNls @NotNull String getId() {
        return PluginStatusBarWidget.ID;
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return Constants.PLUGIN_NAME;
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return project.isInitialized();
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new PluginStatusBarWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return Objects.nonNull(statusBar.getProject()) && statusBar.getProject().isInitialized();
    }

}
