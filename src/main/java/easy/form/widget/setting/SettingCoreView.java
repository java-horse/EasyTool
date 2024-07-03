package easy.form.widget.setting;

import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.helper.ServiceHelper;
import easy.widget.core.CoreCommonView;

import javax.swing.*;

public class SettingCoreView extends CoreCommonView {
    private JPanel panel;
    private JLabel settingLabel;
    private JScrollPane settingScrollPane;

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    public SettingCoreView() {
        settingLabel.setText("请选择 Widget Core View 视窗展示的Tab界面, 默认全部展示");

    }

    public JPanel getContent() {
        return panel;
    }

}
