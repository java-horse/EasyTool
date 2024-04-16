package easy.api.handle;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ArrayUtil;
import easy.api.context.ApiDocContext;
import easy.api.data.ApiDocItemData;
import easy.enums.JavaClassTypeEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.ApiDocUtil;
import easy.util.PsiClassUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author wangdingfu
 * @date 2022-09-18 17:53:46
 */
public class GenApiDocHandle {

    /**
     * 组装API文档数据
     *
     * @param apiDocContext API文档上下文
     * @param psiClass      psi类
     * @return {@link java.util.List<easy.api.data.ApiDocItemData> }
     * @author mabin
     * @date 2024/04/13 17:12
     */
    public static List<ApiDocItemData> genApiDocData(ApiDocContext apiDocContext, PsiClass psiClass) {
        List<ApiDocItemData> apiDocItemDataList = Lists.newArrayList();
        // 获取当前操作的方法
        PsiMethod targetMethod = PsiClassUtil.getTargetMethod(apiDocContext.getTargetElement());
        // 获取当前类所有方法
        PsiMethod[] psiMethods = psiClass.getMethods();
        if (ArrayUtil.isEmpty(psiMethods)) {
            return apiDocItemDataList;
        }
        // 获取当前类的真实类型
        JavaClassTypeEnum javaClassTypeEnum = JavaClassTypeEnum.get(psiClass);
        // 遍历当前类的所有方法
        int docNo = 0;
        for (PsiMethod psiMethod : psiMethods) {
            // 过滤不符合条件的方法(不需要解析的方法)
            if (!ApiDocUtil.isValidMethod(javaClassTypeEnum, psiMethod)) {
                continue;
            }
            // 判断是否当前操作方法
            if (Objects.isNull(targetMethod) || targetMethod.equals(psiMethod)) {
                ApiDocItemData apiDocItemData = new ApiDocItemData();
                // 接口序号(本地自增)
                apiDocItemData.setDocNo(psiClass.getQualifiedName() + StrUtil.DOT + (docNo++));
                // 接口全局唯一标识(MD5(moduleName + className + methodName))
                Module module = ModuleUtil.findModuleForPsiElement(psiClass);
                if (Objects.isNull(module)) {
                    continue;
                }
                apiDocItemData.setApiKey(genApiKey(module, psiClass, psiMethod));
                // 设置标题(获取标题顺序: Swagger -> JavaDoc -> className#methodName)
                apiDocItemData.setTitle(genApiTitle(psiMethod, psiClass));

            }
        }


        return null;
    }

    /**
     * 生成API的全局唯一标识
     *
     * @param module    模块
     * @param psiClass  psi类
     * @param psiMethod psi的方法
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 17:56
     */
    private static String genApiKey(Module module, PsiClass psiClass, PsiMethod psiMethod) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        String moduleId = module.getName();
        if (!ArrayUtil.isEmpty(contentRoots)) {
            moduleId = contentRoots[0].getPath();
        }
        return SecureUtil.md5(moduleId + StrUtil.DOT + psiClass.getQualifiedName() + StrUtil.DOT + psiMethod.getName());
    }

    /**
     * 生成API名称
     *
     * @param psiMethod psi的方法
     * @param psiClass  psi类
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 17:59
     */
    private static String genApiTitle(PsiMethod psiMethod, PsiClass psiClass) {
        try {
            return ApplicationManager.getApplication().runReadAction((ThrowableComputable<String, Throwable>) () -> {
                // 获取Swagger的ApiOperation注解中的value属性
                for (PsiAnnotation psiAnnotation : psiMethod.getAnnotations()) {
                    if (!StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.API_OPERATION.getClassPackage())) {
                        continue;
                    }
                    PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue("value");
                    if (Objects.isNull(psiAnnotationAttributeValue)) {
                        continue;
                    }
                    String valueText = psiAnnotationAttributeValue.getText();
                    return StringUtils.contains(valueText, "\"") ? StringUtils.replace(valueText, "\"",
                            StringUtils.EMPTY).trim() : StringUtils.trim(valueText);
                }
                // 获取JavaDoc中第一行非空注释元素即可
                PsiDocComment docComment = psiMethod.getDocComment();
                if (Objects.nonNull(docComment)) {
                    for (PsiElement descriptionElement : docComment.getDescriptionElements()) {
                        String text = descriptionElement.getText().trim();
                        if (StringUtils.isNotEmpty(text)) {
                            return text;
                        }
                    }
                }
                return psiClass.getQualifiedName() + "#" + psiMethod.getName();
            });
        } catch (Throwable e) {
            return StringUtils.EMPTY;
        }
    }

}
