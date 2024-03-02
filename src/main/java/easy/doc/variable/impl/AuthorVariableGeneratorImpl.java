package easy.doc.variable.impl;

import com.intellij.psi.PsiElement;


public class AuthorVariableGeneratorImpl extends AbstractVariableGeneratorImpl {

    @Override
    public String generate(PsiElement element) {
        return getConfig().getAuthor();
    }

}