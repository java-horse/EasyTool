package easy.doc.variable.impl;

import com.google.common.base.Joiner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DocVariableGeneratorImpl extends AbstractVariableGeneratorImpl {

    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    @Override
    public String generate(PsiElement element) {
        if (element instanceof PsiNamedElement psiNamedElement) {
            PsiDocComment docComment = ((PsiJavaDocumentedElement) element).getDocComment();
            if (docComment != null) {
                PsiElement[] descriptionElements = docComment.getDescriptionElements();
                List<String> descTextList = Arrays.stream(descriptionElements).map(PsiElement::getText).filter(StringUtils::isNotBlank).map(StringUtils::trim).collect(Collectors.toList());
                String result = Joiner.on(StringUtils.EMPTY).skipNulls().join(descTextList);
                return StringUtils.isNotBlank(result) ? result : translateService.translate(psiNamedElement.getName());
            }
            return translateService.translate(psiNamedElement.getName());
        }
        return StringUtils.EMPTY;
    }

}
