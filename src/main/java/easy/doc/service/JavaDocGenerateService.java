package easy.doc.service;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import easy.doc.generator.DocGenerator;
import easy.doc.generator.impl.ClassDocGeneratorImpl;
import easy.doc.generator.impl.FieldDocGeneratorImpl;
import easy.doc.generator.impl.MethodDocGeneratorImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class JavaDocGenerateService {

    private final Map<Class<? extends PsiElement>, DocGenerator> docGeneratorMap = ImmutableMap.<Class<? extends PsiElement>, DocGenerator>builder()
            .put(PsiClass.class, new ClassDocGeneratorImpl())
            .put(PsiMethod.class, new MethodDocGeneratorImpl())
            .put(PsiField.class, new FieldDocGeneratorImpl())
            .build();

    /**
     * 生成JavaDoc文本
     *
     * @param psiElement psi元素
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/08/01 10:04
     */
    public String generate(PsiElement psiElement) {
        AtomicReference<String> genJavaDoc = new AtomicReference<>(StringUtils.EMPTY);
        ApplicationManagerEx.getApplicationEx().runReadAction(() -> {
            DocGenerator docGenerator = null;
            for (Map.Entry<Class<? extends PsiElement>, DocGenerator> entry : docGeneratorMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(psiElement.getClass())) {
                    docGenerator = entry.getValue();
                    break;
                }
            }
            if (Objects.nonNull(docGenerator)) {
                genJavaDoc.set(docGenerator.generate(psiElement));
            }
        });
        return genJavaDoc.get();
    }

}
