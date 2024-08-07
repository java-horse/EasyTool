package easy.action;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtilEx;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThrowableRunnable;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;
import easy.helper.ServiceHelper;
import easy.icons.EasyIcons;
import easy.translate.TranslateService;
import easy.util.BundleUtil;
import easy.util.LanguageUtil;
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
    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);
    private TranslateConfig translateConfig = ServiceHelper.getService(TranslateConfigComponent.class).getState();
    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile) || !editor.getSelectionModel().hasSelection()) {
            return;
        }
        if (!psiFile.isWritable()) {
            HintManager.getInstance().showErrorHint(editor, "This is read-only source file!");
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
        String title = Boolean.TRUE.equals(translateService.keyConfigurationReminder()) ? TranslateEnum.KING_SOFT.getTranslate()
                : StringUtils.equals(translateConfig.getTranslateChannel(), TranslateEnum.OPEN_BIG_MODEL.getTranslate()) ? translateConfig.getOpenModelChannel() : translateConfig.getTranslateChannel();
        if (LanguageUtil.isAllChinese(selectedText) && editor.getDocument().isWritable()) {
            try {
                String inputResult = Boolean.TRUE.equals(commonConfig.getTranslateConfirmInputModelYesCheckBox()) ? Messages.showInputDialog(StringUtils.EMPTY, title, EasyIcons.ICON.TRANSLATE, translateResult,
                        new InputValidator() {
                            @Override
                            public boolean checkInput(@NlsSafe String inputString) {
                                return StringUtils.isNotBlank(inputString) && inputString.length() <= 255;
                            }

                            @Override
                            public boolean canClose(@NlsSafe String inputString) {
                                return StringUtils.isNotBlank(inputString);
                            }
                        }) : translateResult;
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
                log.error("Chinese English translation writing editor exception!", ex);
            }
        } else {
            int dialogResult = Messages.showOkCancelDialog(translateResult, title, BundleUtil.getI18n("global.button.copy.text"),
                    BundleUtil.getI18n("global.button.cancel.text"), EasyIcons.ICON.TRANSLATE);
            if (dialogResult == Messages.YES) {
                CopyPasteManager.getInstance().setContents(new StringSelection(translateResult));
            }
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && editor.getSelectionModel().hasSelection() && Objects.nonNull(psiFile) && psiFile.isWritable());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
