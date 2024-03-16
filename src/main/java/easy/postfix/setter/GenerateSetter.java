package easy.postfix.setter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import easy.postfix.base.BaseVar;
import easy.postfix.util.PsiMethodUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class GenerateSetter extends AbstractDefaultValGenerate {

    private final boolean noSupperClass;

    public GenerateSetter(boolean generateDefaultVal, BaseVar baseVar) {
        this(generateDefaultVal, baseVar, false);
    }

    public GenerateSetter(boolean generateDefaultVal, BaseVar baseVar, boolean noSupperClass) {
        super(generateDefaultVal, baseVar);
        this.noSupperClass = noSupperClass;
    }

    @Override
    @NotNull
    public List<PsiMethod> getGenerateMethodListByClass(PsiClass psiClass) {
        return PsiMethodUtil.getSetterMethod(psiClass, noSupperClass);
    }

    @Override
    @NotNull
    public String generateCodeByMethod(Set<String> newImportList, PsiMethod method) {
        if (baseVar == null) {
            return StringUtils.EMPTY;
        }
        Project project = method.getProject();
        PsiParameterList parameterList = method.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        if (parameters.length > 0) {
            PsiParameter parameter = parameters[0];
            String methodName = method.getName();
            String defaultVal = generateDefaultVal(project, newImportList, parameter);
            return baseVar.getVarName() + "." + methodName + "(" + defaultVal + ");";
        } else {
            return StringUtils.EMPTY;
        }
    }

}
