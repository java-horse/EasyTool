package easy.swagger;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import easy.enums.SwaggerServiceEnum;
import org.jetbrains.annotations.Nullable;

public interface SwaggerGenerateService {

    /**
     * 初始Swagger配置
     *
     * @param project               项目
     * @param psiFile               psi文件
     * @param psiClass              psi级
     * @param selectionText         选择文本
     * @param swaggerAnnotationEnum
     * @author mabin
     * @date 2024/04/22 15:14
     */
    void initSwaggerConfig(Project project, PsiFile psiFile, PsiClass psiClass, String selectionText,
                           SwaggerServiceEnum swaggerAnnotationEnum, @Nullable PsiElement psiElement);

    /**
     * Swagger注解自动生成
     *
     * @author mabin
     * @date 2024/04/22 14:44
     */
    void doGenerate();

}
