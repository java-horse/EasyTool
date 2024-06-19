package easy.widget.annotation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class DoJacksonAnnotationService extends DoAbstractAnnotationService implements DoAnnotationService {


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
    @Override
    public void addAnnotation(Project project, PsiFile psiFile, PsiElement psiElement, String elementName) {

    }
}
