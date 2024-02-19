package easy.action;

import com.google.common.base.CaseFormat;
import com.intellij.codeInsight.hint.HintManager;
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
import easy.util.LanguageUtil;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 变量格式转换Action
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/09/28 16:10:10
 */
public class CamelCaseAction extends AnAction {

    private static final Logger log = Logger.getInstance(CamelCaseAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor) || !editor.getSelectionModel().hasSelection()) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        String convertText;
        if (!selectedText.equals(selectedText.toLowerCase()) && !selectedText.equals(selectedText.toUpperCase()) && selectedText.indexOf('_') < 0) {
            convertText = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, selectedText);
        } else if (selectedText.indexOf('_') >= 0) {
            convertText = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, selectedText);
        } else {
            HintManager.getInstance().showErrorHint(editor, "Cannot CamelCase format convert");
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                        int start = editor.getSelectionModel().getSelectionStart();
                        EditorModificationUtilEx.insertStringAtCaret(editor, convertText);
                        editor.getSelectionModel().setSelection(start, start + convertText.length());
                    }
            );
            MessageUtil.sendActionDingMessage(e);
        } catch (Throwable ex) {
            log.error("CamelCase转换写入编辑器异常", ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && editor.getDocument().isWritable()
                && editor.getSelectionModel().hasSelection()
                && !LanguageUtil.isContainsChinese(editor.getSelectionModel().getSelectedText()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
