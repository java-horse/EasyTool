package easy.form;

import com.intellij.openapi.ui.DialogWrapper;
import easy.icons.EasyIcons;
import easy.util.BundleUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OpenModelPreviewKeyView extends DialogWrapper {

    private JPanel panel;
    private JLabel keyLabel;
    private JTextField keyTextField;

    public OpenModelPreviewKeyView(String title, String key) {
        super(false);
        init();
        setTitle(title);
        keyTextField.setText(key);
        setOKButtonIcon(EasyIcons.ICON.COPY);
        setOKButtonText(BundleUtil.getI18n("global.button.copy.text"));
        setCancelButtonText(BundleUtil.getI18n("global.button.cancel.text"));
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 500), Math.min(size.height, 200));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

}
