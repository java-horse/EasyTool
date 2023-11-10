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
import com.intellij.util.ThrowableRunnable;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;
import easy.form.TranslateResultView;
import easy.service.TranslateService;
import easy.util.LanguageUtil;
import easy.util.NotificationUtil;
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
        if (StringUtils.isBlank(selectedText) || Boolean.TRUE.equals(keyConfigurationReminder())) {
            return;
        }
        String translateResult = translateService.translate(selectedText);
        if (StringUtils.isBlank(translateResult)) {
            return;
        }
        if (!editor.isViewer() && LanguageUtil.isAllChinese(selectedText)) {
            try {
                WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                            int start = editor.getSelectionModel().getSelectionStart();
                            EditorModificationUtilEx.insertStringAtCaret(editor, translateResult);
                            editor.getSelectionModel().setSelection(start, start + translateResult.length());
                        }
                );
            } catch (Throwable ex) {
                log.error("中英互译写入编辑器异常", ex);
            }
        } else {
            if (new TranslateResultView(translateConfig.getTranslateChannel(), translateResult).showAndGet()) {
                CopyPasteManager.getInstance().setContents(new StringSelection(translateResult));
            }
        }
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

    /**
     * 翻译渠道密钥配置提醒
     *
     * @param
     * @return java.lang.Boolean
     * @author mabin
     * @date 2023/9/17 16:35
     */
    private Boolean keyConfigurationReminder() {
        String translateChannel = translateConfig.getTranslateChannel();
        boolean isRemind = false;
        if (StringUtils.equals(translateChannel, TranslateEnum.BAIDU.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getAppId(), translateConfig.getAppSecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.ALIYUN.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getAccessKeyId(), translateConfig.getAccessKeySecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.YOUDAO.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getSecretId(), translateConfig.getSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.TENCENT.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getTencentSecretId(), translateConfig.getTencentSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.VOLCANO.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getVolcanoSecretId(), translateConfig.getVolcanoSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.XFYUN.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getXfAppId(), translateConfig.getXfApiKey(), translateConfig.getXfApiSecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.GOOGLE.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getGoogleSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.MICROSOFT.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getMicrosoftKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.NIU.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getNiuApiKey());
        }else if (StringUtils.equals(translateChannel, TranslateEnum.CAIYUN.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getCaiyunToken());
        }
        if (isRemind) {
            NotificationUtil.notify("请先配置翻译渠道密钥!");
        }
        return isRemind;
    }

}
