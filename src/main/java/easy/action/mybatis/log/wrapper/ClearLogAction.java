package easy.action.mybatis.log.wrapper;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 清空SQL的Action
 *
 * @project: EasyTool
 * @package: easy.action.mybatis.log.wrapper
 * @author: mabin
 * @date: 2024/01/18 10:06:17
 */
public class ClearLogAction extends AnAction {

    private final Project project;
    private final Editor editor;


    public ClearLogAction(String text, Editor editor, Project project) {
        super(text, text, AllIcons.Actions.GC);
        this.project = project;
        this.editor = editor;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Document document = this.editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(0, document.getTextLength(), StringUtils.EMPTY));
    }


}
