package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.ColorChooserService;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class QrCodeDialogView extends CoreCommonView {
    private JPanel panel;
    private JTextArea qrContentTextArea;
    private JLabel qrCodeLabel;
    private TextFieldWithBrowseButton logoFileTextField;
    private JSpinner sizeSpinner;
    private JButton generateButton;
    private JButton downloadButton;
    private JButton identifyButton;
    private JButton foreColorButton;
    private JButton backColorButton;
    private JSpinner marginSpinner;
    private JComboBox errorCorrectionLevelComboBox;
    private JButton clearButton;
    private JLabel qrCodeTipLabel;

    private Color foreColor = Color.BLACK;
    private Color backColor = Color.WHITE;
    private static BufferedImage qrCodeImage;
    private static String uploadFilePath;

    /**
     * 纠错级别
     */
    private static final Map<String, ErrorCorrectionLevel> ERROR_CORRECTION_LEVEL_MAP = Map.of(
            "低", ErrorCorrectionLevel.L,
            "中低", ErrorCorrectionLevel.M,
            "中高", ErrorCorrectionLevel.Q,
            "高", ErrorCorrectionLevel.H
    );


    public QrCodeDialogView() {
        logoFileTextField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(
                true, false, false, false, false, false)));
        sizeSpinner.setModel(new SpinnerNumberModel(Constants.NUM.THREE_HUNDRED, Constants.NUM.HUNDRED, Constants.NUM.ONE_THOUSAND, Constants.NUM.TWENTY));
        marginSpinner.setModel(new SpinnerNumberModel(Constants.NUM.ONE, Constants.NUM.ZERO, Constants.NUM.TEN, Constants.NUM.ONE));
        qrCodeLabel.setToolTipText("Click upload QrCode file!");
        EasyCommonUtil.customLabelTipText(qrCodeTipLabel, BundleUtil.getI18n("widget.core.qrcode.tip"), JBColor.RED);
        generateButton.setText(BundleUtil.getI18n("global.button.generate.text"));
        downloadButton.setText(BundleUtil.getI18n("global.button.download.text"));
        identifyButton.setText(BundleUtil.getI18n("global.button.identify.text"));
        clearButton.setText(BundleUtil.getI18n("global.button.clear.text"));

        foreColorButton.addActionListener(e -> {
            Color foregroundColor = ColorChooserService.getInstance().showDialog(ProjectManagerEx.getInstanceEx().getDefaultProject(), panel,
                    "Choose Foreground Color", foreColor, true, Collections.emptyList(), true);
            if (Objects.nonNull(foregroundColor)) {
                foreColor = foregroundColor;
            }
            foreColorButton.setForeground(foreColor);
            foreColorButton.setText(String.format("#%06X", (0xFFFFFF & foreColor.getRGB())));
        });
        foreColorButton.setForeground(foreColor);
        foreColorButton.setText(String.format("#%06X", (0xFFFFFF & foreColor.getRGB())));
        backColorButton.addActionListener(e -> {
            Color backgroundColor = ColorChooserService.getInstance().showDialog(ProjectManagerEx.getInstanceEx().getDefaultProject(), panel,
                    "Choose Background Color", backColor, true, Collections.emptyList(), true);
            if (Objects.nonNull(backgroundColor)) {
                backColor = backgroundColor;
            }
            backColorButton.setForeground(backColor);
            backColorButton.setText(String.format("#%06X", (0xFFFFFF & backColor.getRGB())));
        });
        backColorButton.setForeground(backColor);
        backColorButton.setText(String.format("#%06X", (0xFFFFFF & backColor.getRGB())));
        generateButton.setEnabled(false);
        areaListener(qrContentTextArea, generateButton, clearButton);

        generateButton.addActionListener(e -> {
            String qrContent = qrContentTextArea.getText();
            Integer size = Convert.toInt(sizeSpinner.getModel().getValue());
            Integer margin = Convert.toInt(marginSpinner.getModel().getValue());
            String logoPath = logoFileTextField.getText();
            ErrorCorrectionLevel errorCorrectionLevel = ERROR_CORRECTION_LEVEL_MAP.getOrDefault(String.valueOf(errorCorrectionLevelComboBox.getSelectedItem()), ErrorCorrectionLevel.L);
            if (StringUtils.isBlank(qrContent) || size < Constants.NUM.HUNDRED || margin < Constants.NUM.ZERO) {
                return;
            }
            // 二次生成确认
            if (Objects.nonNull(qrCodeImage)) {
                int confirmResult = Messages.showOkCancelDialog("Confirm to regenerate qrCode?", Constants.PLUGIN_NAME,
                        BundleUtil.getI18n("global.button.confirm.text"), BundleUtil.getI18n("global.button.cancel.text"),
                        Messages.getInformationIcon());
                if (confirmResult == MessageConstants.CANCEL) {
                    return;
                }
            }
            QrConfig config = new QrConfig(size, size);
            config.setForeColor(foreColor);
            config.setBackColor(backColor);
            config.setMargin(margin);
            config.setErrorCorrection(errorCorrectionLevel);
            if (StringUtils.isNotBlank(logoPath) || FileUtil.exist(logoPath)) {
                config.setImg(logoPath);
            }
            try {
                qrCodeImage = QrCodeUtil.generate(qrContent, config);
                qrCodeLabel.setIcon(new ImageIcon(qrCodeImage));
            } catch (Exception ex) {
                Messages.showErrorDialog(String.format("QrCode generate error: %s", ex.getMessage()), Constants.PLUGIN_NAME);
            }
        });
        downloadButton.addActionListener(e -> {
            if (Objects.isNull(qrCodeImage)) {
                return;
            }
            FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(String.format("%s QrCode Save", Constants.PLUGIN_NAME),
                    "Select a location to save the qrCode", "png");
            FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, ProjectManagerEx.getInstanceEx().getDefaultProject());
            VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(Constants.PLUGIN_NAME + "_QrCode_" + DateUtil.format(new Date(),
                    DatePattern.PURE_DATETIME_PATTERN) + StrUtil.DOT + "png");
            if (Objects.isNull(virtualFileWrapper)) {
                return;
            }
            try (FileOutputStream outputStream = new FileOutputStream(virtualFileWrapper.getFile())) {
                ImageIO.write(qrCodeImage, "png", outputStream);
                uploadFilePath = null;
                int confirmResult = Messages.showOkCancelDialog("Open qrCode download success folder?", Constants.PLUGIN_NAME,
                        BundleUtil.getI18n("global.button.confirm.text"), BundleUtil.getI18n("global.button.cancel.text"),
                        Messages.getInformationIcon());
                if (confirmResult == MessageConstants.OK) {
                    // 打开所在文件夹
                    File parentDir = virtualFileWrapper.getFile().getParentFile();
                    if (Objects.nonNull(parentDir) && parentDir.exists()) {
                        try {
                            Desktop.getDesktop().open(parentDir);
                        } catch (Exception ex) {
                            Messages.showErrorDialog(String.format("Open QrCode parent directory error: %s", ex.getMessage()), Constants.PLUGIN_NAME);
                        }
                    }
                }
            } catch (Exception ex) {
                Messages.showErrorDialog(String.format("QrCode download error: %s", ex.getMessage()), Constants.PLUGIN_NAME);
            }
        });
        qrCodeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                uploadFilePath = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
                                ProjectManagerEx.getInstance().getDefaultProject(), null))
                        .map(VirtualFile::getPath).orElse(null);
                if (StringUtils.isBlank(uploadFilePath) || !FileUtil.exist(uploadFilePath)) {
                    return;
                }
                try {
                    Image qrCodeUploadImage = ImageIO.read(new File(uploadFilePath));
                    qrCodeUploadImage.getScaledInstance(Constants.NUM.THREE_HUNDRED, Constants.NUM.THREE_HUNDRED, Image.SCALE_SMOOTH);
                    qrCodeLabel.setIcon(new ImageIcon(qrCodeUploadImage));
                    qrCodeImage = null;
                } catch (Exception ex) {
                    Messages.showErrorDialog(String.format("QrCode file upload error: %s", ex.getMessage()), Constants.PLUGIN_NAME);
                }
            }
        });
        identifyButton.addActionListener(e -> {
            if (StringUtils.isBlank(uploadFilePath) || !FileUtil.exist(uploadFilePath)) {
                return;
            }
            try {
                String decode = QrCodeUtil.decode(new File(uploadFilePath));
                qrContentTextArea.setText(decode);
                int confirmResult = Messages.showOkCancelDialog("Jump qrCode identify success link?", Constants.PLUGIN_NAME,
                        BundleUtil.getI18n("global.button.continue.text"), BundleUtil.getI18n("global.button.cancel.text"), Messages.getInformationIcon());
                if (confirmResult == MessageConstants.OK) {
                    // 一键跳转
                    EasyCommonUtil.openLink(decode);
                }
            } catch (Exception ex) {
                Messages.showErrorDialog(String.format("QrCode file identify error: %s", ex.getMessage()), Constants.PLUGIN_NAME);
            }
        });
        clearButton.addActionListener(e -> {
            int confirmResult = Messages.showOkCancelDialog("Confirm clean qrCode data?", Constants.PLUGIN_NAME,
                    BundleUtil.getI18n("global.button.confirm.text"), BundleUtil.getI18n("global.button.cancel.text"), Messages.getQuestionIcon());
            if (confirmResult == MessageConstants.OK) {
                qrContentTextArea.setText(StringUtils.EMPTY);
                qrCodeLabel.setIcon(null);
                logoFileTextField.setText(StringUtils.EMPTY);
                qrCodeImage = null;
                uploadFilePath = null;
            }
        });

    }

    public JPanel getContent() {
        return this.panel;
    }
}
