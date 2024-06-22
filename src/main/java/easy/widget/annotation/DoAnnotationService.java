package easy.widget.annotation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public interface DoAnnotationService {


    /**
     * 添加注解
     *
     * @param project     项目
     * @param psiFile     psi文件
     * @param psiElement  psi元素
     * @param elementName 元素名称
     * @author mabin
     * @date 2024/06/19 17:43
     */
    void addAnnotation(Project project, PsiFile psiFile, PsiElement psiElement, String elementName);

    /**
     * 删除注解
     *
     * @param psiElement psi元素
     * @author mabin
     * @date 2024/06/21 15:26
     */
    void removeAnnotation(PsiElement psiElement);

}
