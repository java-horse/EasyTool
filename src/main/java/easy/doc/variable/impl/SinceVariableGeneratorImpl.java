package easy.doc.variable.impl;

import com.intellij.psi.PsiElement;

public class SinceVariableGeneratorImpl extends AbstractVariableGeneratorImpl {

    @Override
    public String generate(PsiElement element) {
        return "1.0.0";
    }

}