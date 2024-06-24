package easy.doc.variable.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.enums.JavaDocMethodReturnTypeEnum;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import org.apache.commons.lang3.StringUtils;

public class ReturnVariableGeneratorImpl extends AbstractVariableGeneratorImpl {
    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod psiMethod)) {
            return StringUtils.EMPTY;
        }
        if (psiMethod.getReturnTypeElement() == null) {
            return StringUtils.EMPTY;
        }
        String returnName = psiMethod.getReturnTypeElement().getType().getCanonicalText();
        if (Constants.BASE_TYPE_SET.contains(returnName)) {
            return "@return " + returnName;
        } else if ("void".equalsIgnoreCase(returnName)) {
            return StringUtils.EMPTY;
        } else {
            JavaDocConfig javaDocConfig = getConfig();
            if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.CODE.getType())) {
                return "@return {@code " + returnName + " }";
            } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.LINK.getType())) {
                return String.format("@return {@link %s}", returnName);
            } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.COMMENT.getType())) {
                return "@return " + translateService.translate(returnName);
            }
            return String.format("@return {@link %s}", returnName);
        }
    }

}