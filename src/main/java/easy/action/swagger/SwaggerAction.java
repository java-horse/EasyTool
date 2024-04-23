package easy.action.swagger;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.enums.SwaggerServiceEnum;
import easy.handler.ServiceHelper;
import easy.swagger.SwaggerGenerateService;
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

    public SwaggerAction(String title, Icon icon) {
        super(title, title, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile)) {
            return;
        }
        String actionText = e.getPresentation().getText();
        SwaggerGenerateService swaggerGenerateService = SwaggerServiceEnum.getSwaggerGenerateService(actionText);
        if (Objects.isNull(swaggerGenerateService)) {
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        String selectedText = editor.getSelectionModel().getSelectedText();
        // 二次弹窗确认
        if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmYesCheckBox())) {
            int confirmResult = Messages.showYesNoDialog("Confirm Swagger Generation?", Constants.PLUGIN_NAME, Messages.getQuestionIcon());
            if (MessageConstants.YES == confirmResult) {
                execSwagger(project, psiFile, psiClass, selectedText, swaggerGenerateService);
            }
        } else if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmNoCheckBox())) {
            execSwagger(project, psiFile, psiClass, selectedText, swaggerGenerateService);
        }
    }

    /**
     * 执行swagger处理
     *
     * @param project
     * @param psiFile
     * @param psiClass
     * @param selectedText
     * @param swaggerGenerateService
     * @return void
     * @author mabin
     * @date 2023/12/16 17:39
     */
    private static void execSwagger(Project project, PsiFile psiFile, PsiClass psiClass, String selectedText, SwaggerGenerateService swaggerGenerateService) {
        swaggerGenerateService.initSwaggerConfig(project, psiFile, psiClass, selectedText);
        swaggerGenerateService.doGenerate();
    }

}
