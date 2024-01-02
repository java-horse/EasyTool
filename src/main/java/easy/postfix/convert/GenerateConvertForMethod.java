package easy.postfix.convert;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import easy.postfix.base.BaseVar;
import easy.postfix.util.PsiTypeUtil;

import java.util.HashSet;

public class GenerateConvertForMethod extends GenerateConvert {

    private PsiMethod psiMethod;

    public GenerateConvertForMethod(BaseVar varForSet, BaseVar varForGet, PsiMethod psiMethod) {
        super(varForSet, varForGet);
        this.psiMethod = psiMethod;
    }

    @Override
    protected void beforeAppend(StringBuilder builder, String splitText, HashSet<String> newImportList) {
        if (baseVar == null || varForGet == null) {
            return;
        }
        String varName = baseVar.getVarName();
        PsiType psiType = baseVar.getVarType();
        String varForGetVarName = varForGet.getVarName();
        PsiClass contextClass = psiMethod.getContainingClass();
        String className = PsiTypeUtil.getClassName(psiType, contextClass);
        if (className == null) {
            className = psiType.getPresentableText();
        }
        builder.append(splitText);
        builder.append("if (").append(varForGetVarName).append(" == null) {").append(splitText);
        builder.append("    return null;").append(splitText);
        builder.append("}").append(splitText);
        builder.append(className).append(" ").append(varName).append(" = ").append("new ").append(className).append("();").append(splitText);
    }

    @Override
    protected void afterAppend(StringBuilder builder, String splitText, HashSet<String> newImportList) {
        if (baseVar == null) {
            return;
        }
        String varName = baseVar.getVarName();
        builder.append("return ").append(varName).append(";");
    }
}
