package easy.postfix.base;

import com.intellij.psi.PsiType;

public class BaseVar {
    private String varName;
    private PsiType varType;

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public PsiType getVarType() {
        return varType;
    }

    public void setVarType(PsiType varType) {
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "BaseVar{" +
                "varName='" + varName + '\'' +
                ", varType=" + varType +
                '}';
    }

}
