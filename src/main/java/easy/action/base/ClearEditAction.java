package easy.action.base;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 通用清理Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/19 10:55:56
 */
public class ClearEditAction extends AnAction {

    private final Project project;
    private final Editor editor;

    public ClearEditAction(@NotNull Editor editor) {
        super(BundleUtil.getI18n("global.button.clear.text"), BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC);
        this.project = ProjectManager.getInstance().getDefaultProject();
        this.editor = editor;
    }

    public ClearEditAction(@NotNull Editor editor, @NotNull Project project) {
        super(BundleUtil.getI18n("global.button.clear.text"), BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC);
        this.project = project;
        this.editor = editor;
    }

    public ClearEditAction(String text, @NotNull Editor editor, @NotNull Project project) {
        super(text, text, AllIcons.Actions.GC);
        this.project = project;
        this.editor = editor;
    }

    public ClearEditAction(String text, String desc, @NotNull Editor editor, @NotNull Project project) {
        super(text, desc, AllIcons.Actions.GC);
        this.project = project;
        this.editor = editor;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(0, document.getTextLength(), StringUtils.EMPTY));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(StringUtils.isNotEmpty(editor.getDocument().getText()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
