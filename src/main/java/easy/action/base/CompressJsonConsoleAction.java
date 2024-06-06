package easy.action.base;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import easy.util.BundleUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 压缩JSON字符串的Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/25 16:27:58
 */
public class CompressJsonConsoleAction extends AnAction {

    private final ConsoleViewImpl consoleView;

    public CompressJsonConsoleAction(@NotNull ConsoleViewImpl consoleView) {
        super(BundleUtil.getI18n("global.button.compress.text"), BundleUtil.getI18n("global.button.compress.text"), AllIcons.Actions.Collapseall);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String text = consoleView.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        String compressJson = JsonUtil.toJson(JsonUtil.fromObject(text));
        consoleView.clear();
        consoleView.print(compressJson, ConsoleViewContentType.USER_INPUT);
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
        return super.getActionUpdateThread();
    }


}
