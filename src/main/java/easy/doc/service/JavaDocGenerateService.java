package easy.doc.service;

import com.google.common.collect.ImmutableMap;
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

public class JavaDocGenerateService {

    private Map<Class<? extends PsiElement>, DocGenerator> docGeneratorMap = ImmutableMap.<Class<? extends PsiElement>, DocGenerator>builder()
            .put(PsiClass.class, new ClassDocGeneratorImpl())
            .put(PsiMethod.class, new MethodDocGeneratorImpl())
            .put(PsiField.class, new FieldDocGeneratorImpl())
            .build();

    public String generate(PsiElement psiElement) {
        DocGenerator docGenerator = null;
        for (Map.Entry<Class<? extends PsiElement>, DocGenerator> entry : docGeneratorMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(psiElement.getClass())) {
                docGenerator = entry.getValue();
                break;
            }
        }
        if (Objects.isNull(docGenerator)) {
            return StringUtils.EMPTY;
        }
        return docGenerator.generate(psiElement);
    }

}
