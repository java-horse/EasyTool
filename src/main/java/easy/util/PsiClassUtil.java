package easy.util;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.ApiDocConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * PsiClass处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.util
 * @date 2024/04/13 11:49
 */
public class PsiClassUtil {


    /**
     * 获取当前光标所在的节点元素
     *
     * @param e e
     * @return {@link com.intellij.psi.PsiElement }
     * @author mabin
     * @date 2024/04/13 11:49
     */
    public static PsiElement getTargetElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }


    /**
     * 获取用户光标当前所在的类
     *
     * @param psiElement psi的元素
     * @return {@link com.intellij.psi.PsiClass }
     * @author mabin
     * @date 2024/04/13 11:50
     */
    public static PsiClass getPsiClass(PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
    }


    /**
     * 获取当前光标所在的方法
     *
     * @param psiElement psi的元素
     * @return {@link com.intellij.psi.PsiMethod }
     * @author mabin
     * @date 2024/04/13 11:50
     */
    public static PsiMethod getTargetMethod(PsiElement psiElement) {
        if (Objects.nonNull(psiElement)) {
            PsiMethod target = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
            return target instanceof SyntheticElement ? null : target;
        }
        return null;
    }


    /**
     * 查找Java文件
     *
     * @param project 项目
     * @param pkg     Java文件路径
     * @return {@link com.intellij.psi.PsiClass }
     * @author mabin
     * @date 2024/04/13 13:48
     */
    public static PsiClass findJavaFile(Project project, String pkg) {
        return JavaPsiFacade.getInstance(project).findClass(pkg, GlobalSearchScope.allScope(project));
    }


    /**
     * 获取PsiClass
     *
     * @param project        项目
     * @param psiTypeElement Psi型元件
     * @return {@link com.intellij.psi.PsiClass }
     * @author mabin
     * @date 2024/04/13 13:49
     */
    public static PsiClass getPsiClass(Project project, PsiTypeElement psiTypeElement) {
        if (Objects.isNull(project) || Objects.isNull(psiTypeElement)) {
            return null;
        }
        return JavaPsiFacade.getInstance(project).findClass(psiTypeElement.getType().getCanonicalText(), psiTypeElement.getResolveScope());
    }


    /**
     * 判断是否为一个java类（不是接口或者枚举或者注解等）
     *
     * @param psiClass psi类
     * @return boolean
     * @author mabin
     * @date 2024/04/13 13:51
     */
    public static boolean isClass(PsiClass psiClass) {
        return !(Objects.isNull(psiClass) || psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType());
    }


    /**
     * 判断PsiType是否为Void
     *
     * @param psiType psi的类型
     * @return boolean
     * @author mabin
     * @date 2024/04/13 13:51
     */
    public static boolean isVoid(PsiType psiType) {
        if (Objects.isNull(psiType)) {
            return false;
        }
        String canonicalText = psiType.getCanonicalText();
        if (CommonClassNames.JAVA_LANG_VOID.equals(canonicalText)) {
            return true;
        }
        return psiType instanceof PsiPrimitiveType && ApiDocConstants.ModifierProperty.VOID.equals(canonicalText);
    }

    /**
     * 获取方法全路径
     *
     * @param psiMethod psi的方法
     * @return {@link String }
     * @author mabin
     * @date 2024/04/13 16:15
     */
    public static String getMethodFullPath(PsiMethod psiMethod) {
        String psiClassName = ((PsiClassImpl) psiMethod.getParent()).getQualifiedName();
        String name = psiMethod.getName();
        List<String> paramTypes = Lists.newArrayList();
        PsiParameterList parameterList = psiMethod.getParameterList();
        for (PsiParameter parameter : parameterList.getParameters()) {
            paramTypes.add(parameter.getType().getCanonicalText());
        }
        return psiClassName + "#" + name + "(" + StringUtils.join(paramTypes, ",") + ")";
    }

    public static String getMethodName(PsiMethod psiMethod) {
        String psiClassName = ((PsiClassImpl) psiMethod.getParent()).getName();
        String name = psiMethod.getName();
        return psiClassName + "#" + name;
    }

}
