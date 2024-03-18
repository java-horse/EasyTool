package easy.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.icons.EasyIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OpenModelPreviewKeyView extends DialogWrapper {

    private final TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
    private static final Logger log = Logger.getInstance(OpenModelPreviewKeyView.class);

    private JPanel panel;
    private JLabel keyLabel;
    private JTextField keyTextField;

    public OpenModelPreviewKeyView(String title, String key) {
        super(false);
        init();
        setTitle(title);
        keyTextField.setText(key);
        setOKButtonIcon(EasyIcons.ICON.COPY);
        setOKButtonText("复制");
        setCancelButtonText("关闭");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

}
