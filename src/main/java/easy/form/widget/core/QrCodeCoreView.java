package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intellij.icons.AllIcons;
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
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class QrCodeCoreView extends CoreCommonView {
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
    private JComboBox codeTypeComboBox;

    private Color foreColor = Color.BLACK;
    private Color backColor = Color.WHITE;
    private static BufferedImage qrCodeImage;
    private static BufferedImage uploadQrCodeImage;

    private static final String[] IMAGE_TYPE = {"png", "jpg", "jpeg", "gif", "bmp"};

    /**
     * 纠错级别
     */
    private static final Map<String, ErrorCorrectionLevel> ERROR_CORRECTION_LEVEL_MAP = Map.of(
            "低", ErrorCorrectionLevel.L,
            "中低", ErrorCorrectionLevel.M,
            "中高", ErrorCorrectionLevel.Q,
            "高", ErrorCorrectionLevel.H
    );

    /**
     * 条码格式
     */
    private static final Map<String, BarcodeFormat> BARCODE_FORMAT_MAP = Map.of(
            "二维码", BarcodeFormat.QR_CODE,
            "条形码（39）", BarcodeFormat.CODE_39,
            "条形码（93）", BarcodeFormat.CODE_93,
            "条形码（128）", BarcodeFormat.CODE_128
    );

    public QrCodeCoreView() {
        logoFileTextField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withFileFilter(file -> StrUtil.endWithAnyIgnoreCase(file.getName(), ".jpg", ".png"))));
        sizeSpinner.setModel(new SpinnerNumberModel(Constants.NUM.THREE_HUNDRED, Constants.NUM.HUNDRED, Constants.NUM.ONE_THOUSAND, Constants.NUM.TWENTY));
        marginSpinner.setModel(new SpinnerNumberModel(Constants.NUM.ONE, Constants.NUM.ZERO, Constants.NUM.TEN, Constants.NUM.ONE));
        qrCodeLabel.setToolTipText("Click upload QrCode file!");
        EasyCommonUtil.customLabelTipText(qrCodeTipLabel, BundleUtil.getI18n("widget.core.qrcode.tip"), JBColor.RED);
        generateButton.setIcon(AllIcons.General.ArrowDown);
        generateButton.setText(BundleUtil.getI18n("global.button.generate.text"));
        downloadButton.setIcon(AllIcons.Actions.Download);
        downloadButton.setText(BundleUtil.getI18n("global.button.download.text"));
        identifyButton.setIcon(AllIcons.General.ArrowUp);
        identifyButton.setText(BundleUtil.getI18n("global.button.identify.text"));
        clearButton.setIcon(AllIcons.Actions.GC);
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
                int confirmResult = MessageUtil.showOkCancelDialog("Confirm to regenerate qrCode?", Messages.getInformationIcon());
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
                BarcodeFormat barcodeFormat = BARCODE_FORMAT_MAP.getOrDefault(String.valueOf(codeTypeComboBox.getSelectedItem()), BarcodeFormat.QR_CODE);
                qrCodeImage = QrCodeUtil.generate(qrContent, barcodeFormat, config);
                qrCodeLabel.setIcon(new ImageIcon(qrCodeImage));
            } catch (Exception ex) {
                MessageUtil.showErrorDialog(String.format("QrCode generate error: %s", ex.getMessage()));
            }
        });
        downloadButton.addActionListener(e -> {
            if (Objects.isNull(qrCodeImage)) {
                MessageUtil.showInfoMessage("qrCode is not exist!");
                return;
            }
            FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(String.format("%s QrCode Save", Constants.PLUGIN_NAME),
                    "Select a location to save the qrCode", IMAGE_TYPE);
            FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, ProjectManagerEx.getInstanceEx().getDefaultProject());
            VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(Constants.PLUGIN_NAME + "_QrCode_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
            if (Objects.isNull(virtualFileWrapper)) {
                return;
            }
            File file = virtualFileWrapper.getFile();
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                ImgUtil.write(qrCodeImage, FileUtil.extName(file), outputStream);
                uploadQrCodeImage = null;
                int confirmResult = MessageUtil.showOkCancelDialog("Open qrCode download success folder?", Messages.getInformationIcon());
                if (confirmResult == MessageConstants.OK) {
                    // 打开所在文件夹
                    File parentDir = file.getParentFile();
                    if (Objects.nonNull(parentDir) && parentDir.exists()) {
                        try {
                            Desktop.getDesktop().open(parentDir);
                        } catch (Exception ex) {
                            MessageUtil.showErrorDialog(String.format("Open QrCode parent directory error: %s", ex.getMessage()));
                        }
                    }
                }
            } catch (Exception ex) {
                MessageUtil.showErrorDialog(String.format("QrCode download error: %s", ex.getMessage()));
            }
        });
        qrCodeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    int way = MessageUtil.showOkCancelDialog("请选择上传方式?", "打开文件夹", "复制粘贴板", Messages.getInformationIcon());
                    BufferedImage bufferedImage = null;
                    if (way == MessageConstants.OK) {
                        // 文件夹上传
                        String uploadFilePath = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
                                        ProjectManagerEx.getInstance().getDefaultProject(), null))
                                .map(VirtualFile::getPath).orElse(null);
                        if (StringUtils.isBlank(uploadFilePath) || !FileUtil.exist(uploadFilePath)) {
                            return;
                        }
                        bufferedImage = ImageIO.read(new File(uploadFilePath));
                    } else if (way == MessageConstants.CANCEL) {
                        // 粘贴板读取
                        Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
                        if (cp.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                            bufferedImage = (BufferedImage) cp.getData(DataFlavor.imageFlavor);
                            qrCodeLabel.setIcon(new ImageIcon(bufferedImage));
                        }
                    }
                    if (Objects.isNull(bufferedImage)) {
                        return;
                    }
                    bufferedImage.getScaledInstance(Constants.NUM.THREE_HUNDRED, Constants.NUM.THREE_HUNDRED, Image.SCALE_SMOOTH);
                    qrCodeLabel.setIcon(new ImageIcon(bufferedImage));
                    uploadQrCodeImage = bufferedImage;
                    qrCodeImage = null;
                } catch (Exception ex) {
                    MessageUtil.showErrorDialog(String.format("QrCode file upload error: %s", ex.getMessage()));
                }
            }
        });
        identifyButton.addActionListener(e -> {
            if (Objects.isNull(uploadQrCodeImage)) {
                MessageUtil.showErrorDialog("QrCode file is not upload!");
                return;
            }
            try {
                String decode = QrCodeUtil.decode(uploadQrCodeImage);
                if (StringUtils.isBlank(decode)) {
                    MessageUtil.showErrorDialog("QrCode identify error");
                    return;
                }
                qrContentTextArea.setText(decode);
                if (StrUtil.startWithAnyIgnoreCase(decode, Constants.HTTPS, Constants.HTTP)
                        && MessageUtil.showOkCancelDialog("Jump qrCode identify success link?") == MessageConstants.OK) {
                    EasyCommonUtil.openLink(decode);
                }
            } catch (Exception ex) {
                MessageUtil.showErrorDialog(String.format("QrCode file identify error: %s", ex.getMessage()));
            }
        });
        clearButton.addActionListener(e -> {
            if (ObjectUtils.anyNotNull(qrContentTextArea.getText(), qrCodeLabel.getIcon(), logoFileTextField.getText(), qrCodeImage, uploadQrCodeImage)) {
                if (MessageUtil.showOkCancelDialog("Confirm clean qrCode data?") == MessageConstants.OK) {
                    qrContentTextArea.setText(StringUtils.EMPTY);
                    qrCodeLabel.setIcon(null);
                    logoFileTextField.setText(StringUtils.EMPTY);
                    qrCodeImage = null;
                    uploadQrCodeImage = null;
                }
            }
        });
    }

    public JPanel getContent() {
        return this.panel;
    }
}
