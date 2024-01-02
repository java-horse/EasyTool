package easy.postfix.setter.template;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.base.GenerateBaseTemplate;
import easy.postfix.setter.GenerateBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class GenerateAllSetterByBuilderTemplate extends GenerateBaseTemplate {

    private final boolean generateDefaultVal;

    public GenerateAllSetterByBuilderTemplate() {
        super("allbuilder", "Generate Builder",
                GenerateAllSetterByBuilderTemplate::isApplicable0,
                GenerateAllSetterByBuilderTemplate::getExpressions
        );
        this.generateDefaultVal = true;
    }

    private static Pair<String, PsiElement> getRightPsiElement(PsiElement psiElement) {
        String methodName = "";
        if (psiElement instanceof PsiJavaToken) {
            PsiJavaToken psiJavaToken = (PsiJavaToken) psiElement;
            IElementType tokenType = psiJavaToken.getTokenType();
            String tokenTypeName = tokenType.toString();
            // PsiReferenceExpression:  Xxx.builder
            PsiElement builderMethodReference = null;
            switch (tokenTypeName) {
                case "RPARENTH":
                    // 右括号 )
                    // ()
                    PsiElement methodParamExpression = psiElement.getParent();
                    if (methodParamExpression != null) {
                        builderMethodReference = methodParamExpression.getPrevSibling();
                    }
                    break;
                case "SEMICOLON":
                    // 分号 ;
                    PsiElement callExpressionElement = psiElement.getPrevSibling();
                    if (callExpressionElement != null) {
                        builderMethodReference = callExpressionElement.getFirstChild();
                    }
                    break;
                default:
                    break;
            }
            if (builderMethodReference != null) {
                // builder
                PsiElement methodIdentifier = builderMethodReference.getLastChild();
                if (methodIdentifier != null) {
                    methodName = methodIdentifier.getText();
                }
                // Xxx
                PsiElement builderClassReference = builderMethodReference.getFirstChild();
                if (builderClassReference != null) {
                    psiElement = builderClassReference;
                }
            }
        }
        return Pair.of(methodName, psiElement);
    }

    private static boolean isApplicable0(PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        Pair<String, PsiElement> rightPsiElement = getRightPsiElement(psiElement);
        String methodName = rightPsiElement.getLeft();
        psiElement = rightPsiElement.getRight();
        if ("builder".equals(methodName) && (psiElement instanceof PsiReferenceExpression)) {
            PsiReferenceExpression expression = (PsiReferenceExpression) psiElement;
            return StringUtils.isNotBlank(expression.getCanonicalText());

        }
        return false;
    }

    public static PsiElement getExpressions(PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }
        Pair<String, PsiElement> rightPsiElement = getRightPsiElement(psiElement);
        String methodName = rightPsiElement.getLeft();
        psiElement = rightPsiElement.getRight();
        if ("builder".equals(methodName) && (psiElement instanceof PsiReferenceExpression)) {
            PsiReferenceExpression expression = (PsiReferenceExpression) psiElement;
            return psiElement;
        }
        return null;
    }

    @Override
    protected GenerateBase buildGenerate(PsiElement psiElement, PsiFile containingFile, PsiDocumentManager psiDocumentManager, Document document) {
        Project project = psiElement.getProject();
        if (psiElement instanceof PsiMethodCallExpression) {
            psiElement = psiElement.getFirstChild().getFirstChild();
        }
        if (psiElement instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) psiElement;
            String canonicalText = expression.getCanonicalText();
            PsiClassType psiType = PsiType.getTypeByName(canonicalText, project, GlobalSearchScope.allScope(project));
            BaseVar baseVar = new BaseVar();
            baseVar.setVarName(expression.getText());
            baseVar.setVarType(psiType);
            return new GenerateBuilder(generateDefaultVal, baseVar);
        }
        return null;
    }


}
