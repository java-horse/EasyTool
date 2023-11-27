package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtilEx;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.ThrowableRunnable;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;
import easy.icons.EasyIcons;
import easy.service.TranslateService;
import easy.util.LanguageUtil;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
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
    private TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor) || !editor.getSelectionModel().hasSelection()) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        String translateResult = translateService.translate(selectedText);
        if (StringUtils.isBlank(translateResult)) {
            return;
        }
        String title = Boolean.TRUE.equals(translateService.keyConfigurationReminder()) ? TranslateEnum.KING_SOFT.getTranslate() : translateConfig.getTranslateChannel();
        if (!editor.isViewer() && LanguageUtil.isAllChinese(selectedText)) {
            try {
                String inputResult = Messages.showInputDialog(StringUtils.EMPTY, title, EasyIcons.ICON.TRANSLATE, translateResult, new InputValidator() {
                    @Override
                    public boolean checkInput(@NlsSafe String inputString) {
                        return StringUtils.isNotBlank(inputString) && inputString.length() <= 255;
                    }

                    @Override
                    public boolean canClose(@NlsSafe String inputString) {
                        return StringUtils.isNotBlank(inputString);
                    }
                });
                if (StringUtils.isBlank(inputResult)) {
                    return;
                }
                WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                            int start = editor.getSelectionModel().getSelectionStart();
                            EditorModificationUtilEx.insertStringAtCaret(editor, inputResult);
                            editor.getSelectionModel().setSelection(start, start + inputResult.length());
                        }
                );
            } catch (Throwable ex) {
                log.error("中英互译写入编辑器异常", ex);
            }
        } else {
            int dialogResult = Messages.showOkCancelDialog(translateResult, title, "Copy", "Cancel", EasyIcons.ICON.TRANSLATE);
            if (dialogResult == Messages.YES) {
                CopyPasteManager.getInstance().setContents(new StringSelection(translateResult));
            }
        }
        MessageUtil.sendActionDingMessage(e);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && editor.getSelectionModel().hasSelection());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
