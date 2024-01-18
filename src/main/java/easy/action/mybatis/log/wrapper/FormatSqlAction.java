package easy.action.mybatis.log.wrapper;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
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
public class FormatSqlAction extends AnAction {

    public FormatSqlAction() {
        super("Format SQL", "Format SQL", AllIcons.Actions.PrettyPrint);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> e.getPresentation().setVisible(false));
        MyBatisLogFormatWrapper logFormatWrapper = new MyBatisLogFormatWrapper(project);
        logFormatWrapper.pack();
        logFormatWrapper.setSize(800, 420);
        logFormatWrapper.showAndGet();
    }

}
