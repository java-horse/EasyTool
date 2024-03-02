package easy.doc.variable.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class PackageVariableGeneratorImpl extends AbstractVariableGeneratorImpl {

    @Override
    public String generate(PsiElement element) {
        if (Objects.isNull(element)) {
            return StringUtils.EMPTY;
        }
        if (!(element instanceof PsiClass psiClass)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.substringBeforeLast(psiClass.getQualifiedName(), ".");
    }

}
