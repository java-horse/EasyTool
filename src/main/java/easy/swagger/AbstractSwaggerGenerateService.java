package easy.swagger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import easy.handler.ServiceHelper;
import easy.translate.TranslateService;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSwaggerGenerateService implements SwaggerGenerateService {

    protected TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    protected Project project;
    protected PsiFile psiFile;
    protected PsiClass psiClass;
    protected PsiElementFactory elementFactory;
    protected String selectionText;
    protected Boolean isController;

    /**
     * 初始Swagger配置
     *
     * @param project       项目
     * @param psiFile       psi文件
     * @param psiClass      psi级
     * @param selectionText 选择文本
     * @author mabin
     * @date 2024/04/22 15:14
     */
    @Override
    public void initSwaggerConfig(Project project, PsiFile psiFile, PsiClass psiClass, String selectionText) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.selectionText = selectionText;
        this.elementFactory = JavaPsiFacade.getElementFactory(project);
        this.isController = PsiElementUtil.isController(psiClass);
    }

    /**
     * Swagger注解自动生成
     *
     * @author mabin
     * @date 2024/04/22 14:44
     */
    @Override
    public void doGenerate() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (StringUtils.isNotBlank(selectionText)) {
                genSelection(psiClass, StringUtils.trim(selectionText));
                return;
            }
            genClassAnnotation(psiClass);
            if (isController) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod psiMethod : methods) {
                    genMethodAnnotation(psiMethod);
                }
                return;
            }
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            for (PsiClass innerClass : innerClasses) {
                genClassAnnotation(innerClass);
                for (PsiField psiField : innerClass.getAllFields()) {
                    genFieldAnnotation(psiField);
                }
            }
            for (PsiField psiField : psiClass.getAllFields()) {
                genFieldAnnotation(psiField);
            }
        });
    }

    /**
     * 生成选中文本指定区域注解
     *
     * @param psiClass      psi级
     * @param selectionText 选择文本
     * @author mabin
     * @date 2024/04/22 16:15
     */
    private void genSelection(PsiClass psiClass, String selectionText) {
        Map<String, PsiClass> psiClassMap = Maps.newHashMap();
        psiClassMap.put(PsiElementUtil.getPsiElementNameIdentifierText(psiClass), psiClass);
        if (!isController) {
            for (PsiClass innerClass : psiClass.getInnerClasses()) {
                psiClassMap.put(PsiElementUtil.getPsiElementNameIdentifierText(innerClass), innerClass);
            }
        }
        PsiClass selectPsiClass = psiClassMap.get(selectionText);
        if (Objects.nonNull(selectPsiClass)) {
            genClassAnnotation(selectPsiClass);
            return;
        }
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (StringUtils.equals(selectionText, PsiElementUtil.getPsiElementNameIdentifierText(psiMethod))) {
                genMethodAnnotation(psiMethod);
                return;
            }
        }
        List<PsiField> psiFieldList = Lists.newArrayList(psiClass.getAllFields());
        Arrays.stream(psiClass.getInnerClasses()).forEach(innerClass -> psiFieldList.addAll(Arrays.asList(innerClass.getAllFields())));
        for (PsiField psiField : psiFieldList) {
            if (StringUtils.equals(selectionText, PsiElementUtil.getPsiElementNameIdentifierText(psiField))) {
                genFieldAnnotation(psiField);
                return;
            }
        }
    }

    /**
     * 写入注解文本
     *
     * @param name                 名称
     * @param qualifiedName        限定名称
     * @param annotationText       注释文本
     * @param psiModifierListOwner psi修改器列表所有者
     * @author mabin
     * @date 2024/04/23 14:34
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
        PsiNameValuePair[] attributes = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner)
                .getParameterList().getAttributes();
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = modifierList.addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
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
    private void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile javaFile)) {
            return;
        }
        PsiImportList importList = javaFile.getImportList();
        if (Objects.isNull(importList)) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        // 待导入类有多个同名类或没有时 让用户自行处理
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

    /**
     * 生成类注解
     *
     * @param psiClass psi级
     * @author mabin
     * @date 2024/04/22 16:11
     */
    protected abstract void genClassAnnotation(PsiClass psiClass);

    /**
     * 生成方法注解
     *
     * @param psiMethod psi法
     * @author mabin
     * @date 2024/04/22 16:11
     */
    protected abstract void genMethodAnnotation(PsiMethod psiMethod);

    /**
     * 生成字段注解
     *
     * @param psiField psi场
     * @author mabin
     * @date 2024/04/22 16:12
     */
    protected abstract void genFieldAnnotation(PsiField psiField);

}
