package easy.action.convert;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.icons.EasyIcons;
import easy.mybatis.log.ui.MyBatisLogFormatWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 格式化SQL弹窗Action
 *
 * @project: EasyTool
 * @package: easy.action.mybatis.log.wrapper
 * @author: mabin
 * @date: 2024/01/18 10:45:28
 */
public class Log2SqlAction extends AnAction {

    public Log2SqlAction() {
        super("Log2Sql", "Log2Sql", EasyIcons.ICON.SQL);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> e.getPresentation().setVisible(false));
        MyBatisLogFormatWrapper logFormatWrapper = new MyBatisLogFormatWrapper(project);
        logFormatWrapper.pack();
        logFormatWrapper.setSize(800, 450);
        logFormatWrapper.showAndGet();
    }

}
