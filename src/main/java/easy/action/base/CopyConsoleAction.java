package easy.action.base;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * 通用复制Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/19 10:53:28
 */
public class CopyConsoleAction extends AnAction {

    private final ConsoleViewImpl consoleView;

    public CopyConsoleAction(@NotNull ConsoleViewImpl consoleView) {
        super(BundleUtil.getI18n("global.button.copy.text"), BundleUtil.getI18n("global.button.copy.text"), AllIcons.Actions.Copy);
        this.consoleView = consoleView;
    }

    public CopyConsoleAction(String text, @NotNull ConsoleViewImpl consoleView) {
        super(text, text, AllIcons.Actions.Copy);
        this.consoleView = consoleView;
    }

    public CopyConsoleAction(String text, String desc, @NotNull ConsoleViewImpl consoleView) {
        super(text, desc, AllIcons.Actions.Copy);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String text = consoleView.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        CopyPasteManager.getInstance().setContents(new StringSelection(text));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enabled = consoleView.getContentSize() > 0;
        if (!enabled) {
            enabled = e.getData(LangDataKeys.CONSOLE_VIEW) != null;
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (editor != null && editor.getDocument().getTextLength() == 0) {
                enabled = false;
            }
        }
        e.getPresentation().setEnabled(enabled);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
