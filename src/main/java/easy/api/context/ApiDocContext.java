package easy.api.context;


import com.intellij.psi.PsiElement;

public class ApiDocContext {

    /**
     * 目标元素(触发当前事件时鼠标在java类中的节点)
     */
    private PsiElement targetElement;


    public PsiElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(PsiElement targetElement) {
        this.targetElement = targetElement;
    }

}
