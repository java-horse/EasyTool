package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.ui.components.OnOffButton;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.handler.sign.JueJinSignService;
import easy.helper.ServiceHelper;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;

import javax.swing.*;
import java.util.Objects;

public class SignCoreView extends CoreCommonView {
    private JPanel panel;
    private JPasswordField cookiePasswordField;
    private JSpinner reservedSpinner;
    private JSlider drawIntervalSlider;
    private OnOffButton drawSwitchButton;
    private JButton saveButton;
    private JLabel drawIntervalLabel;
    private JButton signButton;

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    public SignCoreView() {
        reservedSpinner.setModel(new SpinnerNumberModel(120000, 120000, 1000000, 1000));
        drawIntervalSlider.addChangeListener(e -> drawIntervalLabel.setText(Integer.toString(drawIntervalSlider.getValue())));
        saveButton.setIcon(AllIcons.Actions.MenuSaveall);
        saveButton.addActionListener(e -> {
            if (Objects.nonNull(widgetConfig) && Objects.nonNull(widgetConfig.getSignConfig())) {
                WidgetConfig.SignConfig signConfig = widgetConfig.getSignConfig();
                signConfig.setCookie(String.valueOf(cookiePasswordField.getPassword()));
                signConfig.setReserved(Convert.toInt(reservedSpinner.getModel().getValue()));
                signConfig.setDrawSwitch(drawSwitchButton.isSelected());
                signConfig.setDrawInternal(Convert.toLong(drawIntervalSlider.getValue()));
            }
        });
        signButton.setIcon(AllIcons.Actions.Execute);
        signButton.addActionListener(e -> {
            if (MessageUtil.showOkCancelDialog("确认模拟自动签到？") == MessageConstants.OK) {
                new JueJinSignService().autoSign();
                MessageUtil.showInfoMessage("模拟签到成功");
            }
        });
        if (Objects.nonNull(widgetConfig) && Objects.nonNull(widgetConfig.getSignConfig())) {
            WidgetConfig.SignConfig signConfig = widgetConfig.getSignConfig();
            cookiePasswordField.setText(signConfig.getCookie());
            reservedSpinner.setModel(new SpinnerNumberModel(signConfig.getReserved().intValue(), 120000, 1000000, 1000));
            drawSwitchButton.setSelected(signConfig.getDrawSwitch());
            drawIntervalSlider.setValue(signConfig.getDrawInternal().intValue());
        }
    }

    public JComponent getContent() {
        return panel;
    }

}
