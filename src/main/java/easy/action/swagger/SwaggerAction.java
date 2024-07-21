package easy.action.swagger;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.enums.SwaggerServiceEnum;
import easy.helper.ServiceHelper;
import easy.ui.SwaggerViewDialog;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

/**
 * Swagger工具Action
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/11/04 11:14:11
 */
public class SwaggerAction extends AnAction {

    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    public SwaggerAction(String title, Icon icon, KeyboardShortcut keyboardShortcut) {
        super(title, title, icon);
        // 设置全局快捷键
        CustomShortcutSet shortcutSet = new CustomShortcutSet(keyboardShortcut);
        registerCustomShortcutSet(shortcutSet, null);
        String actionId = Constants.PLUGIN_NAME + DynamicSwaggerActionGroup.class.getSimpleName() + title;
        ActionManager actionManager = ActionManager.getInstance();
        if (Objects.isNull(actionManager.getAction(actionId))) {
            actionManager.registerAction(actionId, this);
        }
        KeymapManager.getInstance().getActiveKeymap().addShortcut(actionId, shortcutSet.getShortcuts()[0]);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile) || Objects.isNull(psiClass)) {
            return;
        }
        if (!psiFile.isWritable()) {
            HintManager.getInstance().showErrorHint(editor, "This is read-only source file!");
            return;
        }
        SwaggerServiceEnum swaggerAnnotationEnum = SwaggerServiceEnum.getSwaggerAnnotationEnum(e.getPresentation().getText());
        if (Objects.isNull(swaggerAnnotationEnum)) {
            return;
        }
        if (Objects.equals(swaggerAnnotationEnum, SwaggerServiceEnum.SWAGGER_VIEW)) {
            new SwaggerViewDialog(project, psiClass, psiFile).show();
            return;
        }
        // 二次弹窗确认
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            // 尝试获取光标所在PsiElement属性名称
            PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
            if (Objects.nonNull(psiElement)) {
                selectedText = EasyCommonUtil.getPsiElementName(psiElement);
            }
            if (StringUtils.isBlank(selectedText)) {
                HintManager.getInstance().showErrorHint(editor, "The mouse cursor should select the class name, method name, filed name");
                return;
            }
        }
        if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmYesCheckBox())) {
            int confirmResult = MessageUtil.showYesNoDialog(BundleUtil.getI18n("swagger.confirm.generate.text"));
            if (MessageConstants.YES == confirmResult) {
                execSwagger(project, psiFile, psiClass, selectedText, swaggerAnnotationEnum);
            }
        } else if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmNoCheckBox())) {
            execSwagger(project, psiFile, psiClass, selectedText, swaggerAnnotationEnum);
        }
    }

    /**
     * 执行swagger处理
     *
     * @param project
     * @param psiFile
     * @param psiClass
     * @param selectedText
     * @param swaggerAnnotationEnum
     * @return void
     * @author mabin
     * @date 2023/12/16 17:39
     */
    private static void execSwagger(Project project, PsiFile psiFile, PsiClass psiClass, String selectedText, SwaggerServiceEnum swaggerAnnotationEnum) {
        swaggerAnnotationEnum.getSwaggerGenerateService().initSwaggerConfig(project, psiFile, psiClass, selectedText, swaggerAnnotationEnum);
        swaggerAnnotationEnum.getSwaggerGenerateService().doGenerate();
    }

}
