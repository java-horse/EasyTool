package easy.action.base;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import easy.util.BundleUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 通用清理Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/19 10:58:52
 */
public class ClearConsoleAction extends AnAction {

    private final ConsoleViewImpl consoleView;

    public ClearConsoleAction(@NotNull ConsoleViewImpl consoleView) {
        super(BundleUtil.getI18n("global.button.clear.text"), BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC);
        this.consoleView = consoleView;
    }

    public ClearConsoleAction(String text, @NotNull ConsoleViewImpl consoleView) {
        super(text, text, AllIcons.Actions.GC);
        this.consoleView = consoleView;
    }

    public ClearConsoleAction(String text, String desc, @NotNull ConsoleViewImpl consoleView) {
        super(text, desc, AllIcons.Actions.GC);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        consoleView.clear();
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
