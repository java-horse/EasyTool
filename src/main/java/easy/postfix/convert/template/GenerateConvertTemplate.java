package easy.postfix.convert.template;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.base.GenerateBaseTemplate;
import easy.postfix.convert.GenerateConvert;
import easy.postfix.convert.GenerateConvertForDst;
import easy.postfix.util.PsiExpressionUtil;

public class GenerateConvertTemplate extends GenerateBaseTemplate {

    public GenerateConvertTemplate() {
        super("convert", "Generate Convert by to.setXxx(from.getXxx)", GenerateConvertTemplate::isApplicable0);
    }

    private static boolean isApplicable0(PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        if (psiElement instanceof PsiIdentifier) {
            PsiElement parent = psiElement.getParent();
            if (parent instanceof PsiReferenceExpression) {
                PsiReferenceExpression expression = (PsiReferenceExpression) parent;
                if (expression.getType() != null) {
                    return true;
                }
            }
        }
        if (psiElement instanceof PsiExpression) {
            PsiExpression psiExpression = (PsiExpression) psiElement;
            PsiType psiType = psiExpression.getType();
            return psiType != null;
        }
        if (psiElement instanceof PsiJavaToken) {
            PsiJavaToken psiJavaToken = (PsiJavaToken) psiElement;
            IElementType tokenType = psiJavaToken.getTokenType();
            String tokenTypeName = tokenType.toString();
            switch (tokenTypeName) {
                case "SEMICOLON":
                    // 分号 ;
                    psiElement = psiElement.getPrevSibling();
                    break;
                case "RPARENTH":
                    // 右括号 )
                    PsiElement methodParamExpression = psiElement.getParent();
                    if (methodParamExpression != null) {
                        psiElement = methodParamExpression.getParent();
                    }
                    break;
                default:
                    break;
            }
        }
        if (psiElement instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression callExpression = (PsiMethodCallExpression) psiElement;
            PsiType psiTypeForSet = PsiExpressionUtil.getPsiTypeByMethodCallExpression(callExpression);
            PsiType psiTypeForGet = PsiExpressionUtil.getPsiTypeByFirstArgument(callExpression);
            return psiTypeForSet != null && psiTypeForGet != null;
        }
        return false;
    }

    @Override
    protected GenerateBase buildGenerate(PsiElement psiElement, PsiFile containingFile, PsiDocumentManager psiDocumentManager, Document document) {
        if (psiElement instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression callExpression = (PsiMethodCallExpression) psiElement;
            PsiType psiTypeForSet = PsiExpressionUtil.getPsiTypeByMethodCallExpression(callExpression);
            String nameForSet = PsiExpressionUtil.getNameByMethodCallExpression(callExpression);
            if (psiTypeForSet == null) {
                return null;
            }
            PsiType psiTypeForGet = PsiExpressionUtil.getPsiTypeByFirstArgument(callExpression);
            String nameForGet = PsiExpressionUtil.getNameByFirstArgument(callExpression);
            if (psiTypeForGet == null) {
                return null;
            }
            BaseVar varForSet = new BaseVar();
            varForSet.setVarName(nameForSet);
            varForSet.setVarType(psiTypeForSet);

            BaseVar varForGet = new BaseVar();
            varForGet.setVarName(nameForGet);
            varForGet.setVarType(psiTypeForGet);
            return new GenerateConvert(varForSet, varForGet);
        }
        if (psiElement instanceof PsiExpression) {
            PsiExpression psiExpression = (PsiExpression) psiElement;
            PsiType psiType = psiExpression.getType();
            String varName = psiExpression.getText();
            if (psiType != null) {
                BaseVar baseVar = new BaseVar();
                baseVar.setVarName(varName);
                baseVar.setVarType(psiType);
                return new GenerateConvertForDst(baseVar, null);
            }
        }
        return null;
    }


}