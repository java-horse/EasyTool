package easy.form;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
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
    private JLabel imageCountLabel;
    private TextFieldWithBrowseButton imageFolderTextField;
    private JSpinner intervalSpinner;
    private JComboBox timeUnitComboBox;
    private JComboBox changeScopeComboBox;
    private JCheckBox changeSwitchEnableCheckBox;
    private JLabel changeSwitchTipLabel;
    private JLabel imageFolderTipLabel;
    private JLabel changeModelTipLabel;
    private JLabel changeScopeTipLabel;
    private JSpinner imageCountSpinner;
    private JLabel imageCountTipLabel;

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
        imageCountTipLabel.setIcon(AllIcons.General.ContextHelp);
        imageCountTipLabel.setToolTipText("背景轮播图数量限制，建议控制在10张以内（无论文件夹有多少图片，都只会读取此限制数量的图片）");
        // 默认不开启背景轮播功能
        changeSwitchEnableCheckBox.setSelected(false);
        changeSwitchEnableCheckBox.addActionListener(e -> {
            boolean changeSwitch = changeSwitchEnableCheckBox.isSelected();
            imageFolderTextField.setEnabled(changeSwitch);
            intervalSpinner.setEnabled(changeSwitch);
            timeUnitComboBox.setEnabled(changeSwitch);
            changeScopeComboBox.setEnabled(changeSwitch);
            imageCountSpinner.setEnabled(changeSwitch);
        });
        // 设置轮播模式（最大值，最小值，步长等）
        intervalSpinner.setModel(new SpinnerNumberModel(5, 5, 1000, 5));
        imageCountSpinner.setModel(new SpinnerNumberModel(5, 2, 100, 1));
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
        if (changeSwitchEnableCheckBox.isSelected() && interval > Constants.NUM.FIVE) {
            BackgroundService.start();
        } else {
            BackgroundService.stop();
        }
    }

    private void createUIComponents() {
        // 打开设置页面时关闭轮播任务
        BackgroundService.stop();
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        intervalSpinner = new JSpinner(new SpinnerNumberModel(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.FIVE), 5, 1000, 5));
        imageCountSpinner = new JSpinner(new SpinnerNumberModel(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.IMAGE_COUNT, Constants.NUM.FIVE), 2, 100, 1));
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
        int imageCount = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.IMAGE_COUNT, Constants.NUM.FIVE);
        int uiImageCount = ((SpinnerNumberModel) imageCountSpinner.getModel()).getNumber().intValue();
        int storedInterval = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.FIVE);
        int uiInterval = ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue();
        int timeUnit = timeUnitComboBox.getSelectedIndex();
        int storedTimeUnit = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, Constants.NUM.ONE);
        return imageCount != uiImageCount || storedInterval != uiInterval || storedTimeUnit != timeUnit;
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
        imageFolderTextField.setEnabled(changeSwitchEnableCheckBox.isSelected());
        imageCountSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
        imageCountSpinner.setValue(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.IMAGE_COUNT, Constants.NUM.FIVE));
        intervalSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
        intervalSpinner.setValue(propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.FIVE));
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
        // 检查背景图像属性正确性
        checkBackgroundImageProperties();
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, changeSwitchEnableCheckBox.isSelected());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER, imageFolderTextField.getText());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue(), Constants.NUM.FIVE);
        intervalSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
        propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.IMAGE_COUNT, ((SpinnerNumberModel) imageCountSpinner.getModel()).getNumber().intValue(), Constants.NUM.FIVE);
        imageCountSpinner.setEnabled(changeSwitchEnableCheckBox.isSelected());
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
     * 检查背景图像属性正确性
     *
     * @author mabin
     * @date 2024/03/07 17:34
     */
    private void checkBackgroundImageProperties() throws ConfigurationException {
        // 校验图片文件夹是否存在且文件夹下存在图片
        String imageFolder = imageFolderTextField.getText();
        if (!FileUtil.exist(imageFolder)) {
            throw new ConfigurationException(String.format("文件夹：%s 资源不存在", imageFolder));
        }
        File[] files = FileUtil.file(imageFolder).listFiles((dir, name) -> StringUtils.endsWithAny(name, "jpg", "png", "gif"));
        if (ArrayUtil.isEmpty(files)) {
            throw new ConfigurationException(String.format("文件夹：%s 中不存在符合条件的图片资源", imageFolder));
        }
        // 校验相关数字是否合法
        int intervalValue = ((SpinnerNumberModel) intervalSpinner.getModel()).getNumber().intValue();
        if (intervalValue < Constants.NUM.FIVE || intervalValue > 1000) {
            throw new ConfigurationException("间隔时间不符合表达式：5 <= x <= 1000");
        }
        int imageCountValue = ((SpinnerNumberModel) imageCountSpinner.getModel()).getNumber().intValue();
        if (imageCountValue < Constants.NUM.TWO || imageCountValue > 100) {
            throw new ConfigurationException("图片数量限制不符合表达式：2 <= x <= 100");
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
