package easy.form.widget.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
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
    private JLabel base64TipLabel;

    public Base64CoreView() {
        EasyCommonUtil.customLabelTipText(base64TipLabel, "默认进行URL安全的Base64编码");
        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withFileFilter(file -> StrUtil.endWithAnyIgnoreCase(file.getName(), ".jpg", ".png", ".gif"));
        imageFileTextField.addActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, ProjectManagerEx.getInstanceEx().getDefaultProject(), null);
            if (Objects.nonNull(virtualFile)) {
                imageFileTextField.setText(virtualFile.getPath());
            }
        });
        textRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                imageFileTextField.setVisible(false);
                encodeTextArea.setVisible(true);
                decodeTextArea.setText(StringUtils.EMPTY);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                imageFileTextField.setVisible(true);
                encodeTextArea.setVisible(false);
                encodeButton.setEnabled(true);
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
                encodeTextArea.setText(Base64.decodeStr(decodeTextArea.getText()));
                return;
            }
            if (imageRadioButton.isSelected()) {
                // BufferedImage image = ImgUtil.read(new ByteArrayInputStream(Base64.decode(decodeTextArea.getText())));
                // System.out.println("image=" + image.getHeight());
                MessageUtil.showInfoMessage("敬请期待...");
            }
        });
        copyButton.addActionListener(e -> {
            if (StringUtils.isBlank(decodeTextArea.getText())) {
                return;
            }
            CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(decodeTextArea.getText()));
        });
    }

    public JPanel getContent() {
        return panel;
    }

}
