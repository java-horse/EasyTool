package easy.doc.variable.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import easy.service.TranslateService;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ThrowsVariableGeneratorImpl extends AbstractVariableGeneratorImpl {
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        List<PsiClassType> exceptionTypeList = Arrays.stream(((PsiMethod) element).getThrowsList().getReferencedTypes()).collect(Collectors.toList());
        if (exceptionTypeList.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return exceptionTypeList.stream().map(type -> "@throws " + type.getName() + StringUtils.SPACE +
                translateService.translate(type.getName())).collect(Collectors.joining(StringUtils.LF));
    }
}