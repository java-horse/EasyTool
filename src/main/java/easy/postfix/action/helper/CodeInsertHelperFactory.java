package easy.postfix.action.helper;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

public class CodeInsertHelperFactory {

    private CodeInsertHelperFactory() {
    }

    private static final List<CodeInsertHelper<?>> HELPER_LIST = new ArrayList<>(32);

    static {
        addHelper(new PsiLocalCodeInsertHelper());
        addHelper(new PsiMethodCodeInsertHelper());
        addHelper(new PsiForeachCodeInsertHelper());
    }

    public static void addHelper(CodeInsertHelper<? extends PsiElement> helper) {
        HELPER_LIST.add(helper);
    }

    public static <T> CodeInsertHelper<T> getHelper(PsiElement psiElement) {
        for (CodeInsertHelper<?> codeInsertHelper : HELPER_LIST) {
            if (codeInsertHelper.supportElement(psiElement)) {
                return (CodeInsertHelper<T>) codeInsertHelper;
            }
        }
        throw new IllegalStateException("不存在的类型: " + psiElement.getClass().getSimpleName());
    }

}
