package easy.doc.variable.impl;

import com.intellij.psi.PsiElement;
import easy.util.VcsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class VcsBranchVariableGeneratorImpl extends AbstractVariableGeneratorImpl {
    @Override
    public String generate(PsiElement element) {
        if (Objects.isNull(element)) {
            return StringUtils.EMPTY;
        }
        return VcsUtil.getCurrentBranch(element.getProject());
    }

}
