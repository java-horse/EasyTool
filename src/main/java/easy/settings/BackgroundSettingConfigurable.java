package easy.settings;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.background.BackgroundService;
import easy.base.Constants;
import easy.config.background.BackgroundImageConfig;
import easy.config.background.BackgroundImageConfigComponent;
import easy.form.BackgroundSettingView;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class BackgroundSettingConfigurable implements Configurable {

    private BackgroundImageConfig config = ServiceHelper.getService(BackgroundImageConfigComponent.class).getState();
    private BackgroundSettingView view = new BackgroundSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "BackgroundImage";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public boolean isModified() {
        return !config.getImageSwitch().equals(view.getImageSwitchButton().isSelected())
                || !Objects.equals(config.getImageCount(), view.getImageCountSpinner().getValue())
                || !Objects.equals(config.getImageFilePath(), view.getImageFilePathTextFieldButton().getText())
                || !Objects.equals(config.getImageScope(), view.getImageScopeComboBox().getSelectedItem())
                || !Objects.equals(config.getImageTimeModel(), view.getImageTimeModelSpinner().getValue())
                || !Objects.equals(config.getImageTimeUnit(), view.getImageTimeUnitComboBox().getSelectedItem());
    }

    @Override
    public void apply() throws ConfigurationException {
        checkBackgroundImageProperties();
        config.setImageSwitch(view.getImageSwitchButton().isSelected());
        config.setImageCount(Convert.toInt(view.getImageCountSpinner().getValue()));
        config.setImageFilePath(view.getImageFilePathTextFieldButton().getText());
        config.setImageScope(Convert.toStr(view.getImageScopeComboBox().getSelectedItem()));
        config.setImageTimeModel(Convert.toInt(view.getImageTimeModelSpinner().getValue()));
        config.setImageTimeUnit(Convert.toStr(view.getImageTimeUnitComboBox().getSelectedItem()));
    }

    @Override
    public void reset() {
        view.reset();
    }

    @Override
    public void disposeUIResources() {
        // 关闭设置页面时重新判断是否启动
        if (view.getImageSwitchButton().isSelected()) {
            if (!BackgroundService.isRunning()) {
                BackgroundService.start();
            } else {
                BackgroundService.restart();
            }
        } else {
            BackgroundService.stop();
        }
    }

    /**
     * 检查背景图像属性正确性
     *
     * @author mabin
     * @date 2024/03/07 17:34
     */
    private void checkBackgroundImageProperties() throws ConfigurationException {
        // 校验图片文件夹是否存在且文件夹下存在图片(数量>=2)
        String imageFolder = view.getImageFilePathTextFieldButton().getText();
        if (!FileUtil.exist(imageFolder)) {
            throw new ConfigurationException(String.format("文件夹：%s 资源不存在", imageFolder));
        }
        File[] files = FileUtil.file(imageFolder).listFiles((dir, name) -> StringUtils.endsWithAny(name, "jpg", "png", "gif"));
        if (ArrayUtil.isEmpty(files)) {
            throw new ConfigurationException(String.format("文件夹：%s 中不存在符合条件的图片资源", imageFolder));
        }
        if (files.length < 2) {
            throw new ConfigurationException(String.format("文件夹：%s 中需至少存在2张图片资源", imageFolder));
        }
        // 校验相关数字是否合法
        int intervalValue = Convert.toInt(view.getImageTimeModelSpinner().getValue());
        if (intervalValue < Constants.NUM.ONE || intervalValue > Constants.NUM.ONE_THOUSAND) {
            throw new ConfigurationException(String.format("图片轮播间隔不符合表达式：%d <= x <= %d", Constants.NUM.ONE, Constants.NUM.ONE_THOUSAND));
        }
        int imageCountValue = Convert.toInt(view.getImageCountSpinner().getValue());
        if (imageCountValue < Constants.NUM.TWO || imageCountValue > Constants.NUM.TWENTY) {
            throw new ConfigurationException(String.format("图片数量限制不符合表达式：%d <= x <= %d", Constants.NUM.TWO, Constants.NUM.TWENTY));
        }
    }

}
