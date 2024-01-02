package easy.postfix.setter.action;

import com.intellij.psi.PsiClass;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.action.GenerateBaseAction;
import easy.postfix.setter.GenerateSetter;
import easy.postfix.util.PsiMethodUtil;

public abstract class GenerateAllSetterAction extends GenerateBaseAction {

    private final boolean generateDefaultVal;

    protected GenerateAllSetterAction(boolean generateDefaultVal) {
        this.generateDefaultVal = generateDefaultVal;
    }

    @Override
    protected boolean checkVariableClass(PsiClass psiClass) {
        return PsiMethodUtil.checkClassHasValidSetter(psiClass);
    }

    @Override
    protected GenerateBase buildGenerateByVar(BaseVar baseVar) {
        return new GenerateSetter(generateDefaultVal, baseVar);
    }

}
