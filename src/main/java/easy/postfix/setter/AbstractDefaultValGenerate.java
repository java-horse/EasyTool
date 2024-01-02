package easy.postfix.setter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import easy.postfix.base.AbstractMethodListGenerate;
import easy.postfix.base.BaseVar;
import easy.postfix.util.BaseTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public abstract class AbstractDefaultValGenerate extends AbstractMethodListGenerate {

    private final boolean generateDefaultVal;

    protected AbstractDefaultValGenerate(boolean generateDefaultVal, BaseVar baseVar) {
        super(baseVar);
        this.generateDefaultVal = generateDefaultVal;
    }

    protected String generateDefaultVal(Project project, Set<String> newImportList, PsiParameter parameter) {
        String defaultVal = "";
        if (generateDefaultVal) {
            PsiType psiType = parameter.getType();
            String typeName = psiType.getPresentableText();
            String defaultVal0 = BaseTypeUtil.getJavaBaseTypeDefaultValStr(typeName);
            // 基础类型
            if (defaultVal0 != null) {
                defaultVal = defaultVal0;
            } else {
                if (psiType instanceof PsiClassReferenceType) {
                    PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiType;
                    PsiClass psiClass = psiClassReferenceType.resolve();
                    if (psiClass == null) {
                        return StringUtils.EMPTY;
                    }
                    String qualifiedName = psiClass.getQualifiedName();
                    String commonDefaultVal = BaseTypeUtil.getDefaultValStrByQname(qualifiedName);
                    if (commonDefaultVal != null) {
                        defaultVal = commonDefaultVal;
                        String commonDefaultValImport = BaseTypeUtil.getDefaultValImportByQname(qualifiedName);
                        if (commonDefaultValImport != null) {
                            newImportList.add(commonDefaultValImport);
                        }
                    } else {
                        Pair<String, String> pair = handlerByPsiType(newImportList, project, psiType, "%s", typeName);
                        String fieldType = pair.getLeft();
                        String right = pair.getRight();
                        defaultVal = "new " + fieldType + "()" + right;
                    }
                }
            }
        }
        return defaultVal;
    }

}
