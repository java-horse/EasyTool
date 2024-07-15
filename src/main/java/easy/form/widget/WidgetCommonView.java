package easy.form.widget;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.enums.WidgetCoreTabEnum;
import easy.form.widget.setting.SettingCoreView;
import easy.helper.ServiceHelper;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class WidgetCommonView extends DialogWrapper {
    private JPanel panel;
    private JTabbedPane tabbedPane;

    public WidgetCommonView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("Widget Core View");
        WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();
        if (Objects.isNull(widgetConfig)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(widgetConfig.getWidgetCoreTabSet())) {
            for (String tabName : widgetConfig.getWidgetCoreTabSet()) {
                Component component = WidgetCoreTabEnum.getComponent(tabName);
                if (Objects.isNull(component)) {
                    continue;
                }
                tabbedPane.add(tabName, component);
            }
        } else {
            for (WidgetCoreTabEnum tabEnum : WidgetCoreTabEnum.values()) {
                tabbedPane.add(tabEnum.getTitle(), tabEnum.getComponent());
                widgetConfig.getWidgetCoreTabSet().add(tabEnum.getTitle());
            }
        }
        tabbedPane.addTab("Tab设置", new SettingCoreView().getContent());
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 800), Math.max(size.height, 600));
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }


}
