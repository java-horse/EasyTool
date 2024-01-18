package easy.action.mybatis.log.wrapper;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.ide.CopyPasteManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * 复制格式化SQL的Action
 *
 * @project: EasyTool
 * @package: easy.action.mybatis.log.wrapper
 * @author: mabin
 * @date: 2024/01/18 10:20:28
 */
public class CopySqlAction extends AnAction {

    private final ConsoleViewImpl consoleView;

    public CopySqlAction(String text, @NotNull ConsoleViewImpl consoleView) {
        super(text, text, AllIcons.Actions.Copy);
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

}
