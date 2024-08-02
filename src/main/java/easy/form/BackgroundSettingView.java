package easy.form;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.OnOffButton;
import easy.config.background.BackgroundImageConfig;
import easy.config.background.BackgroundImageConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

public class BackgroundSettingView {
    private BackgroundImageConfig config = ServiceHelper.getService(BackgroundImageConfigComponent.class).getState();

    private JPanel panel;
    private OnOffButton imageSwitchButton;
    private JSpinner imageCountSpinner;
    private JSpinner imageTimeModelSpinner;
    private JComboBox<String> imageTimeUnitComboBox;
    private JComboBox<String> imageScopeComboBox;
    private TextFieldWithBrowseButton imageFilePathTextFieldButton;
    private JLabel imageFilePathTextFieldTipLabel;
    private JLabel imageCountTipLabel;
    private JLabel imageTimeModelTipLabel;
    private JLabel imageScopeTipLabel;

    public BackgroundSettingView() {
        imageSwitchButton.addActionListener(e -> {
            boolean selected = imageSwitchButton.isSelected();
            imageFilePathTextFieldButton.setEnabled(selected);
            imageCountSpinner.setEnabled(selected);
            imageTimeModelSpinner.setEnabled(selected);
            imageTimeUnitComboBox.setEnabled(selected);
            imageScopeComboBox.setEnabled(selected);
            if (selected) {
                MessageUtil.showWarningDialog("开启IDE背景轮播会消耗一定内存，请谨慎开启！");
            }
        });
        EasyCommonUtil.customLabelTipText(imageFilePathTextFieldTipLabel, "建议文件夹中背景图片不宜过多");
        EasyCommonUtil.customLabelTipText(imageTimeModelTipLabel, "建议背景轮播间隔单位为：分钟（MINUTES）");
        EasyCommonUtil.customLabelTipText(imageScopeTipLabel, "建议背景图展示范围为：BOTH（IDE可视范围内背景全覆盖）");
        EasyCommonUtil.customLabelTipText(imageCountTipLabel, "背景轮播图数量限制，建议控制在10张以内（无论文件夹有多少图片，都只会读取此限制数量的图片）");
        imageTimeModelSpinner.setModel(new SpinnerNumberModel(5, 1, 1000, 1));
        imageCountSpinner.setModel(new SpinnerNumberModel(2, 2, 20, 1));
        imageFilePathTextFieldButton.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("请选择背景图片文件夹");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String currentFolder = imageFilePathTextFieldButton.getText();
                if (StringUtils.isNotBlank(currentFolder)) {
                    fileChooser.setCurrentDirectory(new File(currentFolder));
                }
                fileChooser.showOpenDialog(panel);
                File selectedFile = fileChooser.getSelectedFile();
                imageFilePathTextFieldButton.setText(Objects.isNull(selectedFile) ? StringUtils.EMPTY : selectedFile.getAbsolutePath());
            }
        });
    }

    public void reset() {
        setImageSwitchButton(config.getImageSwitch());
        setImageFilePathTextFieldButton(config.getImageFilePath());
        setImageCountSpinner(config.getImageCount());
        setImageTimeModelSpinner(config.getImageTimeModel());
        setImageTimeUnitComboBox(config.getImageTimeUnit());
        setImageScopeComboBox(config.getImageScope());
    }

    public JComponent getComponent() {
        return panel;
    }

    public OnOffButton getImageSwitchButton() {
        return imageSwitchButton;
    }

    public void setImageSwitchButton(Boolean imageSwitchButton) {
        this.imageSwitchButton.setSelected(imageSwitchButton);
    }

    public JSpinner getImageCountSpinner() {
        return imageCountSpinner;
    }

    public void setImageCountSpinner(Integer imageCountSpinner) {
        this.imageCountSpinner.setValue(imageCountSpinner);
    }

    public JSpinner getImageTimeModelSpinner() {
        return imageTimeModelSpinner;
    }

    public void setImageTimeModelSpinner(Integer imageTimeModelSpinner) {
        this.imageTimeModelSpinner.setValue(imageTimeModelSpinner);
    }

    public JComboBox<String> getImageTimeUnitComboBox() {
        return imageTimeUnitComboBox;
    }

    public void setImageTimeUnitComboBox(String imageTimeUnitComboBox) {
        this.imageTimeUnitComboBox.setSelectedItem(imageTimeUnitComboBox);
    }

    public JComboBox<String> getImageScopeComboBox() {
        return imageScopeComboBox;
    }

    public void setImageScopeComboBox(String imageScopeComboBox) {
        this.imageScopeComboBox.setSelectedItem(imageScopeComboBox);
    }

    public TextFieldWithBrowseButton getImageFilePathTextFieldButton() {
        return imageFilePathTextFieldButton;
    }

    public void setImageFilePathTextFieldButton(String imageFilePathTextFieldButton) {
        this.imageFilePathTextFieldButton.setText(imageFilePathTextFieldButton);
    }

}
