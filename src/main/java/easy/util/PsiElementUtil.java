package easy.util;

import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class PsiElementUtil {

    private PsiElementUtil() {
    }

    /**
     * 获取PSI元素名称标识符文本
     *
     * @param psiElement psi的元素
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/20 15:36
     */
    public static String getPsiElementNameIdentifierText(PsiElement psiElement) {
        if (Objects.isNull(psiElement)) {
            return StringUtils.EMPTY;
        }
        if (psiElement instanceof PsiClass psiClass) {
            PsiIdentifier nameIdentifier = psiClass.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiMethod psiMethod) {
            PsiIdentifier nameIdentifier = psiMethod.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiField psiField) {
            return psiField.getNameIdentifier().getText();
        } else if (psiElement instanceof PsiParameter psiParameter) {
            PsiIdentifier nameIdentifier = psiParameter.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else {
            return StringUtils.EMPTY;
        }
    }
}
