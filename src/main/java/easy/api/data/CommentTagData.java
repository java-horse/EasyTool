package easy.api.data;

import com.intellij.psi.PsiElement;

public class CommentTagData {

    /**
     * 参数名
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 节点对象
     */
    private PsiElement psiElement;

    public CommentTagData() {
    }

    public CommentTagData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }
}
