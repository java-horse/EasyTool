package easy.form.widget;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.enums.WidgetCoreTabEnum;
import easy.form.widget.setting.SettingCoreView;
import easy.helper.ServiceHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class WidgetCommonView extends DialogWrapper {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private static final String SETTING_TAB = "Tab设置";

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
                if (StringUtils.equals(tabEnum.getTitle(), WidgetCoreTabEnum.WINDOWS_PROCESS.getTitle()) && !SystemInfo.isWindows) {
                    continue;
                }
                tabbedPane.add(tabEnum.getTitle(), tabEnum.getComponent());
                widgetConfig.getWidgetCoreTabSet().add(tabEnum.getTitle());
            }
        }
        tabbedPane.addTab(SETTING_TAB, new SettingCoreView().getContent());
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setIconAt(i, AllIcons.Actions.PinTab);
            String tabTitle = tabbedPane.getTitleAt(i);
            tabbedPane.setToolTipTextAt(i, tabTitle);
            if (StringUtils.equals(tabTitle, SETTING_TAB)) {
                tabbedPane.setForegroundAt(i, JBColor.RED);
            }
        }
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 900), Math.max(size.height, 650));
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }


}
