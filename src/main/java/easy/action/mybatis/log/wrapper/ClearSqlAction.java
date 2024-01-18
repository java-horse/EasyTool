package easy.action.mybatis.log.wrapper;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 清理SQL日志的Action
 *
 * @project: EasyTool
 * @package: easy.action.mybatis.log.wrapper
 * @author: mabin
 * @date: 2024/01/18 14:42:59
 */
public class ClearSqlAction extends AnAction {

    private final ConsoleViewImpl consoleView;

    public ClearSqlAction(String text, @NotNull ConsoleViewImpl consoleView) {
        super(text, text, AllIcons.Actions.GC);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        consoleView.clear();
    }


}
