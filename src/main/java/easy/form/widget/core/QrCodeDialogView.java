package easy.form.widget.core;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ColorChooserService;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

    private Color foreColor = JBColor.BLACK;
    private Color backColor = JBColor.WHITE;
    private static BufferedImage qrCodeImage;
    private static String uploadFilePath;

    /**
     * 纠错级别
     */
    private static Map<String, ErrorCorrectionLevel> errorCorrectionLevelMap = Map.of(
            "低", ErrorCorrectionLevel.L,
            "中低", ErrorCorrectionLevel.M,
            "中高", ErrorCorrectionLevel.Q,
            "高", ErrorCorrectionLevel.H
    );


    public QrCodeDialogView() {
        logoFileTextField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(
                true, false, false, false, false, false)));
        sizeSpinner.setModel(new SpinnerNumberModel(300, 100, 1000, 20));
        marginSpinner.setModel(new SpinnerNumberModel(1, 0, 10, 1));
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
        areaListener(qrContentTextArea, generateButton);
        generateButton.addActionListener(e -> {

        });
        downloadButton.addActionListener(e -> {

        });
        identifyButton.addActionListener(e -> {

        });
    }

    public JPanel getContent() {
        return this.panel;
    }
}
