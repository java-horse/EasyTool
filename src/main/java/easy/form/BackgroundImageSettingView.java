package easy.form;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.background.BackgroundService;
import easy.base.Constants;
import easy.enums.BackgroundImageChangeScopeEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

public class BackgroundImageSettingView implements Configurable {
    private JPanel panel;
    private JLabel imageFolderLabel;
    private JLabel changeModelLabel;
    private JLabel changeScopeLabel;
    private JLabel changeSwitchLabel;
    private TextFieldWithBrowseButton imageFolderTextField;
    private JSpinner intervalSpinner;
    private JComboBox timeUnitComboBox;
    private JComboBox changeScopeComboBox;
    private JCheckBox changeSwitchEnableCheckBox;
    private JLabel changeSwitchTipLabel;
    private JLabel imageFolderTipLabel;
    private JLabel changeModelTipLabel;
    private JLabel changeScopeTipLabel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "BackgroundImage";
    }

    @Override
    public @Nullable JComponent createComponent() {
        // 设置功能提示
        changeSwitchTipLabel.setIcon(AllIcons.General.Warning);
        changeSwitchTipLabel.setToolTipText("开启IDE背景轮播会消耗一定内存，请谨慎开启！");
        imageFolderTipLabel.setIcon(AllIcons.General.ContextHelp);
        imageFolderTipLabel.setToolTipText("建议文件夹中背景图片不宜过多");
        changeModelTipLabel.setIcon(AllIcons.General.ContextHelp);
        changeModelTipLabel.setToolTipText("建议背景轮播间隔单位为：分钟（MINUTES）");
        changeScopeTipLabel.setIcon(AllIcons.General.ContextHelp);
        changeScopeTipLabel.setToolTipText("建议背景图展示范围为：BOTH（IDE可视范围内背景全覆盖）");
        // 默认不开启背景轮播功能
        changeSwitchEnableCheckBox.setSelected(false);
        changeSwitchEnableCheckBox.addActionListener(e -> {
            imageFolderTextField.setEnabled(changeSwitchEnableCheckBox.isSelected());
            intervalSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
            timeUnitComboBox.setEnabled(changeSwitchEnableCheckBox.isSelected());
            changeScopeComboBox.setEnabled(changeSwitchEnableCheckBox.isSelected());
        });
        // 设置文件选择监听
        FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        imageFolderTextField.addBrowseFolderListener(new TextBrowseFolderListener(singleFolderDescriptor) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String currentFolder = imageFolderTextField.getText();
                if (StringUtils.isNotBlank(currentFolder)) {
                    fileChooser.setCurrentDirectory(new File(currentFolder));
                }
                fileChooser.showOpenDialog(panel);
                File selectedFile = fileChooser.getSelectedFile();
                imageFolderTextField.setText(Objects.isNull(selectedFile) ? StringUtils.EMPTY : selectedFile.getAbsolutePath());
            }
        });
        return panel;
    }

    @Override
    public void disposeUIResources() {
        // 关闭设置页面时重新判断是否启动
        int interval = ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue();
        if (changeSwitchEnableCheckBox.isSelected() && interval > Constants.NUM.ZERO) {
            BackgroundService.start();
        } else {
            BackgroundService.stop();
        }
    }

    private void createUIComponents() {
        // 打开设置页面时关闭轮播任务
        BackgroundService.stop();
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        intervalSpinner = new JSpinner(new SpinnerNumberModel(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.ZERO), 0, 1000, 5));
        timeUnitComboBox.setSelectedIndex(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, Constants.NUM.ONE));
    }

    @Override
    public boolean isModified() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String storedFolder = propertiesComponent.getValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER, StringUtils.EMPTY);
        return !StringUtils.equals(storedFolder, imageFolderTextField.getText())
                || changeModelModified(propertiesComponent)
                || changeScopeModified(propertiesComponent)
                || propertiesComponent.getBoolean(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH) != changeSwitchEnableCheckBox.isSelected();
    }

    private boolean changeModelModified(PropertiesComponent propertiesComponent) {
        int storedInterval = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.ZERO);
        int uiInterval = ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue();
        int timeUnit = timeUnitComboBox.getSelectedIndex();
        int storedTimeUnit = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, Constants.NUM.ONE);
        return storedInterval != uiInterval || storedTimeUnit != timeUnit;
    }

    private boolean changeScopeModified(PropertiesComponent prop) {
        String storedText = prop.getValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE, getChangeScopeCombination());
        String chooseScopeText = null;
        if (StringUtils.equals(String.valueOf(changeScopeComboBox.getSelectedItem()), BackgroundImageChangeScopeEnum.EDITOR.getName())) {
            chooseScopeText = IdeBackgroundUtil.EDITOR_PROP;
        } else if (StringUtils.equals(String.valueOf(changeScopeComboBox.getSelectedItem()), BackgroundImageChangeScopeEnum.FRAME.getName())) {
            chooseScopeText = IdeBackgroundUtil.FRAME_PROP;
        } else if (StringUtils.equals(String.valueOf(changeScopeComboBox.getSelectedItem()), BackgroundImageChangeScopeEnum.BOTH.getName())) {
            chooseScopeText = getChangeScopeCombination();
        }
        return !StringUtils.equals(storedText, chooseScopeText);
    }

    @Override
    public void reset() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        changeSwitchEnableCheckBox.setSelected(propertiesComponent.getBoolean(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false));
        imageFolderTextField.setText(propertiesComponent.getValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER));
        intervalSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
        intervalSpinner.setValue(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.ZERO));
        timeUnitComboBox.setSelectedIndex(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, Constants.NUM.ONE));
        String storedText = propertiesComponent.getValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE, getChangeScopeCombination());
        if (StringUtils.equals(storedText, IdeBackgroundUtil.EDITOR_PROP)) {
            changeScopeComboBox.setSelectedItem(BackgroundImageChangeScopeEnum.EDITOR.getName());
        } else if (StringUtils.equals(storedText, IdeBackgroundUtil.FRAME_PROP)) {
            changeScopeComboBox.setSelectedItem(BackgroundImageChangeScopeEnum.FRAME.getName());
        } else if (StringUtils.equals(storedText, getChangeScopeCombination())) {
            changeScopeComboBox.setSelectedItem(BackgroundImageChangeScopeEnum.BOTH.getName());
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, changeSwitchEnableCheckBox.isSelected());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER, imageFolderTextField.getText());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue(), Constants.NUM.ZERO);
        intervalSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, timeUnitComboBox.getSelectedIndex(), Constants.NUM.ONE);
        String changeScopeText = String.valueOf(changeScopeComboBox.getSelectedItem());
        if (StringUtils.equals(changeScopeText, BackgroundImageChangeScopeEnum.BOTH.getName())) {
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE, getChangeScopeCombination());
        } else if (StringUtils.equals(changeScopeText, BackgroundImageChangeScopeEnum.EDITOR.getName())) {
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE, IdeBackgroundUtil.EDITOR_PROP);
        } else if (StringUtils.equals(changeScopeText, BackgroundImageChangeScopeEnum.FRAME.getName())) {
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE, IdeBackgroundUtil.FRAME_PROP);
        }
    }

    /**
     * 获取变更范围组合
     *
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/06 14:16
     */
    private String getChangeScopeCombination() {
        return IdeBackgroundUtil.EDITOR_PROP + "," + IdeBackgroundUtil.FRAME_PROP;
    }

}
