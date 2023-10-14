package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtilEx;
import com.intellij.openapi.project.Project;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * serialVersionUID快捷生成
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/10/14 15:11:50
 */
public class SerialVersionUIDAction extends AnAction {

    private static final Logger log = Logger.getInstance(SerialVersionUIDAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                        String insertStr = "private static final long serialVersionUID = " + UUID.randomUUID().getLeastSignificantBits() + "L;";
                        EditorModificationUtilEx.insertStringAtCaret(editor, insertStr);
                    }
            );
        } catch (Throwable ex) {
            log.error("serialVersionUID写入编辑器异常", ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }
}
