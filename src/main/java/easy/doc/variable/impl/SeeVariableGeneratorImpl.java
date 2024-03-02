package easy.doc.variable.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SeeVariableGeneratorImpl extends AbstractVariableGeneratorImpl {

    @Override
    public String generate(PsiElement element) {
        if (element instanceof PsiClass psiClass) {
            PsiClass superClass = psiClass.getSuperClass();
            PsiClass[] interfaces = psiClass.getInterfaces();
            List<String> superList = Lists.newArrayList();
            if (superClass != null && (!"Object".equalsIgnoreCase(superClass.getName()))) {
                    superList.add(superClass.getName());
            }
            if (interfaces.length > 0) {
                superList.addAll(Arrays.stream(interfaces).map(PsiClass::getName).collect(Collectors.toList()));
            }
            return superList.stream().map(sup -> "@see " + sup).collect(Collectors.joining(StringUtils.LF));
        } else if (element instanceof PsiMethod psiMethod) {
            StringBuilder seeString = new StringBuilder();
            PsiParameterList parameterList = psiMethod.getParameterList();
            for (PsiParameter parameter : parameterList.getParameters()) {
                if (parameter == null || parameter.getTypeElement() == null) {
                    continue;
                }
                seeString.append("@see ").append(parameter.getTypeElement().getText()).append(StringUtils.LF);
            }
            PsiTypeElement returnTypeElement = psiMethod.getReturnTypeElement();
            if (returnTypeElement != null && !"void".equals(returnTypeElement.getText())) {
                seeString.append("@see ").append(returnTypeElement.getText()).append(StringUtils.LF);
            }
            return seeString.toString();
        } else if (element instanceof PsiField psiField) {
            String type = psiField.getType().getPresentableText();
            if (Constants.BASE_TYPE_SET.contains(type)) {
                return StringUtils.EMPTY;
            }
            return "@see " + type;
        } else {
            return StringUtils.EMPTY;
        }
    }
}