package easy.postfix.getter.action;

import com.intellij.psi.PsiClass;
import easy.base.Constants;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.action.GenerateBaseAction;
import easy.postfix.getter.GenerateGetter;
import easy.postfix.util.PsiMethodUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateAllGetterAction extends GenerateBaseAction {

    @Override
    protected boolean checkVariableClass(PsiClass psiClass) {
        return PsiMethodUtil.checkClassHasValidGetter(psiClass);
    }

    @Override
    protected GenerateBase buildGenerateByVar(BaseVar baseVar) {
        return new GenerateGetter(baseVar);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_GETTER;
    }

    @NotNull
    @Override
    public String getText() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_GETTER;
    }

}
