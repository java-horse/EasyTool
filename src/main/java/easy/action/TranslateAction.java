package easy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ThrowableRunnable;
import easy.service.TranslateService;
import easy.util.LanguageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 翻译Action
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/04 21:29
 **/

public class TranslateAction extends AnAction {

    private static final Logger log = Logger.getInstance(TranslateAction.class);
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);

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
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isNotBlank(selectedText)) {
            // 中文直接翻译且替换, 否则显示翻译结果
            String translateResult = translateService.translate(selectedText);
            if (StringUtils.isBlank(translateResult)) {
                return;
            }
            if (LanguageUtil.isAllChinese(selectedText)) {
                try {
                    WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                                int start = editor.getSelectionModel().getSelectionStart();
                                EditorModificationUtil.insertStringAtCaret(editor, translateResult);
                                editor.getSelectionModel().setSelection(start, start + translateResult.length());
                            }
                    );
                } catch (Throwable ex) {
                    log.error("中英互译写入编辑器异常", ex);
                }
            } else {

            }
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor));
    }

}
