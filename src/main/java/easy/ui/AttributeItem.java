package easy.ui;

import com.intellij.psi.PsiElement;

import javax.swing.*;

/**
 * 公共UI属性项
 *
 * @author mabin
 * @project EasyTool
 * @package easy.ui
 * @date 2024/06/01 09:38
 */
public class AttributeItem {
    /**
     * 展示名称
     */
    private String name;
    /**
     * 类、方法、属性的QualifiedName名称
     */
    private String realName;
    /**
     * 图标
     */
    private Icon icon;
    /**
     * 是否选中
     */
    private boolean selected;
    /**
     * 类、方法、属性的对象
     */
    private PsiElement psiElement;

    public AttributeItem(String name, String realName, Icon icon, PsiElement psiElement, boolean selected) {
        this.name = name;
        this.realName = realName;
        this.icon = icon;
        this.psiElement = psiElement;
        this.selected = selected;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }
}
