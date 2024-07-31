package easy.doc.variable.impl;

import com.intellij.psi.PsiElement;
import com.intellij.vcs.log.VcsUser;
import easy.util.VcsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class VcsUserVariableGeneratorImpl extends AbstractVariableGeneratorImpl{
    @Override
    public String generate(PsiElement element) {
        if (Objects.isNull(element)) {
            return StringUtils.EMPTY;
        }
        VcsUser vcsUser = VcsUtil.getVcsUser(element.getProject());
        if (Objects.isNull(vcsUser)) {
            return StringUtils.EMPTY;
        }
        return vcsUser.getName();
    }

}
