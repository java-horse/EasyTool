package easy.widget.annotation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public abstract class DoAbstractAnnotationService implements DoAnnotationService {

    protected Project project;
    protected PsiFile psiFile;
    protected PsiElementFactory elementFactory;

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
        if (ObjectUtils.anyNull(project, psiFile, psiElement) || StringUtils.isBlank(elementName)) {
            return;
        }
        this.project = project;
        this.psiFile = psiFile;
        this.elementFactory = JavaPsiFacade.getElementFactory(project);
        writeAnnotation(project, psiFile, psiElement, elementName);
    }

    /**
     * 写入注解
     *
     * @param project     项目
     * @param psiFile     psi文件
     * @param psiElement  psi元素
     * @param elementName 元素名称
     * @author mabin
     * @date 2024/06/21 13:43
     */
    protected abstract void writeAnnotation(Project project, PsiFile psiFile, PsiElement psiElement, String elementName);

    /**
     * 写入代码
     *
     * @param name                 姓名
     * @param qualifiedName        限定名称
     * @param annotationText       注释文本
     * @param psiModifierListOwner psi修饰符列表业主
     * @author mabin
     * @date 2024/06/19 18:18
     */
    protected void doWrite(String name, String qualifiedName, String annotationText, PsiModifierListOwner psiModifierListOwner) {
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (Objects.isNull(modifierList)) {
            return;
        }
        PsiAnnotation existAnnotation = modifierList.findAnnotation(qualifiedName);
        if (Objects.nonNull(existAnnotation)) {
            existAnnotation.delete();
        }
        PsiNameValuePair[] attributes = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner).getParameterList().getAttributes();
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = modifierList.addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 删除
     *
     * @param qualifiedName        限定名称
     * @param psiModifierListOwner psi修饰符列表业主
     * @author mabin
     * @date 2024/06/21 15:20
     */
    protected void doRemove(String qualifiedName, PsiModifierListOwner psiModifierListOwner) {
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (Objects.isNull(modifierList)) {
            return;
        }
        // 移除注解(import xxx 自行处理)
        PsiAnnotation existAnnotation = modifierList.findAnnotation(qualifiedName);
        if (Objects.nonNull(existAnnotation)) {
            existAnnotation.delete();
        }
    }

    /**
     * 导入依赖
     *
     * @param elementFactory 元件厂
     * @param file           锉
     * @param className      类名
     * @author mabin
     * @date 2024/04/23 14:37
     */
    protected void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile javaFile)) {
            return;
        }
        PsiImportList importList = javaFile.getImportList();
        if (Objects.isNull(importList)) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        if (psiClasses.length != 1) {
            return;
        }
        PsiClass waiteImportClass = psiClasses[0];
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            PsiJavaCodeReferenceElement importReference = is.getImportReference();
            if (Objects.isNull(importReference)) {
                continue;
            }
            if (StringUtils.equals(waiteImportClass.getQualifiedName(), importReference.getQualifiedName())) {
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }

}
