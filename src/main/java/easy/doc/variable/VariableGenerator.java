package easy.doc.variable;

import com.intellij.psi.PsiElement;
import easy.config.doc.JavaDocConfig;

public interface VariableGenerator {

    String generate(PsiElement element);


    JavaDocConfig getConfig();
}
