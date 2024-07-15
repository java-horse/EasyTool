package easy.form.widget.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import easy.base.Constants;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Objects;

public class Base64CoreView extends CoreCommonView {
    private JPanel panel;
    private JRadioButton textRadioButton;
    private JRadioButton imageRadioButton;
    private JTextArea encodeTextArea;
    private JTextArea decodeTextArea;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton copyButton;
    private TextFieldWithBrowseButton imageFileTextField;
    private JLabel encodeImageLabel;
    private JLabel base64TipLabel;
    private JButton clearButton;

    public Base64CoreView() {
        EasyCommonUtil.customLabelTipText(base64TipLabel, "默认进行URL安全的Base64编码");
        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withFileFilter(file -> StrUtil.endWithAnyIgnoreCase(file.getName(), ".jpg", ".png", ".gif"));
        imageFileTextField.addActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, ProjectManagerEx.getInstanceEx().getDefaultProject(), null);
            if (Objects.nonNull(virtualFile)) {
                imageFileTextField.setText(virtualFile.getPath());
                BufferedImage image = ImgUtil.read(virtualFile.getPath());
                encodeImageLabel.setIcon(new ImageIcon(resizeImage(image)));
            }
        });
        textRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                imageFileTextField.setVisible(false);
                encodeTextArea.setVisible(true);
                encodeImageLabel.setIcon(null);
                decodeTextArea.setText(StringUtils.EMPTY);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                imageFileTextField.setVisible(true);
                encodeTextArea.setVisible(false);
                encodeButton.setEnabled(true);
                encodeImageLabel.setIcon(null);
                decodeTextArea.setText(StringUtils.EMPTY);
            }
        });
        textRadioButton.setSelected(true);
        encodeButton.setEnabled(false);
        encodeButton.setIcon(AllIcons.General.ArrowDown);
        areaListener(encodeTextArea, encodeButton);
        decodeButton.setEnabled(false);
        decodeButton.setIcon(AllIcons.General.ArrowUp);
        copyButton.setEnabled(false);
        copyButton.setIcon(AllIcons.Actions.Copy);
        clearButton.setIcon(AllIcons.Actions.GC);
        areaListener(decodeTextArea, decodeButton, copyButton);
        encodeButton.addActionListener(e -> {
            if (textRadioButton.isSelected()) {
                if (StringUtils.isBlank(encodeTextArea.getText())) {
                    MessageUtil.showInfoMessage("请输入待Encode文本");
                    return;
                }
                decodeTextArea.setText(Base64.encodeUrlSafe(encodeTextArea.getText()));
                return;
            }
            if (imageRadioButton.isSelected()) {
                if (StringUtils.isBlank(imageFileTextField.getText())) {
                    MessageUtil.showInfoMessage("请选择待Encode图片");
                    return;
                }
                File file = new File(imageFileTextField.getText());
                if (!FileUtil.exist(file)) {
                    return;
                }
                decodeTextArea.setText(Base64.encodeUrlSafe(file));
            }
        });
        decodeButton.addActionListener(e -> {
            if (StringUtils.isBlank(decodeTextArea.getText())) {
                MessageUtil.showInfoMessage("请输入待Decode文本");
                return;
            }
            if (textRadioButton.isSelected()) {
                if (StringUtils.isNotBlank(encodeTextArea.getText()) && MessageUtil.showOkCancelDialog("是否重新Decode操作?") == MessageConstants.CANCEL) {
                    return;
                }
                try {
                    BufferedImage image = ImgUtil.read(new ByteArrayInputStream(Base64.decode(decodeTextArea.getText())));
                    if (Objects.nonNull(image)) {
                        MessageUtil.showErrorDialog("待Decode文本属于Image类型");
                        return;
                    }
                } catch (Exception ignore) {
                }
                encodeTextArea.setText(Base64.decodeStr(decodeTextArea.getText()));
                return;
            }
            if (imageRadioButton.isSelected()) {
                try {
                    if (Objects.nonNull(encodeImageLabel.getIcon()) && MessageUtil.showOkCancelDialog("是否重新Decode操作?") == MessageConstants.CANCEL) {
                        return;
                    }
                    BufferedImage image = ImgUtil.read(new ByteArrayInputStream(Base64.decode(decodeTextArea.getText())));
                    encodeImageLabel.setIcon(new ImageIcon(resizeImage(image)));
                } catch (Exception ex) {
                    MessageUtil.showErrorDialog(String.format("Image decode exception: %s", ex.getMessage()));
                }
            }
        });
        copyButton.addActionListener(e -> {
            if (StringUtils.isBlank(decodeTextArea.getText())) {
                return;
            }
            CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(decodeTextArea.getText()));
        });
        clearButton.addActionListener(e -> {
            if (ObjectUtils.anyNotNull(encodeTextArea.getText(), decodeTextArea.getText(), imageFileTextField.getText(), encodeImageLabel.getIcon())
                    && MessageUtil.showOkCancelDialog("Confirm Clear Data?") == MessageConstants.OK) {
                encodeTextArea.setText(StringUtils.EMPTY);
                decodeTextArea.setText(StringUtils.EMPTY);
                imageFileTextField.setText(StringUtils.EMPTY);
                encodeImageLabel.setIcon(null);
            }
        });
    }

    /**
     * 调整图像大小
     *
     * @param image 图像
     * @return {@link java.awt.image.BufferedImage}
     * @author mabin
     * @date 2024/07/12 17:28
     */
    private BufferedImage resizeImage(BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(Constants.NUM.TWO_HUNDRED, Constants.NUM.TWO_HUNDRED,
                image.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, Constants.NUM.TWO_HUNDRED, Constants.NUM.TWO_HUNDRED, null);
        g2d.dispose();
        return resizedImage;
    }

    public JPanel getContent() {
        return panel;
    }

}
