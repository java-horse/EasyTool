package easy.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Swagger二次确认弹窗
 *
 * @project: EasyChar
 * @package: easy.dialog
 * @author: mabin
 * @date: 2023/11/09 15:50:57
 */
public class SwaggerConfirmDialog extends DialogWrapper {

    private final String content;

    public SwaggerConfirmDialog(String title, String content) {
        super(true);
        this.setTitle(title);
        this.setResizable(false);
        this.content = content;
        this.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JLabel(content);
    }

}
