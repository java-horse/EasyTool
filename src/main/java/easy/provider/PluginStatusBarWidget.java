package easy.provider;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.IconLikeCustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import easy.base.Constants;
import easy.form.widget.WidgetCommonView;
import easy.handler.PluginForUpdateHandler;
import easy.icons.EasyIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class PluginStatusBarWidget extends TextPanel.WithIconAndArrows implements StatusBarWidget, IconLikeCustomStatusBarWidget {
    public static final String ID = Constants.PLUGIN_NAME + "PluginStatusBarWidget";

    private Project project;

    public PluginStatusBarWidget(@NotNull Project project) {
        this.project = project;
        setIcon(EasyIcons.ICON.LOGO);
        setToolTipText(Constants.PLUGIN_NAME);
        setTextAlignment(TextPanel.CENTER_ALIGNMENT);
    }

    @Override
    public @NonNls @NotNull String ID() {
        return ID;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void dispose() {
        this.project = null;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        if (project.isDisposed()) {
            return;
        }
        // 设置点击监听器
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent event, int clickCount) {
                if (!project.isDisposed()) {
                    DataContext dataContext = DataManager.getInstance().getDataContext(event.getComponent());
                    ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(Constants.PLUGIN_NAME, createActionGroup(),
                            dataContext, true, null, Constants.NUM.TEN);
                    popup.showInBestPositionFor(dataContext);
                }
                return true;
            }
        }.installOn(this, true);
    }

    /**
     * 创建操作组
     *
     * @return {@link com.intellij.openapi.actionSystem.DefaultActionGroup}
     * @author mabin
     * @date 2024/07/18 16:21
     */
    private DefaultActionGroup createActionGroup() {
        return new DefaultActionGroup(Constants.PLUGIN_NAME, true) {
            {
                addSeparator();
                add(new AnAction("检查更新", "检查更新", AllIcons.Ide.Notification.PluginUpdate) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        PluginForUpdateHandler.forUpdate(project);
                    }
                });
                add(new AnAction("效率组件", "效率组件", EasyIcons.ICON.PUZZLE) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        new WidgetCommonView().show();
                    }
                });
                addSeparator();
                add(new AnAction("插件设置", "插件设置", EasyIcons.ICON.SETTING) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, Constants.PLUGIN_NAME);
                    }
                });
            }
        };
    }

}
