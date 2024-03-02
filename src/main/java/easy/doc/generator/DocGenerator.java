package easy.doc.generator;

import com.intellij.psi.PsiElement;

public interface DocGenerator {
    String generate(PsiElement psiElement);
}
